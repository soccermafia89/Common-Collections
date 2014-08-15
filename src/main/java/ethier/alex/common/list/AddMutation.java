/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.list;

/**

 @author alex
 */
public class AddMutation implements Mutation {
    
    protected MutationType mutationType;
    protected Object value;

    @Override
    public int getIndex() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public MutationType getType() {
        return mutationType;
    }
    
}
