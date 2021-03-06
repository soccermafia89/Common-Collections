/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**

 Performance TODO: Create a separate compaction methods if insert/removals occurred to differentiate from normal add based compaction.
 Testing has shown that this list is worse than compaction list for random access calls after creating the list.

 TODO: consider a cache method.
 When a user calls the cache on an index, it is added to the cache (does not actually have to occur internally this way).
 When a user calls dumpCache() an array is returned with the elements in the order that they were attached to the cache.

 This allows get mutations to be added, a more optimized compaction will only occur when dumpCache is called.


 @author Alex Ethier & Liban Mohammed
 */
public class MutationList<E> extends ArrayLinkList<E> {

    private int numMutations; // The number of insertions/mutations that are written into the change tree.
    private MutationNode mutationTreeRoot;

    public MutationList() {
        numMutations = 0;
    }

    @Override
    public Iterator<E> iterator() {
        if (numMutations > 0) {
            this.compact();
        }

        return super.iterator();
    }

    //
//    @Override
//    public E remove(int index) {
//    }
    @Override
    public void add(int index, E element) {
//        System.out.println("");
//        System.out.println("Insert called with index: " + index + " value: " + element);

        if (numMutations == 0) {
            mutationTreeRoot = new MutationNode();
            mutationTreeRoot.value = element;
            mutationTreeRoot.delta = index;
            mutationTreeRoot.mutationType = MutationType.INSERT;
        } else {
            this.addToTree(index, element, mutationTreeRoot);
        }

        super.totalSize++;
        numMutations++;

//        printTree();
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
    // If you move to a right child, append the current node's delta to the current delta.
    // If you move to a left child, do not append any delta.
    // When returning the node's index value, append the node's delta.
    private void addToTree(int delta, E element, MutationNode rootNode) {

//        System.out.println("Current delta: " + delta + " node delta: " + rootNode.delta);

        if (delta > rootNode.delta) {
            int newDelta = delta - rootNode.delta;

            if (rootNode.rightChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = newDelta;
                newChild.value = element;
                newChild.mutationType = MutationType.INSERT;
                rootNode.rightChild = newChild;
//                System.out.println("Element added to tree with value: " + element + " with node delta: " + delta);
            } else {
                this.addToTree(newDelta, element, rootNode.rightChild);
            }
        } else {

            rootNode.delta++; // Only when appending the child to the left do we increment the root delta.

            if (rootNode.leftChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = delta;
                newChild.value = element;
                newChild.mutationType = MutationType.INSERT;
                rootNode.leftChild = newChild;
//                System.out.println("Element added to tree with value: " + element + " with node delta: " + delta);
            } else {
                this.addToTree(delta, element, rootNode.leftChild);
            }
        }
    }
    // Converts the write buffer mutationList into the read buffer compactArray

    // Converts the write buffer mutationList into the read buffer compactArray
    private void compact() {
        if (super.firstLink.next != null || numMutations > 0) {
//            System.out.println("Processing Compaction.");
//            System.out.println("Total size: " + super.totalSize);
//            System.out.println("Inserts: " + numInserts);
//            int arrayLinkListValues = super.totalSize - numInserts; // Number of values in the array link list.
//            System.out.println("ArrayLinkList values: " + arrayLinkListValues);

//            int newSize = (super.totalSize * 3) / 2 + 1; // Do not allocate extra space on compaction.
            int newSize = super.totalSize + this.getRemainingSize();
            Object[] compactArray = new Object[newSize];

            ArrayLink tmpLink = super.firstLink; // The arraylink we are traversing to do compaction.
            int compactArrayOffset = 0; // Keep track of the write position for the compact array.
            int arrayLinkOffset = 0; // Offset in the current array link for elements already copied into the compact array.

            if (numMutations != 0) { // First merge in all insertions/removals

                ChangeNode[] changeLog = new ChangeNode[numMutations];
                this.flattenTree(mutationTreeRoot, changeLog, 0, 0);
                mutationTreeRoot = null; // Immediately Clear the mutation tree.

                for (int changeLogCounter = 0; changeLogCounter < changeLog.length; changeLogCounter++) {

                    // First merge the underlying array link list values up to the current change log index.
                    // Number of elements needed to be copied from the array link list before applying the next change node.
                    int numArrayLinkCopies = changeLog[changeLogCounter].index - compactArrayOffset;

                    int remainingValues = tmpLink.values.length - arrayLinkOffset; // Number of remaining values that were not copied in the current link.

                    if (numArrayLinkCopies > remainingValues) { // If the number of elements to copy will overflow to the next array link.
                        // First copy the remaining elements in our current array link.
                        System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, remainingValues);
                        compactArrayOffset += remainingValues;
                        numArrayLinkCopies -= remainingValues;
                        arrayLinkOffset = 0;
                        tmpLink = tmpLink.next;

                        // Next traverse each array link copying the full link over if possible.
                        while (numArrayLinkCopies > tmpLink.values.length) {
                            System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, tmpLink.values.length);
                            compactArrayOffset += tmpLink.values.length;
                            numArrayLinkCopies -= tmpLink.values.length;

                            tmpLink = tmpLink.next;
                        }
                    }

                    // Copy remaining elements in last link.
                    System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, numArrayLinkCopies);
                    arrayLinkOffset += numArrayLinkCopies;
                    compactArrayOffset += numArrayLinkCopies;

                    // Apply the change event
                    if (changeLog[changeLogCounter].mutationType == MutationType.INSERT) {
                        compactArray[compactArrayOffset] = changeLog[changeLogCounter].value;
                        compactArrayOffset++;
                    } else { // Else assume removal (by incrementing the arrayLinkOffset.)
                        arrayLinkOffset++;
                    }
//                System.out.println("Finished Applying Change Event, new compacted values: " + Arrays.toString(compactArray));
                }
            }

//            System.out.println("ArrayLinkList values:");
//            super.print();

//            System.out.println("Finished applying all change log events, compact remaining array link list values.");
//            System.out.println("Current compact array snapshot: " + Arrays.toString(compactArray));

