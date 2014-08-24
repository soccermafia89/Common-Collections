/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance;

/**

 @author alex
 */
public interface DataGenerator {
    
    public Double[] getDoubles(int size);
    
    public Integer[] getIntegers(int numNumbers, int maxSize);
    
    public BigObject[] getBigObjects(int number, int objectSize);
    
}
