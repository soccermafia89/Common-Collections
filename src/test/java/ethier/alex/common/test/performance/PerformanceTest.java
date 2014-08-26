package ethier.alex.common.test.performance;

import com.google.common.base.Stopwatch;
import ethier.alex.common.list.ArrayLinkList;
import ethier.alex.common.list.CompactionList;
import ethier.alex.common.list.ControlArrayList;
import ethier.alex.common.list.StaticControlConfigurator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.github.jamm.MemoryMeter;
import org.junit.BeforeClass;
import org.junit.Test;

/**

 Tests validating operation and performance of new data structures.

 @author alex
 */
public class PerformanceTest {

    private static Logger logger = LogManager.getLogger(PerformanceTest.class);

    @BeforeClass
    public static void setUpClass() {
//        BasicConfigurator.configure();
    }

    @Test
    public void testLargeCollection() {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********   Large Collection Test   *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
//        System.out.println("Test memory measurer.");
//        long bytes = MemoryMeasurer.measureBytes("test");
//        System.out.println("Test size: " + bytes);
//        MemoryMeter meter = new MemoryMeter();


        for (int round = 0; round < 2; round++) {

            int size = 20000000;
            RandomDataGenerator dataGenerator = new RandomDataGenerator();
            Double[] values = dataGenerator.getDoubles(size);
            Stopwatch methodTimer = Stopwatch.createUnstarted();
            Stopwatch collectionTimer = Stopwatch.createUnstarted();
            double total = 0;

            List<Collection> collections = new ArrayList();

            Collection arrayList = new ArrayList();
            Collection arrayDeque = new ArrayDeque();
            Collection arrayLinkList = new ArrayLinkList();
            Collection controlList = new ControlArrayList(size);
//            Collection multiset = HashMultiset.create();
//            Collection googleArrayList = Lists.newArrayList();
            Collection linkedList = new LinkedList();
            Collection compactionList = new CompactionList();

            collections.add(arrayDeque);
            collections.add(arrayList);
            collections.add(arrayLinkList);
            collections.add(controlList);
//            collections.add(multiset); // Takes way too long.
//            collections.add(googleArrayList); // Wrapper for ArrayList
            collections.add(linkedList);
            collections.add(compactionList);


            Collections.shuffle(collections);
            Iterator<Collection> it = collections.iterator();
            while (it.hasNext()) {
                Collection collection = it.next();
                collectionTimer.start();
                methodTimer.start();

                // Test collection creation and entry.
                for (int i = 0; i < values.length; i++) {
                    double value = values[i];
                    collection.add(value);
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to enter {} entries.", collection.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();
                methodTimer.start();

                // Test collection traversal
                Iterator<Double> valueIterator = collection.iterator();
                while (valueIterator.hasNext()) {
                    double value = valueIterator.next();
                    total += value;
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to traverse {} entries.", collection.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();


                collectionTimer.stop();
                logger.info("{} took {} milliseconds total.", collection.getClass().getCanonicalName(), collectionTimer.elapsed(TimeUnit.MILLISECONDS));
                collectionTimer.reset();
                
//                Stopwatch memoryTimer = Stopwatch.createStarted();
//                long byteUsage = meter.measureDeep(collection);
//                memoryTimer.stop();
//                logger.info("Used memory: {}, took {} milliseconds to compute.", byteUsage, memoryTimer.elapsed(TimeUnit.MILLISECONDS));
//                memoryTimer.reset();
                
                it.remove();
                Runtime.getRuntime().gc();
                logger.info("");
            }

            logger.trace("Total: {}", total);
        }
    }
    
    @Test
    public void testLargeMemory() {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********     Large Memory Test     *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");
        
//        System.out.println("Test memory measurer.");
//        long bytes = MemoryMeasurer.measureBytes("test");
//        System.out.println("Test size: " + bytes);
        MemoryMeter meter = new MemoryMeter();


        for (int round = 0; round < 2; round++) {

            int size = 2000000;
            RandomDataGenerator dataGenerator = new RandomDataGenerator();
            Double[] values = dataGenerator.getDoubles(size);
            Stopwatch methodTimer = Stopwatch.createUnstarted();
            Stopwatch collectionTimer = Stopwatch.createUnstarted();
            double total = 0;

            List<Collection> collections = new ArrayList();

            Collection arrayList = new ArrayList();
            Collection arrayDeque = new ArrayDeque();
            Collection arrayLinkList = new ArrayLinkList();
            Collection controlList = new ControlArrayList(size);
//            Collection multiset = HashMultiset.create();
//            Collection googleArrayList = Lists.newArrayList();
            Collection linkedList = new LinkedList();
            Collection compactionList = new CompactionList();

            collections.add(arrayDeque);
            collections.add(arrayList);
            collections.add(arrayLinkList);
            collections.add(controlList);
//            collections.add(multiset); // Takes way too long.
//            collections.add(googleArrayList); // Wrapper for ArrayList
            collections.add(linkedList);
            collections.add(compactionList);


            Collections.shuffle(collections);
            Iterator<Collection> it = collections.iterator();
            while (it.hasNext()) {
                Collection collection = it.next();
                collectionTimer.start();
                methodTimer.start();

                // Test collection creation and entry.
                for (int i = 0; i < values.length; i++) {
                    double value = values[i];
                    collection.add(value);
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to enter {} entries.", collection.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();
                methodTimer.start();

                // Test collection traversal
                Iterator<Double> valueIterator = collection.iterator();
                while (valueIterator.hasNext()) {
                    double value = valueIterator.next();
                    total += value;
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to traverse {} entries.", collection.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();


                collectionTimer.stop();
                logger.info("{} took {} milliseconds total.", collection.getClass().getCanonicalName(), collectionTimer.elapsed(TimeUnit.MILLISECONDS));
                collectionTimer.reset();
                
                Stopwatch memoryTimer = Stopwatch.createStarted();
                long byteUsage = meter.measureDeep(collection);
                memoryTimer.stop();
                logger.info("Used memory: {}, took {} milliseconds to compute.", byteUsage, memoryTimer.elapsed(TimeUnit.MILLISECONDS));
                memoryTimer.reset();
                
                it.remove();
                Runtime.getRuntime().gc();
                logger.info("");
            }

            logger.trace("Total: {}", total);
        }
    }

    @Test
    public void testSmallCollection() throws InstantiationException, IllegalAccessException {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********   Small Collection Test   *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");

        // Store collections and data in an array of size 1000 or so.
        // Have each collection traverse data under a single timer.
        // Tear down all collections when finished.

        //TODO: other tests
        /*

         -Large object
         -Many reads
         -Many read/writes
         -Base test (try an average of everything.)

         TODO: have iterators fail fast by having a 'modCount' variable that is incremented upon any change.

         */

        for (int round = 0; round < 2; round++) {

            double total = 0;
            int size = 200;
            int numCollections = 100000;

            RandomDataGenerator dataGenerator = new RandomDataGenerator();
            Double[][] data = new Double[numCollections][size];
            for (int i = 0; i < numCollections; i++) {

                Double[] values = dataGenerator.getDoubles(size);
                data[i] = values;
            }

            List<Class> collectionClasses = new ArrayList();

            collectionClasses.add(ArrayList.class);
            collectionClasses.add(ArrayDeque.class);
            collectionClasses.add(ArrayLinkList.class);
            collectionClasses.add(LinkedList.class);
            collectionClasses.add(CompactionList.class);
            collectionClasses.add(ControlArrayList.class);
            StaticControlConfigurator.setSize(size); // Used to configure the control array list.

            Collections.shuffle(collectionClasses);


            Stopwatch writeTimer = Stopwatch.createUnstarted();
            Stopwatch traversalTimer = Stopwatch.createUnstarted();
            Stopwatch totalTimer = Stopwatch.createUnstarted();

            for (Class collectionClass : collectionClasses) {

                totalTimer.start();
                writeTimer.start();
//                this.addCollection(collectionClass, collections, size);
                Collection[] collectionArray = new Collection[numCollections];

                for (int i = 0; i < numCollections; i++) {
                    Collection newCollection = (Collection) collectionClass.newInstance();
                    Double[] values = data[i];
                    for (int j = 0; j < values.length; j++) {
                        double value = values[j];
                        newCollection.add(value);
//                        logger.info("Adding {} to {}.", value, collectionClass.getCanonicalName());
                    }

                    collectionArray[i] = newCollection;
                }

                writeTimer.stop();

                traversalTimer.start();
                // Test collection traversal
                for (int i = 0; i < numCollections; i++) {
                    Collection collection = collectionArray[i];
                    Iterator<Double> valueIterator = collection.iterator();
                    while (valueIterator.hasNext()) {
                        double value = valueIterator.next();
//                        logger.info("Read {} from {}.", value, collectionClass.getCanonicalName());
                        total += value;
                    }
                }
                traversalTimer.stop();
                totalTimer.stop();

                logger.info("TODO: test collection add all method.");


                logger.info("{} took {} milliseconds to write {} entries in {} collections.", collectionClass.newInstance().getClass().getCanonicalName(), writeTimer.elapsed(TimeUnit.MILLISECONDS), size, numCollections);
                logger.info("{} took {} milliseconds to traverse {} entries in {} collections.", collectionClass.newInstance().getClass().getCanonicalName(), traversalTimer.elapsed(TimeUnit.MILLISECONDS), size, numCollections);
                logger.info("{} took {} milliseconds total.", collectionClass.newInstance().getClass().getCanonicalName(), totalTimer.elapsed(TimeUnit.MILLISECONDS));
                logger.info("");

                collectionArray = null;
                totalTimer.reset();
                traversalTimer.reset();
                writeTimer.reset();
                Runtime.getRuntime().gc();
            }

            logger.trace("Total: {}", total);
        }
    }

    @Test
    public void testReadCollection() {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********     Access List Test      *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");


        for (int round = 0; round < 2; round++) {

            int size = 1000000;
            RandomDataGenerator dataGenerator = new RandomDataGenerator();
            Double[] values = dataGenerator.getDoubles(size);
            Integer[] randomAccessPoints = dataGenerator.getIntegers(10 * size, size);
            Stopwatch methodTimer = Stopwatch.createUnstarted();
            Stopwatch collectionTimer = Stopwatch.createUnstarted();
            double total = 0;

            List<List> collections = new ArrayList();

            List arrayList = new ArrayList();
//            List arrayDeque = new ArrayDeque();
            List arrayLinkList = new ArrayLinkList();
//            Collection multiset = HashMultiset.create();
//            List linkedList = new LinkedList(); // Linked list too slow
            List compactionList = new CompactionList();
            List controlArrayList = new ControlArrayList(size);

//            collections.add(arrayDeque);
            collections.add(arrayList);
            collections.add(arrayLinkList);// Borderline too slow
//            collections.add(multiset);
//            collections.add(linkedList); // Takes way too long.
            collections.add(compactionList);
            collections.add(controlArrayList);

            Collections.shuffle(collections);
            Iterator<List> it = collections.iterator();
            while (it.hasNext()) {
                List<Double> list = it.next();
                collectionTimer.start();
                methodTimer.start();

                // Test collection creation and entry.
                for (int i = 0; i < values.length; i++) {
                    double value = values[i];
                    list.add(value);
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to enter {} entries.", list.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();
                methodTimer.start();
                
                //Memory measure test
//                Footprint footPrint = ObjectGraphMeasurer.measure(list);
                //TODO
                //END

                // Test collection traversal
                Iterator<Double> valueIterator = list.iterator();
                while (valueIterator.hasNext()) {
                    double value = valueIterator.next();
                    total += value;
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to traverse {} entries.", list.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();
                methodTimer.start();


                // Test collection random access
                for (int i = 0; i < randomAccessPoints.length; i++) {
                    int randomAccessPoint = randomAccessPoints[i];
                    double value = list.get(randomAccessPoint);
                    total += value;
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to random access {} entries.", list.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), randomAccessPoints.length);
                methodTimer.reset();
                methodTimer.start();

                // Test collection traversal once more
                valueIterator = list.iterator();
                while (valueIterator.hasNext()) {
                    double value = valueIterator.next();
                    total += value;
                }
                methodTimer.stop();
                logger.info("{} took {} milliseconds to traverse {} entries.", list.getClass().getCanonicalName(), methodTimer.elapsed(TimeUnit.MILLISECONDS), size);
                methodTimer.reset();


                collectionTimer.stop();
                logger.info("{} took {} milliseconds total.", list.getClass().getCanonicalName(), collectionTimer.elapsed(TimeUnit.MILLISECONDS));
                collectionTimer.reset();

                it.remove();
                Runtime.getRuntime().gc();
                logger.info("");
            }

            logger.trace("Total: {}", total);
        }
    }

    @Test
    public void testObjectCollection() throws InstantiationException, IllegalAccessException {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********  Object Collection Test   *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");


        for (int round = 0; round < 2; round++) {

            double total = 0;
            int size = 20000;
            int objectSize = 20000;

            RandomDataGenerator dataGenerator = new RandomDataGenerator();
            BigObject[] values = dataGenerator.getBigObjects(size, objectSize);
//            List<BigObject> shuffledValues = new ControlArrayList(size);
            List<BigObject> shuffledValues = new ArrayList(size);
            shuffledValues.addAll(Arrays.asList(values));
            Collections.shuffle(shuffledValues);

            List<Class> listClasses = new ArrayList();

            listClasses.add(ArrayList.class);
            listClasses.add(ArrayLinkList.class);
            listClasses.add(LinkedList.class);
            listClasses.add(CompactionList.class);
            listClasses.add(ControlArrayList.class);
            StaticControlConfigurator.setSize(size); // Used to configure the control array list.

            Collections.shuffle(listClasses);


            Stopwatch writeTimer = Stopwatch.createUnstarted();
            Stopwatch traversalTimer = Stopwatch.createUnstarted();
            Stopwatch findTimer = Stopwatch.createUnstarted();
            Stopwatch totalTimer = Stopwatch.createUnstarted();

            for (Class listClass : listClasses) {
                
                logger.info("Testing {}", listClass.newInstance().getClass().getCanonicalName());
                
                totalTimer.start();
                writeTimer.start();
                // test collection write.
                List newList = (List) listClass.newInstance();
                for (int j = 0; j < values.length; j++) {
                    BigObject value = values[j];
                    newList.add(value);
                }
                writeTimer.stop();

                traversalTimer.start();
                // Test collection traversal
                Iterator<BigObject> valueIterator = newList.iterator();
                while (valueIterator.hasNext()) {
                    BigObject value = valueIterator.next();
                    total += value.getId();
                }
                traversalTimer.stop();

                findTimer.start();
                Iterator<BigObject> accessIterator = shuffledValues.iterator();
                while(accessIterator.hasNext()) {
                    BigObject searchObject = accessIterator.next();
                    int index = newList.indexOf(searchObject);
                    total += index;
                }
                findTimer.stop();
                totalTimer.stop();


                logger.info("{} took {} milliseconds to write {} entries.", listClass.newInstance().getClass().getCanonicalName(), writeTimer.elapsed(TimeUnit.MILLISECONDS), size);
                logger.info("{} took {} milliseconds to traverse {} entries.", listClass.newInstance().getClass().getCanonicalName(), traversalTimer.elapsed(TimeUnit.MILLISECONDS), size);
                logger.info("{} took {} milliseconds to find {} entries.", listClass.newInstance().getClass().getCanonicalName(), findTimer.elapsed(TimeUnit.MILLISECONDS), size);
                logger.info("{} took {} milliseconds total.", listClass.newInstance().getClass().getCanonicalName(), totalTimer.elapsed(TimeUnit.MILLISECONDS));
                logger.info("");
                
                newList = null;

                totalTimer.reset();
                traversalTimer.reset();
                writeTimer.reset();
                findTimer.reset();
                Runtime.getRuntime().gc();
            }

            logger.trace("Total: {}", total);
        }
    }
    
    @Test
    public void testMutation() throws InstantiationException, IllegalAccessException {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********       Mutation Test       *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");


        for (int round = 0; round < 2; round++) {

            double total = 0;
            int size = 200000;
            int insertionSize = 20000;

            RandomDataGenerator dataGenerator = new RandomDataGenerator();
            Integer[] values = dataGenerator.getIntegers(size, 20);
            Integer[] insertionValues = dataGenerator.getIntegers(insertionSize, 9);
            Integer[] insertionPoints = dataGenerator.getIntegers(insertionSize, size);
//            List<BigObject> shuffledValues = new ControlArrayList(size);

            List<Class> listClasses = new ArrayList();

            listClasses.add(ArrayList.class);
            listClasses.add(ArrayLinkList.class);
            listClasses.add(LinkedList.class);
            listClasses.add(CompactionList.class);
//            listClasses.add(ControlArrayList.class);
//            StaticControlConfigurator.setSize(size); // Used to configure the control array list.

            Collections.shuffle(listClasses);


            Stopwatch writeTimer = Stopwatch.createUnstarted();
            Stopwatch insertionTimer = Stopwatch.createUnstarted();
            Stopwatch traversalTimer = Stopwatch.createUnstarted();
            Stopwatch totalTimer = Stopwatch.createUnstarted();

            for (Class listClass : listClasses) {
                
                logger.info("Testing {}", listClass.newInstance().getClass().getCanonicalName());
                
                totalTimer.start();
                writeTimer.start();
                // test collection write.
                List newList = (List) listClass.newInstance();
                for (int j = 0; j < values.length; j++) {
                    Object value = values[j];
                    newList.add(value);
                }
                writeTimer.stop();
                
                insertionTimer.start();
                for(int j=0; j<insertionValues.length;j++) {
                    Object insertionValue = insertionValues[j];
                    int insertionPoint = insertionPoints[j];
                    newList.add(insertionPoint, insertionValue);
                }
                insertionTimer.stop();

                traversalTimer.start();
                // Test collection traversal
                Iterator<Integer> valueIterator = newList.iterator();
                while (valueIterator.hasNext()) {
                    int value = (int) valueIterator.next();
                    total += value;
                }
                traversalTimer.stop();
                totalTimer.stop();


                logger.info("{} took {} milliseconds to write {} entries.", listClass.newInstance().getClass().getCanonicalName(), writeTimer.elapsed(TimeUnit.MILLISECONDS), size);
                logger.info("{} took {} milliseconds to insert {} entries.", listClass.newInstance().getClass().getCanonicalName(), insertionTimer.elapsed(TimeUnit.MILLISECONDS), insertionSize);
                logger.info("{} took {} milliseconds to traverse {} entries.", listClass.newInstance().getClass().getCanonicalName(), traversalTimer.elapsed(TimeUnit.MILLISECONDS), size);
                logger.info("{} took {} milliseconds total.", listClass.newInstance().getClass().getCanonicalName(), totalTimer.elapsed(TimeUnit.MILLISECONDS));
                logger.info("");
                
                newList = null;

                totalTimer.reset();
                traversalTimer.reset();
                writeTimer.reset();
                insertionTimer.reset();
                Runtime.getRuntime().gc();
            }

            logger.trace("Total: {}", total);
        }
    }
}
