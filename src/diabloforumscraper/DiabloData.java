/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author jwalto
 */
public class DiabloData {

     private static Logger log = Logger.getLogger(DiabloData.class);
     private static final String MONGO_HOST = "localhost";
     private static final String MONGO_LAB_HOST = "ds037907.mongolab.com";
     private static final int MONGO_LAB_PORT = 37907;
     private static final int MONGO_PORT = 27017;
     private static final String DB_NAME = "diablo";
     private static final String PROFILE_COLLECTION = "profiles";
     private static final String PROFILE_DATA_COLLECTION = "profileData";
     private static final String PROFILE_WITH_CHARACTERS_COLLECTION = "profilesWithCharacters";
     private static final String ITEM_COLLECTION = "items";
     private Mongo m;
     private Mongo mongoLabM;
     private DB db;
     private DB mongoLabDB;
     private DBCollection profileCollection;
     private DBCollection profileDataCollection;
     private DBCollection profileWithCharactersCollection;
     private DBCollection itemCollection;

     public DiabloData() {
          try {
               m = new Mongo(MONGO_HOST, MONGO_PORT);
               mongoLabM = new Mongo(MONGO_LAB_HOST,MONGO_LAB_PORT);
               db = m.getDB(DB_NAME);
               mongoLabDB = mongoLabM.getDB(DB_NAME);
               String password = "diabloUser";
               mongoLabDB.authenticate("diabloUser", password.toCharArray());
               profileCollection = db.getCollection(PROFILE_COLLECTION);
               profileDataCollection = db.getCollection(PROFILE_DATA_COLLECTION);
               profileWithCharactersCollection = db.getCollection(PROFILE_WITH_CHARACTERS_COLLECTION);
               itemCollection = mongoLabDB.getCollection(ITEM_COLLECTION);
          } catch (Exception e) {
               e.printStackTrace();
               System.exit(0);
          }

     }

     public int determineProfileIndex(String profile){
          int index = -1;
          DBCursor cursor = profileCollection.find();
          while(cursor.hasNext()){
               index++;
               DBObject profileObject = cursor.next();
               String foundProfile = (String) profileObject.get("profile");
               if(profile.equalsIgnoreCase(foundProfile)){
                    break;
               }
          }

          return index;
     }

     public boolean haveItemDataForProfile(String profile) {
          DBObject query = new BasicDBObject();
          query.put("profile-name", profile);
          long count = itemCollection.count(query);
          if (count == 0) {
               log.debug("Do not have any item data for: " + profile);
               return false;
          } else {
               log.debug("Already have item data for profile: " + profile);
               return true;
          }
     }

     public void insertItemData(DBObject itemData) {
          itemCollection.insert(itemData);
     }

     public void insertProfileData(DBObject profileData) {
          DBObject query = new BasicDBObject();
          query.put("profile-name", profileData.get("profile-name"));
          System.out.println("Query: " + query.toString());
          long count = profileDataCollection.count(query);
          System.out.println("Found " + count + " results");
          if (count == 0) {
               profileDataCollection.insert(profileData);
          }
     }
     
     public int getProfileNamesCount(){
         int count = Integer.parseInt(""+profileCollection.count());
         System.out.println("There are "+count+" profiles");
         return count;
         
     }

     public DBCursor getProfileNames(int offset, int limit) {
          DBCursor cursor = profileCollection.find();
          cursor = cursor.skip(offset);
          cursor = cursor.limit(limit);
          cursor = cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
          return cursor;
     }

     public DBCursor getItemCursor(){
          return itemCollection.find();
     }

     public DBCursor getProfileData(int offset) {
          DBCursor cursor = profileDataCollection.find();
          cursor.skip(offset);
          return cursor;
     }

     public void insertProfileWithCharacterData(DBObject profileObject) {
          DBObject query = new BasicDBObject();
          query.put("profile-name", profileObject.get("profile-name"));
          long count = profileWithCharactersCollection.count(query);
          if (count == 0) {
               profileWithCharactersCollection.insert(profileObject);
          }
     }

     public void insertProfiles(List<String> profiles) {
          //System.out.println("insertProfiles called on "+profiles.size()+" profiles");
          int countInserted = 0;
          for (String profile : profiles) {
               DBObject query = new BasicDBObject("profile", profile);
               long count = profileCollection.count(query);
               if (count > 0) {
                    //System.out.println("Already have profile, skipping insert");
               } else {
                    WriteResult result = profileCollection.insert(query);
                    String error = result.getError();
                    if (error != null && error.length() > 0) {
                         System.out.println("ERROR INSERTING RESULTS INTO MONGO!!!!!! " + error);
                    } else {
                         countInserted++;
                    }
               }
          }

          System.out.println("Finished with insertion. Inserted " + countInserted + " new profiles");
     }

     public static void main(String[] args){
          DiabloData database = new DiabloData();
          int index = database.determineProfileIndex("RebelX924-1995");
          System.out.println("index = "+index);
     }
}
