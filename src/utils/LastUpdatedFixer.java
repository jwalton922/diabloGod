/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 *
 * @author Josh
 */
public class LastUpdatedFixer {

    public static void main(String[] args) {
        try {
            Mongo m = new Mongo("ds037907.mongolab.com", 37907);
            DB db = m.getDB("diablo");
            db.authenticate("diabloUser", "diabloUser".toCharArray());
            DBCollection rollingProfileInfoCollection = db.getCollection("eliteKills");
            DBObject totalObjectSearch = new BasicDBObject();
            totalObjectSearch.put("stats", "total");
            boolean foundStats = false;
            DBCursor statCursor = rollingProfileInfoCollection.find(totalObjectSearch);
            DBObject statObject = null;
            while(statCursor.hasNext()){
                statObject = statCursor.next();
            }
            
            DBObject lastUpdateSearch = new BasicDBObject();
            DBObject greaterThanZero = new BasicDBObject();
            greaterThanZero.put("$gt", 0);
            lastUpdateSearch.put("lastUpdate", greaterThanZero);
            
            DBCursor lastUpdateCursor = rollingProfileInfoCollection.find(lastUpdateSearch);
            long lastUpdateSum = 0;
            long lastUpdateCount = 0L;
            long threeMonths = 7776000000L;
            System.out.println("Calculating lastUpdate avg");
            while(lastUpdateCursor.hasNext()){
                DBObject lastUpdateObject = lastUpdateCursor.next();
                long lastUpdateTime = Long.parseLong(lastUpdateObject.get("lastUpdate").toString());
                System.out.println("Found last update time of: "+lastUpdateTime);
                if(lastUpdateTime > threeMonths){
                    lastUpdateTime = threeMonths;
                }
                
                if(lastUpdateTime < 0){
                    continue;
                }
                
                lastUpdateSum+=lastUpdateTime;
                lastUpdateCount++;;
                
            }
            
            double lastUpdateAvg = lastUpdateSum/ (1.0*lastUpdateCount);
            statObject.put("lastUpdateSum", lastUpdateSum);
            statObject.put("lastUpdateProfileCount", lastUpdateCount);
            statObject.put("lastUpdateAvg", lastUpdateAvg);
            
            rollingProfileInfoCollection.update(totalObjectSearch, statObject);
            System.out.println("Finished calculating lat update avg of : "+lastUpdateAvg);
            DBObject badLastUpateSearch = new BasicDBObject();
            DBObject lessThanZero = new BasicDBObject("$lt", 0);
            badLastUpateSearch.put("lastUpdate", lessThanZero);
            DBCursor badLastUpdateCursor = rollingProfileInfoCollection.find(badLastUpateSearch);
            System.out.println("Fixing bad updates");
            while(badLastUpdateCursor.hasNext()){
                DBObject badLastUpdate = badLastUpdateCursor.next();
                badLastUpdate.removeField("lastUpdate");
                DBObject updateSearch = new BasicDBObject("profile", badLastUpdate.get("profile"));
                rollingProfileInfoCollection.update(updateSearch, badLastUpdate);
            }
            
            System.out.println("Finished fixing bad updates");
            
                    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
