/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**

 TODO: consider a cache method.

 When a user calls the cache on an index, it is added to the cache (does not actually have to occur internally this way).
 When a user calls dumpCache() an array is returned with the elements in the order that they were attached to the cache.

 This allows get mutations to be added, a more optimized compaction will only occur when dumpCache is called.


 @author Alex Ethier & Liban Mohammed
 */
public class MutationList<E> extends ArrayLinkList<E> {

    private int numMutations;
    private MutationNode mutationTreeRoot;

    public MutationList() {
        numMutations = 0;
//        mutationTreeRoot = new MutationNode();
    }

    @Override
    public Iterator<E> iterator() {
        this.compact();
        return new MutationIterator<E>();
    }

    //
//    @Override
//    public E remove(int index) {
//    }
    @Override
    public void add(int index, E element) {
        if (numMutations == 0) {
            mutationTreeRoot = new MutationNode();
            mutationTreeRoot.value = element;
            mutationTreeRoot.delta = index;
        } else {
            this.addToTree(index, element, mutationTreeRoot);
        }

        super.totalSize++;
        numMutations++;
    }

    private void addToTree(int delta, E element, MutationNode rootNode) {

        delta = delta - mutationTreeRoot.delta;
        if (delta > 0) {


            if (mutationTreeRoot.rightChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = delta;
                newChild.value = element;
                mutationTreeRoot.rightChild = newChild;
            } else {
                this.addToTree(delta, element, mutationTreeRoot.rightChild);
            }
        } else {
            rootNode.delta++;

            if (mutationTreeRoot.leftChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = delta;
                newChild.value = element;
                mutationTreeRoot.rightChild = newChild;
            } else {
                this.addToTree(delta, element, mutationTreeRoot.leftChild);
            }
        }
    }

    // Converts the write buffer mutationList into the read buffer compactArray
    private void compact() {
        
        int newSize = (super.totalSize * 3) / 2 + 1;
        Object[] compactArray = new Object[newSize];
        ChangeNode[] changeLog = new ChangeNode[numMutations];
        this.flattenTree(mutationTreeRoot, changeLog, 0, mutationTreeRoot.delta);
        mutationTreeRoot = null; // Clear the mutation tree.
        numMutations = 0;

        int changeLogCounter = 0; // Keep track of where in the changelog we are at.
        ArrayLink tmpLink = super.firstLink; // The arraylink we are traversing to do compaction.
        int arrayLinkListOffset = 0; // Keep track of the absolute position we are in for the array link list.
        int compactArrayOffset = 0; // Keep track of the write position for the compact array.
        while (tmpLink != null) {
            int arrayLinkOffset = 0; // Keep track of the position we are in for a single link in the array link list.
            arrayLinkListOffset += tmpLink.values.length;
            while (changeLog[changeLogCounter].index < arrayLinkListOffset && changeLogCounter < changeLog.length) {
                if (changeLog[changeLogCounter].mutationType == MutationType.INSERT) {
                    compactArray[compactArrayOffset] = changeLog[changeLogCounter].value;
                    compactArrayOffset++;
                } else {
                    arrayLinkOffset++; // If not an insertion assume removal which means just increasing the arrayLinkOffset.
                }

                // Copy all elements from the array link list that are between change list indexes.
                // This means copying array elements before the latest change log index that is applied.

                /*
                 public static native void arraycopy(Object src, int srcPos,
                 Object dest, int destPos,
                 int length);
                 */

                //TODO: the last value in this copy may need a +/-1
                int copyLength = changeLog[changeLogCounter].index - arrayLinkOffset;
                System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, copyLength);
                arrayLinkOffset += copyLength;
                compactArrayOffset += copyLength;

                changeLogCounter++;
            }

            // Now copy remaining elements in array link list after the last change log index that appears in the given array link.
            System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, tmpLink.values.length - arrayLinkOffset);
        }

        // Apply any change log values occurring after the array link list has been fully copied over.
        // Any remove value encounterd is an internal error.
        while (changeLogCounter < changeLog.length) {
            compactArray[compactArrayOffset] = changeLog[changeLogCounter].value;
            compactArrayOffset++;
            changeLogCounter++;
        }

        tmpLink = new ArrayLink(compactArray);
        super.firstLink = tmpLink;
        super.writeLink = tmpLink;
        super.writeLinkOffset = super.totalSize;
    }

    //TODO: Change the writeArray type to a changelog node.
    private void flattenTree(MutationNode rootNode, ChangeNode[] writeArray, int offset, int index) {
        if (rootNode.leftChild != null) {
            this.flattenTree(rootNode.leftChild, writeArray, offset++, index + rootNode.leftChild.delta);
        }
        ChangeNode changeNode = new ChangeNode();
        changeNode.index = index;
        changeNode.value = rootNode.value;
        changeNode.mutationType = rootNode.mutationType;
        writeArray[offset] = changeNode;
        if (rootNode.rightChild != null) {
            this.flattenTree(rootNode.rightChild, writeArray, offset++, index + rootNode.rightChild.delta);
        }
    }

    /**

     Iterator

     */
    private class MutationIterator<E> implements Iterator {

        protected int offset;
        protected int iteratorModCount;

        public MutationIterator() {
            offset = 0;
            iteratorModCount = MutationList.super.modCount;
        }

        @Override
        public boolean hasNext() {
            return offset != MutationList.super.totalSize;
        }

        @Override
        public E next() {
            checkModCount();
            if (offset >= MutationList.super.totalSize) {
                throw new NoSuchElementException();
            }

            int tmpPointer = offset;
            offset++;
            return (E) MutationList.super.firstLink.values[tmpPointer];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        final void checkModCount() {
            if (MutationList.super.modCount != iteratorModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
