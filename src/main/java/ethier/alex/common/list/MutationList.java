/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

import java.util.Arrays;
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

    private int numInserts;
    private MutationNode mutationTreeRoot;

    public MutationList() {
        numInserts = 0;
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
        System.out.println("");
        System.out.println("Insert called with index: " + index + " value: " + element);

        if (numInserts == 0) {
            mutationTreeRoot = new MutationNode();
            mutationTreeRoot.value = element;
            mutationTreeRoot.delta = index;
            mutationTreeRoot.mutationType = MutationType.INSERT;
            System.out.println("Element added to tree with value: " + element + " with node delta: " + mutationTreeRoot.delta);
        } else {
            this.addToTree(index, element, mutationTreeRoot);
        }

        super.totalSize++;
        numInserts++;

        printTree();
    }

    // To be used for internal testing only.
    private void printTree() {
        this.printTreeNode(mutationTreeRoot, "O", 0);
    }

    private void printTreeNode(MutationNode rootNode, String history, int index) {
        if (rootNode.leftChild != null) {
            printTreeNode(rootNode.leftChild, history + "L", index);
        }
        System.out.println(history + "=" + rootNode.value + " delta=" + rootNode.delta + " index=" + (index + rootNode.delta));
        if (rootNode.rightChild != null) {
            printTreeNode(rootNode.rightChild, history + "R", index + rootNode.delta);
        }
    }

    // The change tree is implemented as follows:
    // An objects initial delta is its index value.
    // To traverse the tree compare the added object's delta with the current root delta.
    // If the child's delta is larger, add to the right and subtract the root node delta from the child.
    // If the child's delta is smaller or equal, increment the root node delta by one.
    // To calculate a child's index value from the deltas:
    // If you move to a right child, append the delta to the current delta.
    // If you move to a left child, do not append any delta.
    // When returning the node's index value, append the node's delta.
    private void addToTree(int delta, E element, MutationNode rootNode) {

        System.out.println("Current delta: " + delta + " node delta: " + rootNode.delta);

        if (delta > rootNode.delta) {
            int newDelta = delta - rootNode.delta;
            System.out.println("Appending to right side.");

            if (rootNode.rightChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = newDelta;
                newChild.value = element;
                newChild.mutationType = MutationType.INSERT;
                rootNode.rightChild = newChild;
                System.out.println("Element added to tree with value: " + element + " with node delta: " + delta);
            } else {
                this.addToTree(newDelta, element, rootNode.rightChild);
            }
        } else {
            System.out.println("Appending to left side.");

            rootNode.delta++; // Only when appending the child to the left do we increment the root delta.

            if (rootNode.leftChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = delta;
                newChild.value = element;
                newChild.mutationType = MutationType.INSERT;
                rootNode.leftChild = newChild;
                System.out.println("Element added to tree with value: " + element + " with node delta: " + delta);
            } else {
                this.addToTree(delta, element, rootNode.leftChild);
            }
        }
    }

    // Converts the write buffer mutationList into the read buffer compactArray
    private void compact() {
        if (super.firstLink.next != null || numInserts > 0) {
            System.out.println("Processing Compaction.");
            System.out.println("Total size: " + super.totalSize);
            System.out.println("Inserts: " + numInserts);
            int arrayLinkListValues = super.totalSize - numInserts; // Number of values in the array link list.
            System.out.println("ArrayLinkList values: " + arrayLinkListValues);

            int newSize = (super.totalSize * 3) / 2 + 1;
            Object[] compactArray = new Object[newSize];

            ChangeNode[] changeLog = null;
            if (numInserts != 0) {
                changeLog = new ChangeNode[numInserts];
                this.flattenTree(mutationTreeRoot, changeLog, 0, 0);
                mutationTreeRoot = null; // Clear the mutation tree.

                System.out.print("Change log value list with length: " + changeLog.length + ": [");
                for (int i = 0; i < changeLog.length; i++) {
                    ChangeNode changeNode = changeLog[i];
                    System.out.print(" " + changeNode.index + "=" + changeNode.value);
                }
                System.out.println(" ]");
            }

            int changeLogCounter = 0; // Keep track of where in the changelog we are at.
            ArrayLink tmpLink = super.firstLink; // The arraylink we are traversing to do compaction.
            int arrayLinkListOffset = 0; // Keeps track of how many elements we have traversed via link list.
            int compactArrayOffset = 0; // Keep track of the write position for the compact array.
            int arrayLinkListValuesWritten = 0; // Keeps track of number of elements in our array linkn list that have been compacted.

            /**
             TODO: See comment below.
             */
            //TODO: Instead of using the tmpLink != null we may be able to break off computation earlier 
            // if we know that the arrayLinkList offset is reached.
            // i.e. if we merge in super.total size of array link values, break early.
          
            while (arrayLinkListValuesWritten != arrayLinkListValues) { // Iterate over all links in the internal array link list.
                System.out.println("");
                System.out.println("Processing next array link with num values: " + tmpLink.values.length);

                int arrayLinkOffset = 0; // Keep track of the position we are in for a single link in the array link list.
                arrayLinkListOffset += tmpLink.values.length;

                // First attempt merging until all change events in this region are used up.
                while (numInserts != 0 && changeLogCounter < changeLog.length && changeLog[changeLogCounter].index < arrayLinkListOffset) {
                    System.out.println("Merging array link values up to change log index: " + changeLog[changeLogCounter].index);
                    int numArrayLinkCopies = changeLog[changeLogCounter].index - compactArrayOffset;
                    System.out.println("Copying " + numArrayLinkCopies + " array link values.");
                    // First merge the array using the array link list values up to the current change log index.
                    System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, numArrayLinkCopies);
                    arrayLinkOffset += numArrayLinkCopies;
                    compactArrayOffset += numArrayLinkCopies;

                    // Second apply the change event and increment the change log counter.
                    if (changeLog[changeLogCounter].mutationType == MutationType.INSERT) {
//                        System.out.println("INSERTION MUTATION DETECTED");
                        compactArray[compactArrayOffset] = changeLog[changeLogCounter].value;
                        compactArrayOffset++;
                    } else { // Else assume removal (by incrementing the arrayLinkOffset.)
//                        System.out.println("DELETION MUTATION DETECTED: [" + changeLog[changeLogCounter].mutationType + "]");
                        arrayLinkOffset++;
                    }

                    changeLogCounter++;
                    System.out.println("Finished Applying Change Event, new compacted values: " + Arrays.toString(compactArray));
                }
                
                System.out.println("Finished merging all change events in this link, applying rest of array link values.");

                if (tmpLink.next != null) {
                    // Now apply any left over array link values after the final change log event.
                    int numArrayLinkCopies = tmpLink.values.length - arrayLinkOffset;
                    System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, numArrayLinkCopies);
                    arrayLinkOffset += numArrayLinkCopies;
                    compactArrayOffset += numArrayLinkCopies;
                } else {
                                        
                    System.out.println("Using write link for copy: " + writeLink.values.length);
                    System.out.println("Net number of values already copied: " + arrayLinkListValuesWritten);
                    System.out.println("Net number of values in array link list: " + arrayLinkListValues);
                    
                    // We are at the write link and do not need to copy all values over.
                    int numArrayLinkCopies = arrayLinkListValues - arrayLinkListValuesWritten - arrayLinkOffset; // Add in the effect of most recent array link offset
                    System.arraycopy(writeLink.values, arrayLinkOffset, compactArray, compactArrayOffset, numArrayLinkCopies);
                    break;                 
                }

                System.out.println("Finished Link Merge, new compacted values: " + Arrays.toString(compactArray));

                tmpLink = tmpLink.next;
                arrayLinkListValuesWritten += arrayLinkOffset;
                System.out.println("Net array link offset: " + arrayLinkOffset);
                System.out.println("New total array link list values written: " + arrayLinkListValuesWritten);
            }

            System.out.println("");
            System.out.println("Finished applying array link list merge, applying remaining change log events.");

            // Apply any change log values occurring after the array link list has been fully copied over.
            // Any remove mutation encountered is an internal error.
            while (changeLog != null && changeLogCounter < changeLog.length) {
//                System.out.println("Change log counter: " + changeLogCounter + " object: " + changeLog[changeLogCounter]);
                compactArray[compactArrayOffset] = changeLog[changeLogCounter].value;
                compactArrayOffset++;
                changeLogCounter++;
            }

            tmpLink = new ArrayLink(compactArray);
            super.firstLink = tmpLink;
            super.writeLink = tmpLink;
            super.writeLinkOffset = super.totalSize;

            System.out.println("Final compacted Content: " + Arrays.toString(compactArray));
            System.out.println("");
            System.out.println("");

            numInserts = 0; // Don't forget to reset the number of mutations.
        }
    }

    //TODO: Change the writeArray type to a changelog node.
    private int flattenTree(MutationNode rootNode, ChangeNode[] writeArray, int offset, int index) {
//        System.out.println("Flatten tree called with write offset: " + offset);

        if (rootNode.leftChild != null) {
            offset = this.flattenTree(rootNode.leftChild, writeArray, offset, index);
        }
        ChangeNode changeNode = new ChangeNode();
        changeNode.index = index + rootNode.delta;
        changeNode.value = rootNode.value;
        changeNode.mutationType = rootNode.mutationType;
        writeArray[offset] = changeNode;
        System.out.println("Writing value: " + changeNode.value + " to offset: " + offset + " with index: " + changeNode.index);
        offset++;
        if (rootNode.rightChild != null) {
            offset = this.flattenTree(rootNode.rightChild, writeArray, offset, index + rootNode.delta);
        }

        return offset;
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
