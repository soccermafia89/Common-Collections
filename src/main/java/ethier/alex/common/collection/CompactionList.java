/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.collection;

import java.util.Iterator;

/**

To support better read times than the ArrayLinkList, on any read the multiple links will be condensed into a single link.

 @author Alex Ethier
 */
public class CompactionList<E> extends ArrayLinkList {

    Object[] compactArray;

    @Override
    public E get(int index) {

        compact();
        return (E) super.readLink.values[index];
    }
    
    @Override
    public Iterator<E> iterator() {
        compact();
        if(compactArray == null) { // Consider corner case where compaction hasn't occurred yet.
            return new CompactionIterator(super.readLink.values, super.totalSize);
        }
        return new CompactionIterator(compactArray, super.totalSize);
    }

    // Compacts the underlying linklist into a single link with all elements within a single array.
    private void compact() {
        
        if (super.readLink.next != null) {
            ArrayLink link = super.readLink;
            
            int newSize = (super.totalSize*3)/2 + 1;
            compactArray = new Object[newSize];
            int offset = 0;

            while(link.next != null) {
                Object[] values = link.values;
                System.arraycopy(values, 0, compactArray, offset, values.length);
                offset += values.length;
                link = link.next;      
            }
            // The last link is a corner case
            Object[] values = link.values;
            System.arraycopy(values, 0, compactArray, offset, super.totalSize - offset);

            link = new ArrayLink(compactArray);
            super.readLink = link;
            super.writeLink = link;
        }
    }
}
