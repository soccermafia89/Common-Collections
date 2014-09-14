package ethier.alex.common.list;

import java.util.*;

/**

 An implementation of List. Should have much better write performance than ArrayLists, but will be slower during random access.
TODO: Consider having an array link counter that keeps track of the number of links in the list.  This will prove useful when traversing the list
as we no longer need while loops and instead may use for loops for traversal.

 @author Alex Ethier
 */
public class ArrayLinkList<E> implements List<E> {

    private static final int initSize = 12; // Set the initial size to mimic current array lists.
    protected ArrayLink writeLink; // Pointer to the tail of the list for fast writing.
    protected int writeLinkOffset; // Where to write new values in the current writeLink's arraylist.
    protected int totalSize; // Keep trace of the size of the list.
    protected int modCount; // Used for fail-fast iterators.
    protected ArrayLink firstLink; // Pointer to the head of the list for reading.

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

        if (writeLinkOffset != writeLink.values.length) {
            writeLink.values[writeLinkOffset] = object;
            writeLinkOffset++;
        } else { // Once the current write link's array is full, allocate a new array rather than using array copy.
            int newSize = (writeLink.values.length * 3) / 2 + 1; // When creating the new size mimic array list's implementation.

            writeLink.next = new ArrayLink(newSize);
            writeLink = writeLink.next;

            writeLink.values[0] = object;
            writeLinkOffset = 1;
        }

        modCount++;
        totalSize++;
        return true;
    }

    // Inserting elements at random points behaves like a normal array list except on the specific array link instead of the entire list.
    @Override
    public void add(int index, E element) {

        if (index > totalSize || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSize);
        }

        int arrayLinkListOffset = 0; // Tracks absolute number of elements we have traversed in the list.
        ArrayLink tmpLink = firstLink;
        while ((arrayLinkListOffset += tmpLink.values.length) < index) {
            tmpLink = tmpLink.next;
        }
        
        if (tmpLink.next == null) {// We are at the last link, i.e the writeLink
            if (totalSize == arrayLinkListOffset) { // The last link happens to be full.
                // TODO: Since this code is the same as below, consider creating a single method to use.
                int shiftValues = arrayLinkListOffset - index; // Number of elements we have to shift.
                int arrayLinkOffset = writeLink.values.length - shiftValues; // Where to begin shifting elements.

                Object[] newValues = new Object[writeLink.values.length + 1];

                System.arraycopy(writeLink.values, 0, newValues, 0, arrayLinkOffset);
                System.arraycopy(writeLink.values, arrayLinkOffset, newValues, arrayLinkOffset + 1, shiftValues);
                writeLink.values = newValues;
                writeLink.values[arrayLinkOffset] = element;
            } else { // Creating a new array is not necessary, only shift over required elements.
                int arrayLinkIndex = (index - arrayLinkListOffset) + writeLink.values.length; // Where to begin shifting elements.                
                int shiftValues = writeLinkOffset - arrayLinkIndex;
                                
//                if(shiftValues > 0) {  Not sure if this check makes things faster or slower...
                    System.arraycopy(writeLink.values, arrayLinkIndex, writeLink.values, arrayLinkIndex + 1, shiftValues);
//                }
                writeLink.values[arrayLinkIndex] = element;
            }
            
            writeLinkOffset++; // Remember to update the write link offset.
        } else {
            int shiftValues = arrayLinkListOffset - index; // Number of elements we have to shift.
            int arrayLinkOffset = tmpLink.values.length - shiftValues; // Where to begin shifting elements.

            Object[] newValues = new Object[tmpLink.values.length + 1];

            System.arraycopy(tmpLink.values, 0, newValues, 0, arrayLinkOffset);
            System.arraycopy(tmpLink.values, arrayLinkOffset, newValues, arrayLinkOffset + 1, shiftValues);
            tmpLink.values = newValues;
            tmpLink.values[arrayLinkOffset] = element;
        }

        totalSize++;
        modCount++;

//        this.print();
    }

    @Override
    public E remove(int index) {
        
        /**
        
        TODO ERROR: if at last link, we need to modify the write link offset value to support add(object e);
        
        */
        
        if (index >= totalSize) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSize);
        }

        modCount++;

        int count = index;
        ArrayLink tmpLink = firstLink;
        while (count >= tmpLink.values.length) {
            count = count - tmpLink.values.length;
            tmpLink = tmpLink.next;
        }

        E oldValue = (E) tmpLink.values[count];

        Object[] newValues = new Object[tmpLink.values.length - 1];
        System.arraycopy(tmpLink.values, 0, newValues, 0, count);
        System.arraycopy(tmpLink.values, count + 1, newValues, count, newValues.length - count);
        tmpLink.values = newValues;
