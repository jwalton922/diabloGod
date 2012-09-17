/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author jwalton
 */
public class DiabloDataGatherer {

     private static Logger log = Logger.getLogger(DiabloDataGatherer.class);
     private DiabloData database;
     private DiabloItemCreator itemCreator;

     public DiabloDataGatherer(DiabloData database) {
          this.database = database;
          this.itemCreator = new DiabloItemCreator();
     }

     public void gatherItemData(int offset) {
          DBCursor profileCursor = database.getProfileNames(offset);

          while (profileCursor.hasNext()) {
               DBObject profileObject = profileCursor.next();
               String profile = (String) profileObject.get("profile");
               if (!database.haveItemDataForProfile(profile)) {
                    log.info("Getting item data for profile: " + profile);
                    List<DiabloItemRecord> items = itemCreator.createDiabloItems(profile);
                    log.debug("Have "+items.size()+" items to insert");
                    for (DiabloItemRecord item : items) {
                         database.insertItemData(item.toDBObject());
                    }
               } else {
                    log.info("skipping profile: "+profile+". Already have item data");
               }
          }
     }

     public static void main(String[] args) {
          DiabloData database = new DiabloData();
          DiabloDataGatherer dataGatherer = new DiabloDataGatherer(database);
          dataGatherer.gatherItemData(0);
     }
}
