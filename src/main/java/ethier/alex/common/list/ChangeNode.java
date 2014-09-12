package ethier.alex.common.list;

/**

This is used by the mutation list.  The list keeps a change log filled with change nodes.  The change node tells the list what changes to apply.

 @author Alex Ethier
 */
public class ChangeNode {
    protected int index;
    protected MutationType mutationType;

    protected Object value;
    
}
