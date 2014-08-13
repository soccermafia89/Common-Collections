package ethier.alex.common.collection;

/**

To be used by the ArrayLinkList.

 @author Alex Ethier
 */
public class ArrayLink<E> {
    
    protected E[] values;
    protected ArrayLink next;
    
    protected ArrayLink(int size) {
        values = (E[]) new Object[size];
    }
    
    protected ArrayLink(E[] newValues) {
        values = newValues;
    }
}
