/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

/**

This class is used by the MutationList.  Any insert or remove on the list is stored in a tree instead of being applied immediately.
The change events are thus wrapped in tree nodes.
TODO: Recreate interface with the INSERT and REMOVE nodes to save on memory usage.

 @author Alex Ethier
 */
public class MutationNode {
    protected MutationNode leftChild;
    protected MutationNode rightChild;
    
    protected MutationType mutationType;
    
    protected Object value;   
    protected int delta; // Object index is computed by summing deltas while traversing tree.
}
