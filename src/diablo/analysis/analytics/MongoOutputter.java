/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis.analytics;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

/**
 *
 * @author Josh
 */
public class MongoOutputter {
    private Mongo m;
    private DBCollection statsCollection;
    private boolean isTest = false;
    
    public MongoOutputter(boolean isTest){
        this.isTest = isTest;
        try {
            m = new Mongo("ds037907.mongolab.com", 37907);
            DB db = m.getDB("diablo");
            db.authenticate("diabloUser", "diabloUser".toCharArray());
            statsCollection = db.getCollection("stats");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void writeStat(DBObject object){
        if(isTest){
            System.out.println("Not writing stat, is test");
            return;
        }
        try {
            WriteResult result = statsCollection.insert(object);
            String error = result.getError();
            if(error == null || error.length() <= 0){
                System.out.println("No error writing stat object");
            } else {
                System.out.println("Possible error writing stat object: "+error);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
