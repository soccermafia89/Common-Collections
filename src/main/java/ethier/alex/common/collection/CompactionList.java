/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.collection;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**

 An implementation of collection. Should have much better write performance than an ArrayList while retaining read performance.

 @author Alex Ethier
 */
public class CompactionList<E> extends ArrayLinkList {

    Object[] compactArray;

    @Override
    public E get(int index) {

        compact();
        return (E) super.firstLink.values[index];
    }

    @Override
    public Iterator<E> iterator() {
        compact();
        if (compactArray == null) { // Consider corner case where compaction hasn't occurred yet.
            return new CompactionIterator();
        }
        return new CompactionIterator();
    }

    // Compacts the underlying linklist into a single link with all elements within a single array.
    private void compact() {

        if (super.firstLink.next != null) {
            ArrayLink link = super.firstLink;

            int newSize = (super.totalSize * 3) / 2 + 1;
            compactArray = new Object[newSize];
            int offset = 0;

            while (link.next != null) {
                System.arraycopy(link.values, 0, compactArray, offset, link.values.length);
                offset += link.values.length;
                link = link.next;
            }
            // The last link is a corner case
            Object[] values = link.values;
            System.arraycopy(values, 0, compactArray, offset, super.totalSize - offset);

            link = new ArrayLink(compactArray);
            super.firstLink = link;
            super.writeLink = link;
        }
    }
    
    @Override
    public Object[] toArray() {
        Object[] returnArray = new Object[totalSize];
        
        ArrayLink link = firstLink;
        int offset = 0;
        while (link.next != null) {
            System.arraycopy(link.values, 0, returnArray, offset, link.values.length);
            offset += link.values.length;
            link = link.next;
        }
        
        // Use this oppurtunity as a free compaction (that also shrinks the array).
        ArrayLink compactLink = new ArrayLink(returnArray);
        firstLink = compactLink;
        writeLink = compactLink;
        
        return returnArray;
    }

    /**

     Iterator

     */
    private class CompactionIterator<E> implements Iterator {

        protected int offset;
        protected int iteratorModCount;

        public CompactionIterator() {
            offset = 0;
            iteratorModCount = CompactionList.super.modCount;
        }

        @Override
        public boolean hasNext() {
            return offset < CompactionList.super.totalSize;
        }

        @Override
        public E next() {
            checkModCount();
            
            int tmpPointer = offset;
            offset++;
            return (E) compactArray[tmpPointer];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        final void checkModCount() {
            if (CompactionList.super.modCount != iteratorModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
