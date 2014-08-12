/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.utils;

/**

 @author alex
 */
public class DataGenerator {
    
    
    public DataGenerator() {
    }
    
    public double[] getDoubles(int size) {
        double[] doubles = new double[size];
        
        for(int i=0; i<size; i++) {
            doubles[i] = Math.random();
        }
        
        return doubles;
    }
    
    public int[] getIntegers(int numNumbers, int maxSize) {
        int[] integers = new int[numNumbers];
        
        for(int i=0; i<numNumbers; i++) {
            integers[i] = (int) (maxSize*Math.random());
        }
        
        return integers;
    }
}
