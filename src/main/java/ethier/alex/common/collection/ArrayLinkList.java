
package ethier.alex.common.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**

An implementation of collection (NOT LIST, will have performance issues on insert).

 @author Alex Ethier
 */
public class ArrayLinkList<E> implements List<E> {
    
    private static final int initSize  = 16;
    
    ArrayLink writeLink;
    
    int bucketWriteOffset;
    int totalSize;
    
    ArrayLink readLink;
//    int readBucketOffset;
//    int readTotalOffset;
    
    public ArrayLinkList() {
        writeLink = new ArrayLink(initSize);
        
        bucketWriteOffset = 0;
        
        readLink = writeLink;
    }
    
    @Override
    public boolean add(E object) {
        
        if(bucketWriteOffset < writeLink.values.length) {
            writeLink.values[bucketWriteOffset] = object;
            bucketWriteOffset++;        
        } else {
            int newSize =  (writeLink.values.length*3)/2 + 1;

            writeLink.next = new ArrayLink(newSize);
            writeLink = writeLink.next;
            
            writeLink.values[0] = object;
            bucketWriteOffset = 1;
        }
        
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
        return new ArrayLinkListIterator(readLink, totalSize);
    }
    
    
    @Override
    public boolean containsAll(Collection<?> c) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public E get(int index) {
        ArrayLink tmpLink = readLink;
        while(index >= tmpLink.values.length) {
            index = index - tmpLink.values.length;
            tmpLink = tmpLink.next;
        }
        
        return (E)tmpLink.values[index];
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
}
