/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.backup;

/**

An implementation of collection (NOT LIST, will have performance issues on insert).

 @author alex
 */
public class EthierArrayDequeBackup <V> {
    
    ArrayLinkedListBackup linkedList;
    
    Object[] currentArray;
    int bucketWriteOffset;
    int totalSize;
    
    ArrayLinkedListBackup readList;
    Object[] readCurrentArray;
    int readBucketOffset;
    int readBucketSize;
    int readTotalOffset;
    int tmpOffset;
    
    public EthierArrayDequeBackup() {
        linkedList = new ArrayLinkedListBackup();
        
        currentArray = new Object[16];
        linkedList.set(currentArray);
        bucketWriteOffset = 0;
        
        readList = linkedList;
        readCurrentArray = currentArray;
        readBucketOffset = 0;
        readBucketSize = 16;
        readTotalOffset = 0;
    }
    
    public void add(V object) {
        
        if(bucketWriteOffset < currentArray.length) {
            currentArray[bucketWriteOffset] = object;
            bucketWriteOffset++;        
        } else {
            int newSize =  currentArray.length << 1;
            currentArray = new Object[newSize];
            currentArray[0] = object;
            bucketWriteOffset = 1;
            linkedList.createNext();
            linkedList = linkedList.moveNext();
            linkedList.set(currentArray);            
        }
        
        totalSize++;
    }
    
    public V getNext() {
        
        readTotalOffset++;

        if(readBucketOffset < readBucketSize) {
            tmpOffset = readBucketOffset;
            readBucketOffset++;
            return (V) readCurrentArray[tmpOffset];
        } else {
            readList = readList.moveNext();
            readBucketOffset = 1;
            readBucketSize = readBucketSize << 1;

            readCurrentArray = readList.get();
            return (V) readCurrentArray[0];
        }
    }
        
    public boolean hasNext() {
        return readTotalOffset < totalSize;
    }
}
