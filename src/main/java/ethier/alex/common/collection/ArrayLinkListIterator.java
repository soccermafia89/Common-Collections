/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.collection;

import java.util.Iterator;

/**

 @author alex
 */
public class ArrayLinkListIterator<E> implements Iterator<E> {
    
    ArrayLink arrayLink;
    int totalSize;
    int totalCurrentOffset;
    int bucketOffset;
    
    public ArrayLinkListIterator(ArrayLink initLink, int myTotalSize) {
        arrayLink = initLink;
        totalSize = myTotalSize;
        totalCurrentOffset = 0;
        bucketOffset = 0;
    }

    @Override
    public boolean hasNext() {
        return totalCurrentOffset < totalSize;
    }

    @Override
    public E next() {
        
        totalCurrentOffset++;
        
        if(bucketOffset < arrayLink.values.length) {
            int tmpOffset = bucketOffset;
            bucketOffset++;
            return (E) arrayLink.values[tmpOffset];
        } else {
            arrayLink = arrayLink.next;
            bucketOffset = 1;

            return (E) arrayLink.values[0];
        }
    }
    
    // Do not support.
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
