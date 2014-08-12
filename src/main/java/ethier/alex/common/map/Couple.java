/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.map;

/**

 @author alex
 */
public class Couple<V,K> {
    
    public final V key;
    public final K value;
    
    public Couple(V myKey, K myValue) {
        key = myKey;
        value = myValue;
    }
}
