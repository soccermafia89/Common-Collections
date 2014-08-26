/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance2;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**

 @author alex
 */
public class ResultCompiler {
    
    private static Logger logger = LogManager.getLogger(ResultCompiler.class);
    private Collection<Class> listClasses;
    private int rounds;
    private int sets;
    
    public ResultCompiler(Collection<Class> testListClasses, int myRounds, int mySets) {
        listClasses = testListClasses;
        rounds = myRounds;
        sets = mySets;
    }
    
    public Map<Class, Integer> getRank(NumOperations numOperations, 
                                     double insertRatio, double mutateRatio, 
                                     int numTraversals, int numRandomAccesses) throws InstantiationException, IllegalAccessException {
        
        Map<Class, Integer> netClassRanks = new HashMap<Class, Integer>();
        Multimap<Class, Integer> ranks = HashMultimap.create();
        
        for(int i=0; i < sets; i++) {
            logger.info("Running set: {}", i);
            
            Map<Class, Long> medianScores = this.getMedianScore(numOperations, 
                                                                insertRatio, mutateRatio, 
                                                                numTraversals, numRandomAccesses);
            
            List<Class> rankedClasses = this.computeRanks(medianScores);
            for(int j=0; j < rankedClasses.size(); j++) {
                ranks.put(rankedClasses.get(j), j);  
            } 
        }
        
        for(Class clazz : ranks.keySet()) {
            Collection<Integer> classRanks = ranks.get(clazz);
            int sum = 0;
            for(int classRank : classRanks) {
                sum += classRank;
            }
            netClassRanks.put(clazz, sum);
        }
        
        return netClassRanks;
    }
    
    // A shitty way to sort a map by value.
    private List<Class> computeRanks(Map<Class, Long> medianScores) {
        
        List<Map.Entry<Class, Long>> rankedEntryList = new ArrayList<Map.Entry<Class, Long>>();
        for(Map.Entry<Class, Long> entry : medianScores.entrySet()) {
            rankedEntryList.add(entry);
        }
        
        Collections.sort(rankedEntryList, new Comparator<Map.Entry<Class, Long>>() {

            @Override
            public int compare(Map.Entry<Class, Long> entry1, Map.Entry<Class, Long> entry2) {
                return (int) (entry1.getValue() - entry2.getValue());
            }
            
        });
        
        List<Class> rankedList = new ArrayList<Class>();
        for(int i=0; i < rankedEntryList.size(); i++) {
            rankedList.add(rankedEntryList.get(i).getKey());
        }
        
        return rankedList;
    }
    
    // Run each set of tests for the specidifed number of rounds using the median round value as the result score.
    private Map<Class, Long> getMedianScore(NumOperations numOperations, 
                                     double insertRatio, double mutateRatio, 
                                     int numTraversals, int numRandomAccesses) throws InstantiationException, IllegalAccessException {
        
        
        Map<Class, Long> medianScores = new HashMap<Class, Long>();
        Multimap<Class, Long> setResults = HashMultimap.create();
        
        for(int i=0; i < rounds; i++) {
            logger.info("Running round: {}", i);
            
            TestRunner testRunner = new TestRunner(listClasses);
            Map<Class, Long> testResults = testRunner.runTests(numOperations, insertRatio, mutateRatio, numTraversals, numRandomAccesses);

            for(Class clazz : testResults.keySet()) {
                long testScore = testResults.get(clazz);
                setResults.put(clazz, testScore);
            }
        }
        
        for(Class clazz : setResults.keySet()) {
            Collection<Long> setResult = setResults.get(clazz);
            Long medianResult = this.getMedian(setResult);
            
            medianScores.put(clazz, medianResult);
        }
        
        return medianScores;
    }
    
    private Long getMedian(Collection<Long> values) {
        List<Long> valueList = new ArrayList(values);
        Collections.sort(valueList);
        
        if(valueList.size() % 2 == 0) {
            int firstMedianIndex = (valueList.size() - 1) / 2;
            int secondMedianIndex = firstMedianIndex + 1;
            
            long firstValue = valueList.get(firstMedianIndex);
            long secondValue = valueList.get(secondMedianIndex);
            return (firstValue + secondValue) / 2;
        } else {
            int medianIndex = valueList.size() / 2;
            return valueList.get(medianIndex);
        }
    }
}
