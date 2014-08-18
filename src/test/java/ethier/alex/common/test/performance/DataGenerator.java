/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance;

/**

 @author alex
 */
public class DataGenerator {
    
    
    public DataGenerator() {
    }
    
    public Double[] getDoubles(int size) {
        Double[] doubles = new Double[size];
        
        for(int i=0; i<size; i++) {
            doubles[i] = Math.random();
        }
        
        return doubles;
    }
    
    public Integer[] getIntegers(int numNumbers, int maxSize) {
        Integer[] integers = new Integer[numNumbers];
        
        for(int i=0; i<numNumbers; i++) {
            integers[i] = (int) (maxSize*Math.random());
        }
        
        return integers;
    }
    
    public BigObject[] getBigObjects(int number, int objectSize) {
        BigObject[] bigObjects = new BigObject[number];
        for(int i=0; i < bigObjects.length; i++) {
            BigObject newBigObject = new BigObject(objectSize, i);
            bigObjects[i] = newBigObject;
        }
        
        return bigObjects;
    }
}
