/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import com.mongodb.DBObject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Josh
 */
public class Hero {

    private String heroClass = "unknown";
    private int gender = -1;
    private int level;
    private int paragonLevel = -1;
    private boolean hardcore = false;
    private int eliteKills = -1;
    private long lastUpdated = -1;
    //stats
    private Map<String, Double> statMap = new HashMap<String, Double>();
    private Map<String, String> itemMap = new HashMap<String, String>();

    public Hero(DBObject heroObject, String profileName, String time) {
        heroClass = (String) heroObject.get("class");
        gender = (Integer) heroObject.get("gender");
        level = (Integer) heroObject.get("level");
        paragonLevel = (Integer) heroObject.get("paragonLevel");
        hardcore = (Boolean) heroObject.get("hardcore");
        lastUpdated = Long.parseLong(heroObject.get("lastUpdated").toString());

        DBObject killObject = (DBObject) heroObject.get("kills");
        if (killObject.get("elites") != null) {
            eliteKills = (Integer) killObject.get("elites");
        }

        DBObject statObject = (DBObject) heroObject.get("stats");
        
        for (int i = 0; i < Constants.STATS.size(); i++) {
            String statValue = statObject.get(Constants.STATS.get(i)).toString();
            if (statValue != null) {
                statMap.put(Constants.STATS.get(i), Double.parseDouble(statValue));
            }
        }
        String heroId = heroObject.get("id").toString();
        for(int i = 0; i < Constants.SLOTS.size(); i++){
            String itemFileName = time+"_"+Constants.SLOTS.get(i)+"_"+heroId+"_"+profileName+".txt";
            String fullItemFileName = DiabloFileReader.ITEMS_DIR+"/"+itemFileName;
            File f = new File(fullItemFileName);
            if(f.exists()){
                itemMap.put(Constants.SLOTS.get(i), fullItemFileName );
            } else {
                System.out.println("Could not find item file: "+fullItemFileName);
            }
        }

    }
}
