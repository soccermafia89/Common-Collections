/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**

TODO: consider a cache method.

When a user calls the cache on an index, it is added to the cache (does not actually have to occur internally this way).
When a user calls dumpCache() an array is returned with the elements in the order that they were attached to the cache.

This allows get mutations to be added, a more optimized compaction will only occur when dumpCache is called.


 @author alex
 */
public class MutationList<E> implements List<E> {

    private List<Mutation> mutationList;
    private Object[] compactArray;
    private int totalSize;

    public MutationList() {
        mutationList = new ArrayLinkList();
    }

    @Override
    public boolean add(E e) {
        AddMutation mutation = new AddMutation();
        mutation.mutationType = MutationType.ADD;
        mutation.value = e;    
        mutationList.add(mutation);
        return true;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new MutationIterator();
    }
    
    // Converts the write buffer mutationList into the read buffer compactArray
    private void compact() {
        
    }
    
    // Generates a new mutationList with bulk adds, inserts, and removals to aid in the compation process
    // This means finding if mutations are cummutative, if a group of mutations are they can be groups into bulk processes.
    private void minorCompact() {
        /* 
        
        There are three cummutivity pairings:
        (add, insert)
        (add, remove)
        (insert, remove)
        
        Creating the following groupings will offer performance enhancements:
        
        (add) -> can converts add chains into an array that is bulk copied to the compact array.
        (add, remove) -> Either removals negate adds, or they can be moved to the end allowing a bulk add to occur.
        (insert, remove) -> can bulk shift elements once instead of many times.
        
        */
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
    
    Iterator
    
    */
    
    private class MutationIterator<E> implements Iterator<E> {
        
        private int offset;
        
        private MutationIterator() {
            offset = 0;
        }

        @Override
        public boolean hasNext() {
            return offset < totalSize;
        }

        @Override
        public E next() {
            return (E) compactArray[offset++];
        }

        @Override
        public void remove() {
            RemoveMutation mutation = new RemoveMutation();
            mutation.mutationType = MutationType.REMOVE;
            mutation.index = offset;
            
            mutationList.add(mutation);
        }
    }
}
