/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.collection;

import java.util.Arrays;
import java.util.Iterator;

/**

 @author alex
 */
public class CompactionIterator<E> implements Iterator {
    
    int size;
    int pointer;
    Object[] compactArray;
    
    public CompactionIterator(Object[] myCompactArray, int mySize) {
        compactArray = myCompactArray;
        size = mySize;
        pointer = 0;
    }

    @Override
    public boolean hasNext() {
        return pointer < size;
    }

    @Override
    public E next() {
        int tmpPointer = pointer;
        pointer++;
        return (E)compactArray[tmpPointer];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
