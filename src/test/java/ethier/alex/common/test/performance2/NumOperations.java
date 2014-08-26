/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance2;

/**

 @author alex
 */
public enum NumOperations {
    LARGE(20000000), 
    MEDIUM(10000), 
    SMALL(500), 
    TINY(5);
    
    private int size;
    
    
    private NumOperations(int mySize) {
        size = mySize;
    }
    
    public int getNumOperations() {
        return size;
    }
}
