/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance;

import java.util.Random;

/**

 @author alex
 */
public class SeededGenerator implements DataGenerator {

    private final long seed;

    public SeededGenerator(long mySeed) {
        seed = mySeed;
    }

    @Override
    public Double[] getDoubles(int size) {
        Random generator = new Random(seed);

        Double[] doubles = new Double[size];

        for (int i = 0; i < size; i++) {
            doubles[i] = generator.nextDouble();
        }

        return doubles;
    }

    @Override
    public Integer[] getIntegers(int numNumbers, int maxSize) {
        Random generator = new Random(seed);

        Integer[] integers = new Integer[numNumbers];

        for (int i = 0; i < numNumbers; i++) {
            integers[i] = generator.nextInt(maxSize);
        }

        return integers;
    }

    @Override
    public BigObject[] getBigObjects(int number, int objectSize) {
        
        BigObject[] bigObjects = new BigObject[number];
        for (int i = 0; i < bigObjects.length; i++) {
            BigObject newBigObject = new BigObject(objectSize, i);
            bigObjects[i] = newBigObject;
        }

        return bigObjects;
    }
}
