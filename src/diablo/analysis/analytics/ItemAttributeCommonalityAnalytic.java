/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis.analytics;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import diablo.analysis.CombinationGenerator;
import diablo.analysis.Constants;
import diablo.analysis.DataListener;
import diablo.analysis.Hero;
import diablo.analysis.Item;
import diablo.analysis.Profile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.helper.StringUtil;

/**
 *
 * @author Josh
 */
public class ItemAttributeCommonalityAnalytic implements DataListener {

    private MongoOutputter outputter;
    private String date;
    private HashMap<Integer, List<List<Integer>>> twoSetCombinationMap = new HashMap<Integer, List<List<Integer>>>();
    private HashMap<Integer, List<List<Integer>>> threeSetCombinationMap = new HashMap<Integer, List<List<Integer>>>();
    private HashMap<Integer, List<List<Integer>>> fourSetCombinationMap = new HashMap<Integer, List<List<Integer>>>();
    private HashMap<Integer, List<List<Integer>>> fiveSetCombinationMap = new HashMap<Integer, List<List<Integer>>>();
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> classToItemComboCountMap = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();
    private HashMap<String, HashMap<String, Integer>> classToSlotCountMap = new HashMap<String, HashMap<String, Integer>>();
    private List<String> attributesToIgnore = new ArrayList<String>();

    public ItemAttributeCommonalityAnalytic(MongoOutputter outputter, String date) {
        
        attributesToIgnore.add("Durability_Cur");
        attributesToIgnore.add("Durability_Max");
        attributesToIgnore.add("Armor_Item");
        attributesToIgnore.add("Attacks_Per_Second_Item");
        
        this.outputter = outputter;
        this.date = date;
        for (String charClass : Constants.CHARACTER_CLASSES) {
            HashMap<String, HashMap<String, Integer>> slotToCountMap = new HashMap<String, HashMap<String, Integer>>();
            HashMap<String, Integer> slotCountMap = new HashMap<String, Integer>();
            for (String slot : Constants.SLOTS) {
                slotCountMap.put(slot, 0);
                HashMap<String, Integer> itemComboCountMap = new HashMap<String, Integer>();
                slotToCountMap.put(slot, itemComboCountMap);

            }
            classToItemComboCountMap.put(charClass, slotToCountMap);
            classToSlotCountMap.put(charClass, slotCountMap);
        }

        for (int i = 2; i <= 17; i++) {
            twoSetCombinationMap.put(i, CombinationGenerator.generateCombinations(i, 2));
            threeSetCombinationMap.put(i, CombinationGenerator.generateCombinations(i, 3));
            fourSetCombinationMap.put(i, CombinationGenerator.generateCombinations(i, 4));
            fiveSetCombinationMap.put(i, CombinationGenerator.generateCombinations(i, 5));
        }
        System.out.println("Finished Initializing ItemAttributeCommonalityAnalytic");
    }

    @Override
    public void processProfile(Profile profile) {
    }

    @Override
    public void processHero(Hero hero) {
    }

    @Override
    public void processItem(Item item) {
        String heroClass = item.getHeroClass();
        Set<String> itemStats = item.getItemStats().keySet();
        List<String> itemStatList = new ArrayList<String>(itemStats);
        for(int i = 0; i < attributesToIgnore.size(); i++){
            itemStatList.remove(attributesToIgnore.get(i));
        }
        ArrayList<Integer> indicesToRemove = new ArrayList<Integer>();
        for(int i = 0; i < itemStatList.size(); i++){
            if(itemStatList.get(i).indexOf("Damage_Weapon") >= 0){
                indicesToRemove.add(i);
            }
        }
        ArrayList<String> itemStatListCondensed = new ArrayList<String>();
        for(int i = 0; i < itemStatList.size(); i++){
            if(!indicesToRemove.contains(i)){
                itemStatListCondensed.add(itemStatList.get(i));
            }
        }
        itemStatList = itemStatListCondensed;
        if (itemStatList == null) {
            System.out.println("Item stats were null, skipping");
            return;
        }

        if (itemStatList.size() < 2) {
            System.out.println("Item had fewer than 2 stats");
            return;
        }
        String slot = item.getSlot();
        Map<String, Integer> slotToCountMap = classToSlotCountMap.get(heroClass);
        Integer slotCount = slotToCountMap.get(slot);
        if (slotCount == null) {
            slotCount = 0;
        }
        slotToCountMap.put(slot, slotCount + 1);
        Map<String, Integer> comboCountMap = classToItemComboCountMap.get(heroClass).get(slot);
        
        if(itemStatList.size() >= 2){
            List<List<Integer>> twoCombinations = twoSetCombinationMap.get(itemStatList.size());
            //System.out.println("item stats: "+itemStatList.toString()+" combinations: "+threeCombinations.size()+" countMap: "+comboCountMap.size());
            processItemCombinations(itemStatList, twoCombinations, comboCountMap);
        }
        
        if (itemStatList.size() >= 3) {
            //System.out.println("ITem stat list size: "+itemStatList.size());
            List<List<Integer>> threeCombinations = threeSetCombinationMap.get(itemStatList.size());
            //System.out.println("item stats: "+itemStatList.toString()+" combinations: "+threeCombinations.size()+" countMap: "+comboCountMap.size());
            processItemCombinations(itemStatList, threeCombinations, comboCountMap);
        }

        if (itemStatList.size() >= 4) {
            List<List<Integer>> fourCombinations = fourSetCombinationMap.get(itemStatList.size());
            processItemCombinations(itemStatList, fourCombinations, comboCountMap);
        }

        if (itemStatList.size() >= 5) {
            List<List<Integer>> fiveCombinations = fiveSetCombinationMap.get(itemStatList.size());
            processItemCombinations(itemStatList, fiveCombinations, comboCountMap);
        }

    }

