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

/**
 *
 * @author jwalto
 */
public class ProgressChecker {
    
    public static void main(String[] args){
        String profileName = "Malice-1872";
        DBObject query = new BasicDBObject("profile", profileName);
        try {
            Mongo m = new Mongo();
            DB db = m.getDB("diablo");
            DBCollection profiles = db.getCollection("profiles");
            DBCursor cursor = profiles.find();
            int count = 0;
            while(cursor.hasNext()){
                count++;
                DBObject result = cursor.next();
                String foundProfile = (String) result.get("profile");
                if(foundProfile.equalsIgnoreCase(profileName)){
                    break;
                }
                
            }
            System.out.println(profileName+" is at the "+count+" index");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
