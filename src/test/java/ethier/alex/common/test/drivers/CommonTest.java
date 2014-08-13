package ethier.alex.common.test.drivers;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.monitoring.runtime.instrumentation.common.com.google.common.collect.HashMultiset;
import ethier.alex.common.collection.ArrayLinkList;
import ethier.alex.common.collection.CompactionList;
import ethier.alex.common.collection.ControlArrayList;
import ethier.alex.common.collection.StaticControlConfigurator;
import ethier.alex.common.map.EthierMap;
import ethier.alex.common.test.utils.DataGenerator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**

Tests validating operation and performance of new data structures.

 @author alex
 */
public class CommonTest {
    
    // TEST CASE TODOS:
    // Fail fast under concurrent modification.

    private static Logger logger = LogManager.getLogger(CommonTest.class);

    @BeforeClass
    public static void setUpClass() {
//        BasicConfigurator.configure();
    }

    @Test
    public void testArrayLinkList() {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********    Ethier Array Test     *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");

        int size = 1000000;
//        int size = 5;

        DataGenerator dataGenerator = new DataGenerator();
        double[] values = dataGenerator.getDoubles(size);

        ArrayLinkList<Double> ethierArrayDeque = new ArrayLinkList<Double>();
        for (int i = 0; i < values.length; i++) {
            double value = values[i];
            ethierArrayDeque.add(value);
        }
        int count = 0;

        logger.info("Checking values.");
        Iterator<Double> it = ethierArrayDeque.iterator();
        while (it.hasNext()) {
            double value = it.next();

            Assert.assertTrue(value == values[count]);
            count++;
        }

        logger.info("Checking hasNext.");
        it = ethierArrayDeque.iterator();
        for (int i = 0; i < values.length; i++) {
            Assert.assertTrue(it.hasNext());
            it.next();
        }
        Assert.assertFalse(it.hasNext());

        logger.info("Checking random access.");
        for (int i = 0; i < values.length; i++) {
            int randAccessPoint = (int) (Math.random() * values.length);
            Assert.assertTrue(ethierArrayDeque.get(randAccessPoint) == values[randAccessPoint]);
        }
    }