    private void processItemCombinations(List<String> itemStats, List<List<Integer>> combinations, Map<String, Integer> comboCountMap) {
        for (int i = 0; i < combinations.size(); i++) {
            List<Integer> combination = combinations.get(i);
            List<String> itemsInCombination = new ArrayList<String>();
            //get item stats at the indices specified in the combination
            for (int j = 0; j < combination.size(); j++) {
                itemsInCombination.add(itemStats.get(combination.get(j)));
            }

            Collections.sort(itemsInCombination);
            String itemKey = StringUtil.join(itemsInCombination, "|||");
            Integer count = comboCountMap.get(itemKey);
            if (count == null) {
                count = 0;
            }
            count = count + 1;
            comboCountMap.put(itemKey, count);
        }
    }

    @Override
    public void outputResults() {

        DBObject outputObject = new BasicDBObject();
        //loop over classes
        for (String charClass : classToItemComboCountMap.keySet()) {
            //for each class, look at each slot
            HashMap<String, HashMap<String, Integer>> slotToAttributeCountMap = classToItemComboCountMap.get(charClass);
            DBObject classObject = new BasicDBObject();
            for (String slot : slotToAttributeCountMap.keySet()) {
                //for each slot get the most popular attribute for combinations of 3 stats, 4 stats, and 5 stats
                DBObject slotObject = new BasicDBObject();
                HashMap<String, Integer> attributeCombosToCountMap = slotToAttributeCountMap.get(slot);
                DBObject twoAttributeObject = new BasicDBObject();
                DBObject threeAttributeObject = new BasicDBObject();
                DBObject fourAttributeObject = new BasicDBObject();
                DBObject fiveAttributeObject = new BasicDBObject();

                String[] maxTwoItemKey = null;
                String[] maxThreeItemKey = null;
                String[] maxFourItemKey = null;
                String[] maxFiveItemKey = null;
                int maxTwoItem = -1;
                int maxThreeItem = -1;
                int maxFourItem = -1;
                int maxFiveItem = -1;

                for (String itemKey : attributeCombosToCountMap.keySet()) {
                    String[] attributes = itemKey.split("\\|\\|\\|");
                    int count = attributeCombosToCountMap.get(itemKey);
                    
                    
                    if (attributes.length == 2) {
                        if (count > maxTwoItem) {
                            maxTwoItem = count;
                            maxTwoItemKey = attributes;
                        }
                    } else if (attributes.length == 3) {
                        if (count > maxThreeItem) {
                            maxThreeItem = count;
                            maxThreeItemKey = attributes;
                        }
                    } else if (attributes.length == 4) {
                        if (count > maxFourItem) {
                            maxFourItem = count;
                            maxFourItemKey = attributes;
                        }
                    } else if (attributes.length == 5) {
                        if (count > maxFiveItem) {
                            maxFiveItem = count;
                            maxFiveItemKey = attributes;
                        }
                    }
                } //end attribute combo map

                twoAttributeObject.put("attributes", maxTwoItemKey);
                twoAttributeObject.put("count", maxTwoItem);
                threeAttributeObject.put("attributes", maxThreeItemKey);
                threeAttributeObject.put("count", maxThreeItem);
                fourAttributeObject.put("attributes", maxFourItemKey);
                fourAttributeObject.put("count", maxFourItem);
                fiveAttributeObject.put("attributes", maxFiveItemKey);
                fiveAttributeObject.put("count", maxFiveItem);

                slotObject.put("twoAttribute", twoAttributeObject);
                slotObject.put("threeAttribute", threeAttributeObject);
                slotObject.put("fourAttribute", fourAttributeObject);
                slotObject.put("fiveAttribute", fiveAttributeObject);
                classObject.put(slot, slotObject);
            } //end slot for loop

            outputObject.put(charClass, classObject);
        } //end class for loop

        outputObject.put("analytic-name", this.getClass().getName());
        outputObject.put("date", date);
        System.out.println("character stats analysis results: " + outputObject.toString());

        outputter.writeStat(outputObject);
    }

    public static void main(String[] args) {
        String test = "attr1|||attr2|||attr3|||atttr4";
        String[] testSplit = test.split("\\|\\|\\|");
        System.out.println(testSplit.length);
    }
}
