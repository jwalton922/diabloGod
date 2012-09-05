/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
    private Mongo m;
    private DB db;
    private DBCollection profileCollection;
    
    public DiabloData() {
        try {
            m = new Mongo(MONGO_HOST, MONGO_PORT);
            db = m.getDB(DB_NAME);
            profileCollection = db.getCollection(PROFILE_COLLECTION);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
    }
    
    public void insertProfiles(List<String> profiles){
        System.out.println("insertProfiles called on "+profiles.size()+" profiles");
        int countInserted = 0;
        for(String profile : profiles){
            DBObject query = new BasicDBObject("profile", profile);
            long count = profileCollection.count(query);
            if(count > 0){
                System.out.println("Already have profile, skipping insert");
            } else {
                WriteResult result = profileCollection.insert(query);
                String error = result.getError();
                if(error != null && error.length() > 0){
                    System.out.println("ERROR INSERTING RESULTS INTO MONGO!!!!!! "+error);
                } else {
                    countInserted++;
                }
            }
        }
        
        System.out.println("Finished with insertion. Inserted "+countInserted+" new profiles");
    }
}
