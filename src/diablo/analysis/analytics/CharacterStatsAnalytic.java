/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis.analytics;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import diablo.analysis.Constants;
import diablo.analysis.DataListener;
import diablo.analysis.Hero;
import diablo.analysis.Item;
import diablo.analysis.Profile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Josh
 */
public class CharacterStatsAnalytic implements DataListener{

    private Map<String, Double> classToParagonLvlSum = new HashMap<String, Double>();
    private Map<String, List<Integer>> classToParagonBins = new HashMap<String,List<Integer>>();
    private Map<String, Double> classToEliteKillSum = new HashMap<String, Double>();
    private Map<String,Map<String,Double>> classToStatMap = new HashMap<String, Map<String,Double>>();
    private Map<String,Map<String,List<Double>>> classToStatListMap = new HashMap<String, Map<String,List<Double>>>();
    private Map<String,Integer> classToCountMap = new HashMap<String,Integer>();
    private MongoOutputter outputter;
    private String date;
    
    public CharacterStatsAnalytic(MongoOutputter outputter, String date){
        this.outputter = outputter;
        this.date = date;
        for(String characterClass : Constants.CHARACTER_CLASSES){
            classToStatMap.put(characterClass, new HashMap<String,Double>());
            for(String stat: Constants.STATS){
                classToStatMap.get(characterClass).put(stat, 0.0);
            }
            classToStatListMap.put(characterClass, new HashMap<String,List<Double>>());
            classToCountMap.put(characterClass, 0);
            classToParagonLvlSum.put(characterClass, 0.0);
            classToEliteKillSum.put(characterClass, 0.0);
            List<Integer> classBins = new ArrayList<Integer>();
            for(int i = 0; i <= 100; i++){
                classBins.add(0);
            }
            classToParagonBins.put(characterClass, classBins);
        }
    }
    
    @Override
    public void processProfile(Profile profile) {
        
    }

    @Override
    public void processHero(Hero hero) {
        Map<String, Double> heroStatMap = hero.getStatMap();
        classToCountMap.put(hero.getHeroClass(), classToCountMap.get(hero.getHeroClass())+1);
        List<Integer> classBins = classToParagonBins.get(hero.getHeroClass());
        int newCount = classBins.get(hero.getParagonLevel())+1;
        classBins.set(hero.getParagonLevel(), newCount);
        classToParagonLvlSum.put(hero.getHeroClass(), classToParagonLvlSum.get(hero.getHeroClass()) + hero.getParagonLevel() );
        classToEliteKillSum.put(hero.getHeroClass(), classToEliteKillSum.get(hero.getHeroClass()) + hero.getEliteKills() );
        for(String stat : heroStatMap.keySet()){
            double statValue = heroStatMap.get(stat);
            
            double statSum = classToStatMap.get(hero.getHeroClass()).get(stat);
            statSum+=statValue;
            classToStatMap.get(hero.getHeroClass()).put(stat, statSum);
            List<Double> stats = classToStatListMap.get(hero.getHeroClass()).get(stat);
            if(stats == null){
                stats = new ArrayList<Double>();
                classToStatListMap.get(hero.getHeroClass()).put(stat, stats);
            }
            stats.add(statValue);
        }
    }

    @Override
    public void processItem(Item item) {
        
    }

    @Override
    public void outputResults() {
        Map<String, Map<String,Double>> classToAvgStatMap = new HashMap<String,Map<String,Double>>();
        Map<String, Double> classToAvgParagonLvl = new HashMap<String,Double>();
        Map<String, Double> classToAvgEliteKills = new HashMap<String,Double>();
        for(String charClass : classToStatMap.keySet()){
            double averageParagonLvl = classToParagonLvlSum.get(charClass)/ (1.0*classToCountMap.get(charClass));
            double averageEliteKills = classToEliteKillSum.get(charClass)/ (1.0*classToCountMap.get(charClass));
            classToAvgParagonLvl.put(charClass, averageParagonLvl);
            classToAvgEliteKills.put(charClass, averageEliteKills);
            Map<String,Double> statToSumMap = classToStatMap.get(charClass);
            Map<String,Double> statAvgMap = new HashMap<String,Double>();
            for(String stat : statToSumMap.keySet()){
                double statSum = statToSumMap.get(stat);
                double avg = statSum/(1.0*classToCountMap.get(charClass));
                statAvgMap.put(stat, avg);
            }
            
            classToAvgStatMap.put(charClass, statAvgMap);
        }
        
        //calculate standard deviation
        Map<String, Map<String,Double>> classToStatStandardDevMap = new HashMap<String,Map<String,Double>>();
        for(String charClass : classToStatListMap.keySet()){
            Map<String, List<Double>> statToListMap = classToStatListMap.get(charClass);
            Map<String, Double> statToStandardDev = new HashMap<String,Double>();
            for(String stat: statToListMap.keySet()){
                List<Double> stats = statToListMap.get(stat);
                double mean = classToAvgStatMap.get(charClass).get(stat);
                double sum = 0;
                for(Double statValue : stats){
                    sum+=Math.pow(statValue-mean, 2);
                }
                
                double variance = sum / (1.0*classToCountMap.get(charClass));
                double standardDeviation = Math.sqrt(variance);
                
                statToStandardDev.put(stat, standardDeviation);
                
            }
            
            classToStatStandardDevMap.put(charClass, statToStandardDev);
            
        }
        
        DBObject outputObject = new BasicDBObject();
        outputObject.put("classToCountMap", classToCountMap);
        outputObject.put("classToStatAvgMap", classToAvgStatMap);
        outputObject.put("classToStatStandardDevMap", classToStatStandardDevMap);
        outputObject.put("classAvgEliteKillsMap",classToAvgEliteKills);
        outputObject.put("classAvgParagonLevels", classToAvgParagonLvl);
        outputObject.put("classParagonLevelBins", classToParagonBins);
        outputObject.put("analytic-name", this.getClass().getName());
        outputObject.put("date", date);
        System.out.println("character stats analysis results: "+outputObject.toString());
        
        this.outputter.writeStat(outputObject);        
    }
    
}