    @Test
    public void testCompactionList() {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********      Compaction Test      *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");

        int size = 1000000;
//        int size = 5;

        DataGenerator dataGenerator = new DataGenerator();
        double[] values = dataGenerator.getDoubles(size);

        CompactionList<Double> compactionList = new CompactionList<Double>();
        for (int i = 0; i < values.length; i++) {
            double value = values[i];
            compactionList.add(value);
        }
        int count = 0;

        logger.info("Checking values.");
        Iterator<Double> it = compactionList.iterator();
        while (it.hasNext()) {
            double value = it.next();

//            System.out.println("Compaction value:" + value + " real value: " + values[count]);
            Assert.assertTrue(value == values[count]);
            count++;
        }

        logger.info("Checking hasNext.");
        it = compactionList.iterator();
        for (int i = 0; i < values.length; i++) {
            Assert.assertTrue(it.hasNext());
            it.next();
        }
        Assert.assertFalse(it.hasNext());

        logger.info("Checking random access.");
        for (int i = 0; i < values.length; i++) {
            int randAccessPoint = (int) (Math.random() * values.length);
            Assert.assertTrue(compactionList.get(randAccessPoint) == values[randAccessPoint]);
        }
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


        for (int round = 0; round < 2; round++) {

            int size = 20000000;
            DataGenerator dataGenerator = new DataGenerator();
            double[] values = dataGenerator.getDoubles(size);
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

            DataGenerator dataGenerator = new DataGenerator();
            double[][] data = new double[numCollections][size];
            for (int i = 0; i < numCollections; i++) {

                double[] values = dataGenerator.getDoubles(size);
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
                    double[] values = data[i];
                    for(int j=0; j<values.length; j++) {
                        double value = values[j];
                        newCollection.add(value);
//                        logger.info("Adding {} to {}.", value, collectionClass.getCanonicalName());
                    }
                    
                    collectionArray[i] = newCollection;
                }

                writeTimer.stop();

                traversalTimer.start();
                // Test collection traversal
                for(int i=0; i<numCollections; i++) {
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


                logger.info("{} took {} milliseconds to write {} entries in {} collections.", collectionClass.newInstance().getClass().getCanonicalName()
                        , writeTimer.elapsed(TimeUnit.MILLISECONDS), size, numCollections);
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

            int size = 5000000;
            DataGenerator dataGenerator = new DataGenerator();
            double[] values = dataGenerator.getDoubles(size);
            int[] randomAccessPoints = dataGenerator.getIntegers(10*size, size);
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
                for(int i=0; i<randomAccessPoints.length;i++) {
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
    
//    private Collection[] writeCollections(Class collectionClass, int numCollections, int dataSize, )

//    private void addCollection(Class clazz, Collection<Collection> destList, int num) throws InstantiationException, IllegalAccessException {
//        for (int i = 0; i < num; i++) {
//            Collection newCollection = (Collection) clazz.newInstance();
//            destList.add(newCollection);
//        }
//    }

    // The faster map implementation requires further thought and revision before testing...
//    @Test
    public void testMap() throws Exception {
        System.out.println("");
        System.out.println("");
        System.out.println("********************************************");
        System.out.println("********         Map  Test         *********");
        System.out.println("********************************************");
        System.out.println("");
        System.out.println("");

        int size = 3500000;

        DataGenerator dataGenerator = new DataGenerator();
        double[] keys = dataGenerator.getDoubles(size);
        double[] values = dataGenerator.getDoubles(size);

        for (int rounds = 0; rounds < 3; rounds++) {
            List<Double> shuffledKeysList = new ArrayList<Double>();
            for (int i = 0; i < size; i++) {
                shuffledKeysList.add(keys[i]);
            }
            Collections.shuffle(shuffledKeysList);

            double[] shuffledKeys = new double[size];
            for (int i = 0; i < size; i++) {
                double keyVal = shuffledKeysList.get(i);
                shuffledKeys[i] = keyVal;
            }

            Stopwatch timer = Stopwatch.createUnstarted();
            Map<Double, Double> javaUtilMap = new HashMap<Double, Double>();

            timer.start();
            for (int i = 0; i < size; i++) {

                double key = keys[i];
                double value = values[i];

                javaUtilMap.put(key, value);
            }
            timer.stop();
            logger.info("Java Util HashMap took {} milliseconds to enter {} entries.", timer.elapsed(TimeUnit.MILLISECONDS), size);
            timer.reset();
            timer.start();
            for (int i = 0; i < size; i++) {
                double key = shuffledKeys[i];
                javaUtilMap.get(key);
            }
            timer.stop();
            javaUtilMap = null;
            Runtime.getRuntime().gc();

            logger.info("Java Util HashMap took {} milliseconds to read {} entries.", timer.elapsed(TimeUnit.MILLISECONDS), size);

            EthierMap ethierMap = new EthierMap<Double, Double>();

            timer.reset();
            timer.start();
            for (int i = 0; i < size; i++) {

                double key = keys[i];
                double value = values[i];

                ethierMap.put(key, value);
            }
            ethierMap.computeMap();
            timer.stop();
            logger.info("Ethier Map took {} milliseconds to input {} entries.", timer.elapsed(TimeUnit.MILLISECONDS), size);
            timer.reset();
            timer.start();
            for (int i = 0; i < size; i++) {
                double key = shuffledKeys[i];
                ethierMap.get(key);
            }
            timer.stop();
            ethierMap = null;
            Runtime.getRuntime().gc();
            logger.info("Ethier Map took {} milliseconds to read {} entries.", timer.elapsed(TimeUnit.MILLISECONDS), size);
        }

        logger.info("Done");

    }
}
