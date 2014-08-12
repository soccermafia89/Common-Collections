/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.backup;

/**

 @author alex
 */
public class ArrayLinkedListBackup {
    
    private Object[] values;
    private ArrayLinkedListBackup next;
    
    public ArrayLinkedListBackup() {
        
    }
    
    private ArrayLinkedListBackup(Object[] myValues) {
        values = myValues;
    }
    
    public Object[] get() {
        return values;
    }
    
    public void set(Object[] newValues) {
        values = newValues;
    }
    
    public void createNext() {
        next = new ArrayLinkedListBackup();
    }
    
    public ArrayLinkedListBackup moveNext() {
        return next;
    }
}
