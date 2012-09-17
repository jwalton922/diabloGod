/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jwalton
 */
public class AffixFinder {

     private DiabloData database;
     private static String affixFile = "/home/jwalton/affixList.txt";

     public AffixFinder(DiabloData database) {
          this.database = database;
     }

     public void findAndWriteAffixesToFile() {
          BufferedWriter bw = null;
          try {
               bw = new BufferedWriter(new FileWriter(affixFile));
               Set<String> affixes = new HashSet<String>();
               DBCursor itemCursor = database.getItemCursor();
               itemCursor.limit(20000);
               while (itemCursor.hasNext()) {
                    DBObject itemObject = itemCursor.next();
                    for (String key : itemObject.keySet()) {
                         if (!affixes.contains(key)) {
                              System.out.println("Found affix: "+key);
                              affixes.add(key);
                              bw.write(key+"\n");
                         }
                    }
               }

               bw.flush();


          } catch (Exception e) {
               e.printStackTrace();
          }
     }

     public static void main(String[] args){
          DiabloData data = new DiabloData();
          AffixFinder finder = new AffixFinder(data);
          finder.findAndWriteAffixesToFile();
     }
}
