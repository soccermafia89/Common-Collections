package ethier.alex.common.collection;

/**

To be used by ArrayLinkList only.

 @author Alex Ethier
 */
public class ArrayLink<E> {
    
    public E[] values;
    public ArrayLink next;
    
    public ArrayLink(int size) {
        values = (E[]) new Object[size];
    }
    
    public ArrayLink(E[] newValues) {
        values = newValues;
    }
}
