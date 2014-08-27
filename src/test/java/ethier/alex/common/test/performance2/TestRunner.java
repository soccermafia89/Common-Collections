/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance2;

import com.google.common.base.Stopwatch;
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
    
    private NumOperations dataSize;
    private double insertRatio;
    private double mutateRatio;
    private int numTraversals;
    private int numRandomAccesses;
            
    
    public TestRunner(Collection<Class> testListClasses, NumOperations myDataSize, 
                                            double myInsertRatio, double myMutateRatio,
                                            int myNumTraversals, int myNumRandomAccesses) {
        
        listClasses = new ArrayList<Class>();
        listClasses.addAll(testListClasses);
        Collections.shuffle(listClasses);
        
        dataSize = myDataSize;
        insertRatio = myInsertRatio;
        mutateRatio = myMutateRatio;
        numTraversals = myNumTraversals;
        numRandomAccesses = myNumRandomAccesses;
    }
    
    public Map<Class, Long> runTests() throws InstantiationException, IllegalAccessException {
        
        System.out.println("TODO: Since random may take a while to compute. pre-compute random values in an array.");
        
        Map<Class, Long> testResults = new HashMap<Class, Long>();
        randomSeed = (long) (Math.random()*Long.MAX_VALUE);
        
        for(Class clazz : listClasses) {
            Random random = new Random(randomSeed);
                       
            Runtime.getRuntime().gc();
            Stopwatch stopwatch = Stopwatch.createStarted();
            this.runPerformanceTest(random, clazz);
            
            Runtime.getRuntime().gc();

            
            stopwatch.stop();
            long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            logger.info("List: {} took {} millis.", clazz.getCanonicalName(), elapsedMillis);
            
            testResults.put(clazz, elapsedMillis); // Hopefully this growing in memory object won't unfairly affect tests.
        }
        
        return testResults;
    }
    
    private void runPerformanceTest(Random random, Class clazz) throws InstantiationException, IllegalAccessException {
        
        logger.info("Performance test underway for class {} with {} operations.", clazz.getCanonicalName(), dataSize.getNumOperations());
        double total = 0.0D;
        
        List listClazz = (List) clazz.newInstance();
        
        int count = 0;
        while(count < dataSize.getNumOperations()) {
//            logger.info("Count: {}", count);
            double picker = random.nextDouble();
            
            if(picker < insertRatio) {
                int insertPoint = random.nextInt(listClazz.size());
                
                listClazz.add(insertPoint, random.nextDouble());
                count++;
            }
            
            if(picker < mutateRatio) {
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
        
        logger.trace("Finished writing to list.");
        
        for(int i=0; i < numTraversals; i++) {
            Iterator it = listClazz.iterator();
            while(it.hasNext()) {
                total += (Double) it.next();
            }
        }
        
        int listClassSize = listClazz.size();
        for(int i=0; i < numRandomAccesses; i++) {
            total += (Double) listClazz.get(random.nextInt(listClassSize));
        }
        
        logger.trace("Finished reading list.");
        
        logger.trace(total);
    }
}