            // Now compact the rest of the array link values whose index is beyond any change events.
            
            
            int remainingValues = tmpLink.values.length - arrayLinkOffset; // Number of remaining values that were not copied in the current link.
            int remainderToCopy = super.totalSize - compactArrayOffset; // The number of elements left to copy.
            if(remainderToCopy > remainingValues) { // First finish up the current link's remaining elements which is a corner case.
                System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, remainingValues);
                compactArrayOffset += remainingValues;
                remainderToCopy -= remainingValues;
                tmpLink = tmpLink.next;
                
                // Now loop through the array links copying their entire list as necessary.
                while(remainderToCopy > tmpLink.values.length) {
                    System.arraycopy(tmpLink.values, 0, compactArray, compactArrayOffset, tmpLink.values.length);
                    compactArrayOffset += tmpLink.values.length;
                    remainderToCopy -= tmpLink.values.length;
                    tmpLink = tmpLink.next;
                }
                
                // On the final array link only copy over what we need.
                System.arraycopy(tmpLink.values, 0, compactArray, compactArrayOffset, remainderToCopy);
            } else {
                System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, remainderToCopy);
            }

            tmpLink = new ArrayLink(compactArray);
            super.firstLink = tmpLink;
            super.writeLink = tmpLink;
            super.writeLinkOffset = super.totalSize;

            numMutations = 0; // Don't forget to reset the number of mutations.
        }
    }

    protected int getRemainingSize() {
        return super.writeLink.values.length - super.writeLinkOffset;
    }

    //TODO: Change the writeArray type to a changelog node.
    private int flattenTree(MutationNode rootNode, ChangeNode[] writeArray, int offset, int index) {

        if (rootNode.leftChild != null) {
            offset = this.flattenTree(rootNode.leftChild, writeArray, offset, index);
        }
        ChangeNode changeNode = new ChangeNode();
        changeNode.index = index + rootNode.delta;
        changeNode.value = rootNode.value;
        changeNode.mutationType = rootNode.mutationType;
        writeArray[offset] = changeNode;
//        System.out.println("Writing value: " + changeNode.value + " to offset: " + offset + " with index: " + changeNode.index);
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
