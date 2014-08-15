/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.backup;

/**

 @author alex
 */
public class ArrayLinkBackup {
    
    private Object[] values;
    private ArrayLinkBackup next;
    
    public ArrayLinkBackup() {
        
    }
    
    public Object[] get() {
        return values;
    }
    
    public void set(Object[] newValues) {
        values = newValues;
    }
    
    public void createNext() {
        next = new ArrayLinkBackup();
    }
    
    public ArrayLinkBackup getNext() {
        return next;
    }
    
    public int size() {
        return values.length;
    }
}