//        this.print();
        --totalSize;

        return oldValue;
    }

    // Convenience method for testing purposes only.
    public void print() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Content: ");
        ArrayLink tmpLink = firstLink;
        while (tmpLink != null) {
            stringBuilder.append(Arrays.toString(tmpLink.values)).append(" -> ");
            tmpLink = tmpLink.next;
        }

        System.out.println(stringBuilder.toString());
    }

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
        if (index > totalSize) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSize);
        }

        ArrayLink tmpLink = firstLink;
        while (index >= tmpLink.values.length) {
            index = index - tmpLink.values.length;
            tmpLink = tmpLink.next;
        }

        return (E) tmpLink.values[index];
    }

    /*
     
    Fill up the current array with values and then create a new array link if necessary.

     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        // Implementing method 1.

        Object[] newArray = c.toArray();

        int additionalSize = newArray.length;
        int remainingSize = writeLink.values.length - writeLinkOffset;

        if (additionalSize < remainingSize) { // Luckily we can append the new elements over.
            System.arraycopy(newArray, 0, writeLink.values, writeLinkOffset, additionalSize);
        } else {
            System.arraycopy(newArray, 0, writeLink.values, writeLinkOffset, remainingSize);
            int leftOverSize = additionalSize - remainingSize;
            int newSize = Math.max((writeLink.values.length * 3) / 2, leftOverSize);
            writeLink.next = new ArrayLink(newSize);
            writeLink = writeLink.next;
            System.arraycopy(newArray, 0, writeLink.values, writeLinkOffset, leftOverSize);
        }

        modCount++;
        totalSize += additionalSize;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Object[] toArray() {
        Object[] returnArray = new Object[totalSize];

        ArrayLink link = firstLink;
        int offset = 0;
        int remainder = totalSize;
        while (remainder > link.values.length) {
            System.arraycopy(link.values, 0, returnArray, offset, link.values.length);
            offset += link.values.length;
            remainder = totalSize - offset;
            link = link.next;
        }
        // We are on the last link now.
        System.arraycopy(link.values, 0, returnArray, offset, remainder);

        return returnArray;
    }

    // This method is bizzare, implement later.
    @Override
    public <T> T[] toArray(T[] a) {
        System.out.println("TODO");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
    
    TODO: This algorithm can be better optimized.  Instead of using a while loop and checking each iteration,
    we can calculate the number of elements we need to check and use a for loop instad, if we find the element the return statement
    will break early.
        
    */
    @Override
    public int indexOf(Object o) {

        ArrayLink tmpLink = firstLink;
        int totalOffset = 0; // Absolute index we are searching through and will return.
        int linkOffset = 0; // The relative offset to the link we are currently on, used to determine when to switch links.
        if (o == null) {
            while (totalOffset < totalSize) {
                if (linkOffset == tmpLink.values.length) {
                    tmpLink = tmpLink.next;
                    linkOffset = 0;
                } else {
                    if (tmpLink.values[linkOffset] == null) {
                        return totalOffset;
                    }

                    totalOffset++;
                    linkOffset++;
                }
            }
        } else {
            while (totalOffset < totalSize) {
                if (linkOffset == tmpLink.values.length) {
                    tmpLink = tmpLink.next;
                    linkOffset = 0;
                } else {
                    if (tmpLink.values[linkOffset].equals(o)) {
                        return totalOffset;
                    }

                    totalOffset++;
                    linkOffset++;
                }
            }
        }

        return -1;
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
    
    // Need an explanation on why the array list implementation holds onto its allocated memory upon clearing.
    // Till then just do what the constructor does.
    @Override
    public void clear() {
        modCount++;
        
        writeLink = new ArrayLink(initSize);
        writeLinkOffset = 0;
        firstLink = writeLink;
        totalSize = 0;
    }

    /*
    
    TODO: This has the most efficient iteration method which isn't used throughout other method implementations.
    Try using this iteration method throughout.
    
    */
    @Override
    public E set(int index, E element) {
        if (index > totalSize || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSize);
        }

        ArrayLink tmpLink = firstLink;
        while(index > tmpLink.values.length) {
            index -= tmpLink.values.length;
            tmpLink = tmpLink.next;
        }
                
        Object object = tmpLink.values[index];
        tmpLink.values[index] = element;
        return (E) object;
    }

    /**

     Iterator

     */
    private class ArrayLinkListIterator<E> implements Iterator<E> {

        protected ArrayLink linkPointer;
        protected int totalCurrentOffset;
        protected int linkOffset;
        protected int iteratorModCount;// Used for throwing fail fast exceptions.
        protected int removeOffset; // Used to track last removed offset.

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

            this.checkModCount();
            if (totalCurrentOffset >= totalSize) {
                throw new NoSuchElementException();
            }

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

        // Do not support...yet
        @Override
        public void remove() {
            this.checkModCount();
            ArrayLinkList.this.remove(totalCurrentOffset);
            totalCurrentOffset--;
            linkOffset--;
            iteratorModCount++;  
        }

        final void checkModCount() {
            if (modCount != iteratorModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
