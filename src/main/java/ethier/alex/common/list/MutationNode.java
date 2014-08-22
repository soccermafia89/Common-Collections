/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

/**

Initially design the mutation nodes as non-arrays.  After finishing implementation, speed up write performance by having the write link store an array.
TODO: Recreate interface with the INSERT and REMOVE nodes to save on memory usage.

 @author alex
 */
public class MutationNode {
    protected MutationNode leftChild;
    protected MutationNode rightChild;
    
    protected MutationType mutationType;
    
    protected Object value;   
    protected int delta; // Object index is computed by summing deltas while traversing tree.
}
