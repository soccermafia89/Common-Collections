/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**

 An implementation of List. Should have much better write performance than an ArrayList while retaining read performance.

TODO: Override insert and remove if they are running slower (match new implementation to arraylist after calling a compaction).
TODO: Do not compact on traversal (iterator called).
TODO: Do not append extra space on certain compactions.

 @author Alex Ethier
 */
public class CompactionList<E> extends ArrayLinkList<E> {

//    Object[] compactArray;// Consider removing this pointer and instead using super.firstLink

    @Override
    public E get(int index) {
        compact();
        if(index > super.totalSize) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSize);
        }
        
        return (E) super.firstLink.values[index];
    }

//    @Override
//    public Iterator<E> iterator() {
//        
//        compact();
//        return new CompactionIterator<E>();
//    }

    // Compacts the underlying linklist into a single link with all elements within a single array.
    // All read methods should call compact().
    private void compact() {

        if (super.firstLink.next != null) {
            ArrayLink link = super.firstLink;

            int newSize = (super.totalSize * 3) / 2 + 1;
            Object[] compactArray = new Object[newSize];
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
        this.compact();
        return super.toArray();
    }

    @Override
    public int indexOf(Object o) {
        this.compact();

        if (o == null) {
            for (int i = 0; i < totalSize; i++) {
                if (super.firstLink.values[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < totalSize; i++) {
                if (super.firstLink.values[i].equals(o)) {
                    return i;
                }
            }
        }
        
        return -1;
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
            return offset != CompactionList.super.totalSize;
        }

        @Override
        public E next() {
            checkModCount();
            if(offset >= CompactionList.super.totalSize) {
                throw new NoSuchElementException();
            }

            int tmpPointer = offset;
            offset++;
            return (E) CompactionList.super.firstLink.values[tmpPointer];
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
