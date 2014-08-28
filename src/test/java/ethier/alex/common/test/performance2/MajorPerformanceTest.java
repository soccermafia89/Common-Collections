package ethier.alex.common.test.performance2;

import ethier.alex.common.list.ArrayLinkList;
import ethier.alex.common.list.CompactionList;
import ethier.alex.common.list.MutationList;
import ethier.alex.world.metrics.MetricFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**

 Tests validating operation and performance of new data structures.

 @author alex
 */
public class MajorPerformanceTest {


    private static Logger logger = LogManager.getLogger(MajorPerformanceTest.class);

    @BeforeClass
    public static void setUpClass() {
//        BasicConfigurator.configure();
    }

    @Test
    public void testPerformance() throws InstantiationException, IllegalAccessException {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********   Performance Test   *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
        MetricFactory.INSTANCE.setLevel(Level.INFO);
        
        Collection<Class> testListClasses = new ArrayList<Class>();
        testListClasses.add(ArrayList.class);
        testListClasses.add(LinkedList.class);
        testListClasses.add(CompactionList.class);
        testListClasses.add(ArrayLinkList.class);
        testListClasses.add(MutationList.class);
        
        int rounds = 7;
        int sets = 100;
        
        ResultCompiler resultCompiler = new ResultCompiler(rounds, sets);
        
//        TestRunner testRunner = MajorPerformanceTest.createSingleWriteSingleReadTest(testListClasses);
//        TestRunner testRunner = MajorPerformanceTest.createSingleWriteRandomReadTest(testListClasses);
        TestRunner testRunner = MajorPerformanceTest.createInsertionTest(testListClasses);
                
        Map<Class, Double> rankedResults = resultCompiler.getRank(testRunner);
        
        System.out.println("");
        System.out.println("");
        System.out.println("Results:");
        System.out.println("");
        for(Class clazz : rankedResults.keySet()) {
            System.out.println(clazz.getCanonicalName() + " was ranked " + rankedResults.get(clazz));
        }
        
        System.out.println("");
        MetricFactory.INSTANCE.printAll();
    }
    
    private static TestRunner createInsertionTest(Collection<Class> testListClasses) {
        TestRunner testRunner = new TestRunner(testListClasses, StaticTestVariables.MEDIUM_DATA, 
                            1, 0,
                            1, 0);
        
        return testRunner;
    } 
    
    private static TestRunner createSingleWriteRandomReadTest(Collection<Class> testListClasses) {
        TestRunner testRunner = new TestRunner(testListClasses, StaticTestVariables.MEDIUM_DATA, 
                            0, 0,
                            1, StaticTestVariables.MEDIUM_DATA);
        
        return testRunner;
    } 
    
    private static TestRunner createSingleWriteSingleReadTest(Collection<Class> testListClasses) {
        TestRunner testRunner = new TestRunner(testListClasses, StaticTestVariables.LARGE_DATA, 
                            0, 0,
                            1, 0);
        
        return testRunner;
    } 
}
