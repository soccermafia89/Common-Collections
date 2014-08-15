/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

/**

 @author alex
 */
public class RemoveMutation implements Mutation {
    
    protected MutationType mutationType;
    protected int index;

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MutationType getType() {
        return mutationType;
    }
    
}
