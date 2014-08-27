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
    
    public Map<Class, Double> getRank(NumOperations numOperations, 
                                     double insertRatio, double mutateRatio, 
                                     int numTraversals, int numRandomAccesses) throws InstantiationException, IllegalAccessException {
        
        TestRunner testRunner = new TestRunner(listClasses, numOperations, 
                                            insertRatio, mutateRatio,
                                            numTraversals, numRandomAccesses);
        
        Map<Class, Double> netClassRanks = new HashMap<Class, Double>();
        
        for(int i=0; i < sets; i++) {
            logger.info("Running set: {}", i);
            
            Map<Class, Long> medianScores = this.getMedianScore(testRunner);
            
            Map<Class, Double> rankedClasses = this.groupRanks(medianScores);
            logger.info("Ranks for set computed: ");
            for(Class rankedClass : rankedClasses.keySet()) {
                if(!netClassRanks.containsKey(rankedClass)) {
                    netClassRanks.put(rankedClass, rankedClasses.get(rankedClass));
                } else {
                    double currentNetRank = netClassRanks.get(rankedClass);
                    netClassRanks.put(rankedClass, currentNetRank + rankedClasses.get(rankedClass));
                }
                
                logger.info("{} had rank {}", rankedClass.getCanonicalName(), rankedClasses.get(rankedClass));
            } 
        }
        
        return netClassRanks;
    }
    
    private Map<Class, Double> groupRanks(Map<Class, Long> medianScores) {
        Map<Class, Double> trueClassRanks = new HashMap<Class, Double>();
        
        Multimap<Long, Class> scoreMap = HashMultimap.create();
        SortedSet<Long> sortedValues = new TreeSet<Long>();
        
        for(Class clazz : medianScores.keySet()) {
            scoreMap.put(medianScores.get(clazz), clazz);
            sortedValues.add(medianScores.get(clazz));
        }
        
        
        double baseRank = 0.0D;
        for(Long value : sortedValues) {
            Collection<Class> classes = scoreMap.get(value);
            
            double rankModifier = 0.0D;
            if(classes.size() > 1) {
                rankModifier = (classes.size()*(classes.size() - 1) / 2D) / classes.size();
            }
            
            double trueRank = baseRank + rankModifier;
            for(Class clazz : classes) {
                trueClassRanks.put(clazz, trueRank);
            }
            baseRank += classes.size();
        }
        
        return trueClassRanks;
    }
    
    // A shitty way to sort a map by value.
//    private List<Class> computeRanks(Map<Class, Long> medianScores) {
//        
//        List<Map.Entry<Class, Long>> rankedEntryList = new ArrayList<Map.Entry<Class, Long>>();
//        for(Map.Entry<Class, Long> entry : medianScores.entrySet()) {
//            rankedEntryList.add(entry);
//        }
//        
//        Collections.sort(rankedEntryList, new Comparator<Map.Entry<Class, Long>>() {
//
//            @Override
//            public int compare(Map.Entry<Class, Long> entry1, Map.Entry<Class, Long> entry2) {
//                return (int) (entry1.getValue() - entry2.getValue());
//            }
//            
//        });
//        
//        List<Class> rankedList = new ArrayList<Class>();
//        for(int i=0; i < rankedEntryList.size(); i++) {
//            rankedList.add(rankedEntryList.get(i).getKey());
//        }
//        
//        return rankedList;
//    }
    
    // Run each set of tests for the specidifed number of rounds using the median round value as the result score.
    private Map<Class, Long> getMedianScore(TestRunner testRunner) throws InstantiationException, IllegalAccessException {
        
        
        Map<Class, Long> medianScores = new HashMap<Class, Long>();
        Multimap<Class, Long> setResults = HashMultimap.create();
        
        for(int i=0; i < rounds; i++) {
            logger.info("Running round: {}", i);
            
            Map<Class, Long> testResults = testRunner.runTests();

            for(Class clazz : testResults.keySet()) {
                long testScore = testResults.get(clazz);
                setResults.put(clazz, testScore);
            }
        }
        
        for(Class clazz : setResults.keySet()) {
            Collection<Long> setResult = setResults.get(clazz);
            Long medianResult = this.getMedian(setResult);
            
            medianScores.put(clazz, medianResult);
            logger.info("{} had median score {}", clazz.getCanonicalName(), medianResult);
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
