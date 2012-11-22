/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josh
 */
public class CombinationGenerator {

    public static List<List<Integer>> generateCombinations(int numberInSet, int choice) {
        List<List<Integer>> combinations = new ArrayList<List<Integer>>();
        if(choice > numberInSet){
            return combinations;
        }
        processChoice(null, 0, numberInSet, choice, combinations);

        return combinations;
    }

    private static void processChoice(List<Integer> choices, int startIndex, int numInSet, int numberToChoose, List<List<Integer>> combinations) {
//        if (choices != null) {
//            System.out.println("processChoice called: currentChoices: " + choices.toString() + " startIndex = " + startIndex);
//        } else {
//            System.out.println("processChoice called: currentChoices: null startIndex = " + startIndex);
//        }

        for (int i = startIndex; i < numInSet; i++) {
            if (choices == null) {
                choices = new ArrayList<Integer>();
            }
            List<Integer> choiceCopy = new ArrayList<Integer>();
            choiceCopy.addAll(choices);
            choiceCopy.add(i);
            if (choiceCopy.size() < numberToChoose) {
                
                processChoice(choiceCopy, i + 1, numInSet, numberToChoose, combinations);
            } else {
                
                combinations.add(choiceCopy);
                
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List<List<Integer>> combinations = CombinationGenerator.generateCombinations(8, 5);
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("Took "+time+" ms to generate "+combinations.size());
        
//        for (int i = 0; i < combinations.size(); i++) {
//            System.out.println("Combination: "+combinations.get(i).toString());
//        }
    }
}
