package ethier.alex.common.test.functionality;

import ethier.alex.common.list.ArrayLinkList;
import ethier.alex.common.list.CompactionList;
import ethier.alex.common.list.MutationList;
import ethier.alex.common.test.performance.DataGenerator;
import java.util.*;
import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**

 Tests validating operation of new data structures.

 @author alex
 */
public class FunctionalityTest {

    // TEST CASE TODOS:
    // Fail fast under concurrent modification.
    // addAll (both cases)
    private static Logger logger = LogManager.getLogger(FunctionalityTest.class);

    @BeforeClass
    public static void setUpClass() {
//        BasicConfigurator.configure();
    }

    private void checkAddIndividually(Object[] values, List list) {
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            Assert.assertTrue(list.add(value));
        }
    }

    private void checkIteration(Object[] values, List list) {
        Iterator<Object> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            Object value = it.next();
            
            if(value == null) {
                System.out.println("Content: " + Arrays.toString(list.toArray()));
                System.out.println("Size: " + list.size());
            }
            
            if(values == null) {
                System.out.println("Values is null");
            }
            
            if(values[count] == null) {
                System.out.println("Values count is null at: " + count);
            }
            
            if(!value.equals(values[count])) {
                System.out.println("Control value=" + values[count] + " list value=" + value);
            }

            Assert.assertTrue(value.equals(values[count]));
            count++;
        }

        Assert.assertTrue(values.length == count);
    }

    private void checkRandomAccess(Object[] values, List list) {
        for(int i = 0; i < values.length; i++) {
            int randAccessPoint = (int) (Math.random() * values.length);
            Assert.assertTrue(list.get(randAccessPoint).equals(values[randAccessPoint]));
        }
    }
    
    // Returns the control list that should be identical.
    private List<Object> checkInsert(Object[] values, List list) {
        
        List controlList = new ArrayList();
        
//        for(int i=0; i<values.length; i++) {
//            list.add(i, values[i]);
//            controlList.add(i, values[i]);
//        }
        
        for(int i=0; i<values.length; i++) {
            int randInsertPoint = (int) (Math.random() * controlList.size());
            list.add(randInsertPoint, values[i]);
            controlList.add(randInsertPoint, values[i]);
        }
        
        return controlList;
    }
    
    private List<Object> checkRemoval(List list, int num) {
        List controlList = new ArrayList();
        controlList.addAll(list);
        
        for(int i=0; i<num; i++) {
            int randRemovePoint = (int) (Math.random() * controlList.size());
            controlList.remove(randRemovePoint);
//            System.out.println("Control removed: " + controlRemoved);
            list.remove(randRemovePoint);
        }
        
        return controlList;
    }

    private void checkIndexOf(Object[] uniqueValues, List list) {

        for (int i = 0; i < 50; i++) {
            int randAccessPoint = (int) (Math.random() * uniqueValues.length);
            Object randomObject = uniqueValues[randAccessPoint];

            Assert.assertTrue(list.indexOf(randomObject) == randAccessPoint);
        }
    }
    
    private void checkToArray(Object[] values, List list) {
        Object[] listArray = list.toArray();
        for(int i=0; i<listArray.length; i++) {
            Assert.assertTrue(listArray[i].equals(values[i]));
        }
        
        Assert.assertTrue(values.length == listArray.length);
    }

    @Test
    public void testLists() throws InstantiationException, IllegalAccessException, Exception {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********    Ethier Array Test     *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");

        List<Class> listClasses = new ArrayList();
        listClasses.add(ArrayList.class);
        listClasses.add(ArrayLinkList.class);
        listClasses.add(CompactionList.class);
        listClasses.add(MutationList.class);
//        listClasses.add(ControlArrayList.class);

//        int largeSize = 10000;
        int largeSize = 10;
        DataGenerator dataGenerator = new DataGenerator();
//        Object[] values = dataGenerator.getDoubles(largeSize);
        Object[] largeValues = dataGenerator.getIntegers(largeSize, 10);

        int smallSize = 2;
        Object[] smallValues = dataGenerator.getDoubles(smallSize);
        
        int mediumSize = 10;
        Object[] mediumValues = dataGenerator.getIntegers(mediumSize, 10);

        Set<Object> uniqueSet = new HashSet<Object>();
        uniqueSet.addAll(Arrays.asList(largeValues));
        Object[] unqiueValues = uniqueSet.toArray();

        for (Class listClass : listClasses) {

            try {
                logger.info("Testing Functionality for: {}", listClass.getCanonicalName());

                List testList = (List) listClass.newInstance();

                logger.info("Testing add.");
                this.checkAddIndividually(largeValues, testList);
                logger.info("Testing traversal.");
                this.checkIteration(largeValues, testList);
                logger.info("Testing random access.");
                this.checkRandomAccess(largeValues, testList);
                this.checkToArray(largeValues, testList);

                List uniqueTestList = (List) listClass.newInstance();
                this.checkAddIndividually(unqiueValues, uniqueTestList);
                logger.info("Testing index of.");
                this.checkIndexOf(unqiueValues, uniqueTestList);

                logger.info("Testing small list corner case.");
                List smallTestList = (List) listClass.newInstance();
                this.checkAddIndividually(smallValues, smallTestList);
                this.checkIteration(smallValues, smallTestList);
                this.checkRandomAccess(smallValues, smallTestList);
                this.checkToArray(smallValues, smallTestList);
                
                logger.info("Testing insertion.");
                List testInsertList = (List) listClass.newInstance();
                List controlList = this.checkInsert(mediumValues, testInsertList);
                logger.info("Iterating post insertion.");
                this.checkIteration(controlList.toArray(), testInsertList);
                
                logger.info("Testing removal.");
                List removeList = (List) listClass.newInstance();
                this.checkAddIndividually(mediumValues, removeList);
                controlList = this.checkRemoval(removeList, (mediumSize/2));
                this.checkIteration(controlList.toArray(), removeList);
            } catch (Exception e) {
                logger.error("List class: {} failed test.", listClass.getCanonicalName());
                throw(e);
            }
        }
    }
}
