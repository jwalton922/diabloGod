/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import java.util.List;

/**
 *
 * @author jwalto
 */
public class DiabloData {

     private static final String MONGO_HOST = "localhost";
     private static final int MONGO_PORT = 27017;
     private static final String DB_NAME = "diablo";
     private static final String PROFILE_COLLECTION = "profiles";
     private static final String PROFILE_DATA_COLLECTION = "profileData";
     private static final String PROFILE_WITH_CHARACTERS_COLLECTION = "profilesWithCharacters";
     private Mongo m;
     private DB db;
     private DBCollection profileCollection;
     private DBCollection profileDataCollection;
     private DBCollection profileWithCharactersCollection;

     public DiabloData() {
          try {
               m = new Mongo(MONGO_HOST, MONGO_PORT);
               db = m.getDB(DB_NAME);
               profileCollection = db.getCollection(PROFILE_COLLECTION);
               profileDataCollection = db.getCollection(PROFILE_DATA_COLLECTION);
               profileWithCharactersCollection = db.getCollection(PROFILE_WITH_CHARACTERS_COLLECTION);
          } catch (Exception e) {
               e.printStackTrace();
               System.exit(0);
          }

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

     public DBCursor getProfileNames(int offset) {
          DBCursor cursor = profileCollection.find();
          cursor.skip(offset);
          return cursor;
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
          if(count == 0){
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
}
