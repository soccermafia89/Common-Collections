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
        if(rootNode.leftChild != null) {
            printTreeNode(rootNode.leftChild, history + "L", index);
        }
        System.out.println(history + "=" + rootNode.value + " delta=" + rootNode.delta + " index=" + (index + rootNode.delta));
        if(rootNode.rightChild != null) {
            printTreeNode(rootNode.rightChild, history + "R", index + rootNode.delta);
        }
    }

    private void addToTree(int delta, E element, MutationNode rootNode) {

        System.out.println("Current delta: " + delta + " node delta: " + rootNode.delta);
        
        if (delta > rootNode.delta) { 
            int newDelta = delta - rootNode.delta;
            System.out.println("Appending to right side.");

            if (rootNode.rightChild == null) {
                MutationNode newChild = new MutationNode();
                newChild.delta = newDelta;
                newChild.value = element;
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
            System.out.println("ArrayLinkList values: " + (super.totalSize - numInserts));

            int newSize = (super.totalSize * 3) / 2 + 1;
            Object[] compactArray = new Object[newSize];

            ChangeNode[] changeLog = null;
            if (numInserts > 0) {
                changeLog = new ChangeNode[numInserts];
                this.flattenTree(mutationTreeRoot, changeLog, 0, 0);
                /**
                
                TODO: Write an iterator to print out what the tree looks like.
                
                */
                
                
                System.out.println("Change log flattened: " + Arrays.toString(changeLog));
                mutationTreeRoot = null; // Clear the mutation tree.
            }
            
            System.out.print("Change log value list: [");
            for(int i=0; i < changeLog.length; i++) {
                ChangeNode changeNode = changeLog[i];
                System.out.print(changeNode.value);
            }
            System.out.println("]");

            int changeLogCounter = 0; // Keep track of where in the changelog we are at.
            ArrayLink tmpLink = super.firstLink; // The arraylink we are traversing to do compaction.
            int arrayLinkListOffset = 0; // Keep track of the absolute position we are in for the array link list.
            int compactArrayOffset = 0; // Keep track of the write position for the compact array.
            int arrayLinkListRemainder = super.totalSize - numInserts; // Remaining number of elements in array link list that need to be copied.
            System.out.println("ArrayLinkList values: " + (super.totalSize - numInserts));
            while (arrayLinkListRemainder != 0) {
                System.out.println("Compacting next array link.");

                int arrayLinkOffset = 0; // Keep track of the position we are in for a single link in the array link list.
                arrayLinkListOffset += tmpLink.values.length;
                System.out.println("New array link list offset: " + arrayLinkListOffset);

                System.out.println("Change log: " + changeLog);
                if (changeLog != null) {
                    System.out.println("change log index: " + changeLog[changeLogCounter].index + " counter: " + changeLogCounter);
                }
                while (changeLog != null && changeLog[changeLogCounter].index < arrayLinkListOffset && changeLogCounter < changeLog.length) {
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
                    //TODO: Check to make sure the array link list actually has elements to copy.

                    int copyLength = changeLog[changeLogCounter].index - arrayLinkOffset;
                    if (copyLength > arrayLinkListRemainder) {
                        System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, copyLength);
                        arrayLinkListRemainder -= copyLength;
                        compactArrayOffset += copyLength;
                    } else {
                        System.out.println("Array linked list remainder: " + arrayLinkListRemainder);
                        System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, arrayLinkListRemainder);
                        arrayLinkListRemainder = 0;
                        compactArrayOffset += arrayLinkListRemainder;
                        //TODO: Add break statements to allow faster completion.
                    }
                    arrayLinkOffset += copyLength;

                    changeLogCounter++;
                }

                System.out.println("Copying remaining arraylist elements.");
                // Now copy remaining elements in array link list after the last change log index that appears in the given array link.
                int copyLength = tmpLink.values.length - arrayLinkOffset;
                if (copyLength > arrayLinkListRemainder) {
                    System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, copyLength);
                    arrayLinkListRemainder -= copyLength;
                    compactArrayOffset += copyLength;
                } else {
                    System.out.println("Array linked list remainder: " + arrayLinkListRemainder);
                    System.arraycopy(tmpLink.values, arrayLinkOffset, compactArray, compactArrayOffset, arrayLinkListRemainder);
                    arrayLinkListRemainder = 0;
                    compactArrayOffset += arrayLinkListRemainder;
                    //TODO: Add break statements to allow faster completion.
                }

                tmpLink = tmpLink.next;
                System.out.println("Compacted Content: " + Arrays.toString(compactArray));
            }

            // Apply any change log values occurring after the array link list has been fully copied over.
            // Any remove mutation encountered is an internal error.
            while (changeLog != null && changeLogCounter < changeLog.length) {
                System.out.println("Change log counter: " + changeLogCounter + " object: " + changeLog[changeLogCounter]);
                compactArray[compactArrayOffset] = changeLog[changeLogCounter].value;
                compactArrayOffset++;
                changeLogCounter++;
            }

            tmpLink = new ArrayLink(compactArray);
            super.firstLink = tmpLink;
            super.writeLink = tmpLink;
            super.writeLinkOffset = super.totalSize;
            
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
