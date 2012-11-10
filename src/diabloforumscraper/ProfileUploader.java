/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 *
 * @author jwalton
 */
public class ProfileUploader {

    public static void uploadProfiles() {
        try {
            Mongo mongolab = new Mongo("ds037907.mongolab.com", 37907);
            Mongo localmongo = new Mongo("localhost", 27017);
            DB db = mongolab.getDB("diablo");
            db.authenticate("diabloUser","diabloUser".toCharArray());
            DB localdb = localmongo.getDB("Downloads");
            DBCollection mlProfileCollection = db.getCollection("profiles");
            DBCollection profileCollection = localdb.getCollection("profiles");
            DBCursor cursor = profileCollection.find();
            while(cursor.hasNext()){
                DBObject po = cursor.next();
                System.out.println("Uploading "+po.get("profile"));
                mlProfileCollection.insert(po);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        ProfileUploader.uploadProfiles();
    }
}
