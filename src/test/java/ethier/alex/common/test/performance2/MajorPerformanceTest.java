package ethier.alex.common.test.performance2;

import ethier.alex.common.list.ArrayLinkList;
import ethier.alex.common.list.CompactionList;
import ethier.alex.common.list.MutationList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
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
    public void testLargeCollection() throws InstantiationException, IllegalAccessException {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********   Large Collection Test   *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
        Collection<Class> testListClasses = new ArrayList<Class>();
        testListClasses.add(ArrayList.class);
        testListClasses.add(LinkedList.class);
        testListClasses.add(CompactionList.class);
        testListClasses.add(ArrayLinkList.class);
        testListClasses.add(MutationList.class);
        
        int rounds = 5;
        int sets = 100;
        
        ResultCompiler resultCompiler = new ResultCompiler(testListClasses, rounds, sets);
        
        /*
        getRank(NumOperations numOperations, 
                                     double insertRatio, double mutateRatio, 
                                     int numTraversals, int numRandomAccesses)
        */
        
        Map<Class, Double> rankedResults = resultCompiler.getRank(NumOperations.MEDIUM, 0.15, 0, 5, NumOperations.SMALL.getNumOperations());
        
        System.out.println("");
        System.out.println("");
        System.out.println("Results:");
        System.out.println("");
        for(Class clazz : rankedResults.keySet()) {
            System.out.println(clazz.getCanonicalName() + " was ranked " + rankedResults.get(clazz));
        }
    }
    
}
