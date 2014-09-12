/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance2;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import ethier.alex.world.metrics.MetricFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class TestRunner {
    
    private static Logger logger = LogManager.getLogger(TestRunner.class);
        
    private long randomSeed;
    private List<Class> listClasses;
    
    private int numOperations;
    private double insertRatio;
    private double mutateRatio;
    private int numTraversals;
    private int numRandomAccesses;
    private boolean includeGC;
            
    
    public TestRunner(Collection<Class> testListClasses, int numOperations, 
                                            double myInsertRatio, double myMutateRatio,
                                            int myNumTraversals, int myNumRandomAccesses, boolean includeGCbool) {
        
        listClasses = new ArrayList<Class>();
        listClasses.addAll(testListClasses);
        Collections.shuffle(listClasses);
        
        this.numOperations = numOperations;
        insertRatio = myInsertRatio;
        mutateRatio = myMutateRatio;
        numTraversals = myNumTraversals;
        numRandomAccesses = myNumRandomAccesses;
        includeGC = includeGCbool;
    }
    
    public Map<Class, Long> runTests() throws InstantiationException, IllegalAccessException {
                
        Map<Class, Long> testResults = new HashMap<Class, Long>();
        randomSeed = (long) (Math.random()*Long.MAX_VALUE);
        Collections.shuffle(listClasses);
        
        for(Class clazz : listClasses) {
            try {
                BufferedRandom random = new BufferedRandom(randomSeed, numOperations, numOperations);

                Runtime.getRuntime().gc();

                Stopwatch stopwatch = Stopwatch.createStarted();
                this.runPerformanceTest(random, clazz);
                if(includeGC) {
                    Runtime.getRuntime().gc();            
                }
                stopwatch.stop();                       
    //            long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                long elapsedNanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
                MetricFactory.INSTANCE.getTimer().updateTimer(clazz.getCanonicalName() + StaticTestVariables.TOTAL_TIMER_BASE_KEY, elapsedNanos, TimeUnit.NANOSECONDS);
                logger.trace("List: {} took {} nanos.", clazz.getCanonicalName(), elapsedNanos);

                testResults.put(clazz, elapsedNanos); // Hopefully this growing in memory object won't unfairly affect tests.
            } catch (Exception e) {
                logger.error("Class: {} failed during testing.", clazz.getCanonicalName());
                Throwables.propagate(e);
            }
        }
        
        return testResults;
    }
    
    private void runPerformanceTest(BufferedRandom random, Class clazz) throws InstantiationException, IllegalAccessException {
//        MetricFactory.INSTANCE.getTimer().continueTimer(StaticTestVariables.EXTRA_TIMER);
//        logger.trace("Performance test underway for class {} with {} operations.", clazz.getCanonicalName(), numOperations);
        double total = 0.0D;
        
        List listClazz = (List) clazz.newInstance();
        
        int count = 0;
        while(count < numOperations) {
//            logger.info("Count: {}", count);
            double picker = random.nextDouble();
            
            if(picker < insertRatio) {
                int insertPoint = random.nextInt(listClazz.size());
//                MetricFactory.INSTANCE.getTimer().stopTimer(StaticTestVariables.EXTRA_TIMER);
                listClazz.add(insertPoint, random.nextDouble());
//                MetricFactory.INSTANCE.getTimer().continueTimer(StaticTestVariables.EXTRA_TIMER);
                count++;
            }
            
            if(picker < mutateRatio && listClazz.size() > 0) {
                int secondPicker = random.nextInt(2);
                if(listClazz.isEmpty()) {
                    secondPicker = 0;
                }
                int mutateIndex = random.nextInt(listClazz.size());

                
                if(secondPicker == 0) { // Insert
                    listClazz.add(mutateIndex, random.nextDouble());
                    count++;
                } else { // Remove
                    listClazz.remove(mutateIndex);
                }
                
                count++;
            }
            
            listClazz.add(random.nextDouble());
            count++;
        }
        
//        logger.trace("Finished writing to list.");
//        MetricFactory.INSTANCE.getTimer().stopTimer(StaticTestVariables.EXTRA_TIMER);

        for(int i=0; i < numTraversals; i++) {

            Iterator it = listClazz.iterator();
            while(it.hasNext()) {
                total += (Double) it.next();
            }
        }
//        MetricFactory.INSTANCE.getTimer().continueTimer(StaticTestVariables.EXTRA_TIMER);

        
        int listClassSize = listClazz.size();
        for(int i=0; i < numRandomAccesses; i++) {
            total += (Double) listClazz.get(random.nextInt(listClassSize -1));
        }
        
        logger.trace(total);
//        MetricFactory.INSTANCE.getTimer().stopTimer(StaticTestVariables.EXTRA_TIMER);
    }
}
