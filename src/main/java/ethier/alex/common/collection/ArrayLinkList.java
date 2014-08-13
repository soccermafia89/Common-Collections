package ethier.alex.common.collection;

import java.util.*;

/**

 An implementation of collection.  Should have much better write performance than ArrayLists, but will be slower during random access.

 @author Alex Ethier
 */
public class ArrayLinkList<E> implements List<E> {

    private static final int initSize = 16;
    protected ArrayLink writeLink;
    protected int writeLinkOffset;
    protected int totalSize;
    protected int modCount; // Used for fail-fast iterators
    protected ArrayLink firstLink;

    public ArrayLinkList() {
        writeLink = new ArrayLink(initSize);
        writeLinkOffset = 0;
        firstLink = writeLink;
        modCount = 0;
    }
    
    public ArrayLinkList(int initialCapacity) {
        writeLink = new ArrayLink(initialCapacity);
        writeLinkOffset = 0;
        firstLink = writeLink;
        modCount = 0;
    }

    @Override
    public boolean add(E object) {

        if (writeLinkOffset < writeLink.values.length) {
            writeLink.values[writeLinkOffset] = object;
            writeLinkOffset++;
        } else {
            int newSize = (writeLink.values.length * 3) / 2 + 1;

            writeLink.next = new ArrayLink(newSize);
            writeLink = writeLink.next;

            writeLink.values[0] = object;
            writeLinkOffset = 1;
        }

        modCount++;
        totalSize++;
        return true;
    }

//    public E getNext() {
//        
//        readTotalOffset++;
//
//        if(readBucketOffset < readLinkedList.values.length) {
//            int tmpOffset = readBucketOffset;
//            readBucketOffset++;
//            return (E) readLinkedList.values[tmpOffset];
//        } else {
//            readLinkedList = readLinkedList.next;
//            readBucketOffset = 1;
//
//            return (E) readLinkedList.values[0];
//        }
//    }
//    public boolean hasNext() {
//        return readTotalOffset < totalSize;
//    }
    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public boolean isEmpty() {
        return totalSize == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayLinkListIterator();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E get(int index) {
        ArrayLink tmpLink = firstLink;
        while (index >= tmpLink.values.length) {
            index = index - tmpLink.values.length;
            tmpLink = tmpLink.next;
        }

        return (E) tmpLink.values[index];
    }

    /*
     Method one:
     If collection.size > remaining object[] size
     Then shrink current objec[] and add a new link
     otherwise copy the array elements onto the current array.

     Method two:
     Always shrink the current object[] and append on a new ArrayLink

     Method three:
     Individually copy elements over.

     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Object o) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] toArray() {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int indexOf(Object o) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int lastIndexOf(Object o) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<E> listIterator() {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        System.out.println("MUST TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support.
    @Override
    public boolean remove(Object o) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Do not support
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**

     Iterator

     */
    private class ArrayLinkListIterator<E> implements Iterator<E> {

        protected ArrayLink linkPointer;
        protected int totalCurrentOffset;
        protected int linkOffset;
        protected int iteratorModCount;// Used for throwing fail fast exceptions.

        public ArrayLinkListIterator() {
            linkPointer = firstLink;
            totalCurrentOffset = 0;
            linkOffset = 0;
            iteratorModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            return totalCurrentOffset != totalSize;
        }

        @Override
        public E next() {
            
            checkModCount();

            totalCurrentOffset++;

            if (linkOffset < linkPointer.values.length) {
                int tmpOffset = linkOffset;
                linkOffset++;
                return (E) linkPointer.values[tmpOffset];
            } else { // We have reached the end of a link and need to grab the next link.
                linkPointer = linkPointer.next;
                linkOffset = 1;

                return (E) linkPointer.values[0];
            }
        }

        // Do not support.
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        final void checkModCount() {
            if (modCount != iteratorModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
