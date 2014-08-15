/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**

 @author alex
 */
public class BigObject {
    
    private int id;
    private String data;
    
    public BigObject(int size, int myId) {
        id = myId;
        data = RandomStringUtils.random(size);
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof BigObject) {
            BigObject oBig = (BigObject) o;
            return id == oBig.id;
        }
        
        return false;
    }
    
    public int getId() {
        return id;
    }
}
