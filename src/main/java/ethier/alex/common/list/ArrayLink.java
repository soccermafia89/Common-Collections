package ethier.alex.common.list;

/**

To be used by the ArrayLinkList.  Is a single link in a link list that contains an array as its value.

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
