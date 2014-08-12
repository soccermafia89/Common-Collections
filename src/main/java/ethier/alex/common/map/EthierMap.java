/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.map;

import java.util.ArrayDeque;
import java.util.Iterator;

/**

A faster implementation of HashMap under certain circumstances.

This map implementation assumes that once the map is being read from, no more writes will occur.

 @author alex
 */
public class EthierMap <K, V> {
    
    // Consider loading data with DOUBLES (key value pairs or entries).
    private ArrayDeque<Couple> couples;
    private ArrayDeque[] buckets;  
    int numBuckets;
    
    
    public EthierMap() {
        couples = new ArrayDeque();
    }
    
    public void put(K key, V value) {
        Couple couple = new Couple(key, value);
        couples.add(couple);
    }
    
    public void computeMap() {
        
        numBuckets = couples.size();
        
        buckets = new ArrayDeque[numBuckets];
        
        
        Iterator<Couple> it = couples.iterator();
        while(it.hasNext()) {
            Couple couple = it.next();
            
            int hashCode = couple.key.hashCode() & (numBuckets -1);
//            if(hashCode < 0) {
//                hashCode = -1*hashCode;
//            }
            int bucket = hashCode % numBuckets;
            
            ArrayDeque bucketList = buckets[bucket];
            if(bucketList == null) {
                bucketList = new ArrayDeque();
                bucketList.add(couple);
                buckets[bucket] = bucketList;
            } else {
                bucketList.add(couple);
            }
        }
        
        couples = null;
    }
   
    public V get(K key) {

        int bucket = key.hashCode() & (numBuckets -1);
        
        ArrayDeque<Couple> bucketList = buckets[bucket];
        
        Iterator<Couple> it = bucketList.iterator();
        while(it.hasNext()) {
            Couple nextCouple = it.next();
            K bucketKey = (K)nextCouple.key;
            if(bucketKey == key || bucketKey.equals(key)) {
                return (V)nextCouple.value;
            }
        }
        
        return null;
    }
}
