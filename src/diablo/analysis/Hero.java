/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import com.mongodb.DBObject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Josh
 */
public class Hero {

    private String heroClass = "unknown";
    private int id = -1;
    private int gender = -1;
    private int level;
    private int paragonLevel = -1;
    private boolean hardcore = false;
    private int eliteKills = -1;
    private long lastUpdated = -1;
    private String profileName = "unknown";
    private long dataTime = -1;
    private int accountEliteKills = -1;
    private int accountMaxParagonLevel = -1;
    //stats
    private Map<String, Double> statMap = new HashMap<String, Double>();
    private Map<String, String> itemMap = new HashMap<String, String>();
    private List<Long> heroTimes; //list of data times hero was found

    public Hero(DBObject heroObject, String profileName, String time) {
        id = (Integer) heroObject.get("id");
        heroClass = (String) heroObject.get("class");
        gender = (Integer) heroObject.get("gender");
        level = (Integer) heroObject.get("level");
        paragonLevel = (Integer) heroObject.get("paragonLevel");
        hardcore = (Boolean) heroObject.get("hardcore");
        lastUpdated = Long.parseLong(heroObject.get("last-updated").toString());
        this.profileName = profileName;
        dataTime = Long.parseLong(time);

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
        for (int i = 0; i < Constants.SLOTS.size(); i++) {
            String itemFileName = time + "_" + Constants.SLOTS.get(i) + "_" + heroId + "_" + profileName + ".txt";
            String fullItemFileName = DiabloFileReader.ITEMS_DIR + "/" + itemFileName;
            File f = new File(fullItemFileName);
            if (f.exists()) {
                itemMap.put(Constants.SLOTS.get(i), fullItemFileName);
            } else {
                //System.out.println("Could not find item file: " + fullItemFileName);
            }
        }

    }

    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getLevel() {
        //System.out.println("Returning hero level: "+level);
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getParagonLevel() {
        return paragonLevel;
    }

    public void setParagonLevel(int paragonLevel) {
        this.paragonLevel = paragonLevel;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public int getEliteKills() {
        return eliteKills;
    }

    public void setEliteKills(int eliteKills) {
        this.eliteKills = eliteKills;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    public Map<String, Double> getStatMap() {
        return statMap;
    }

    public void setStatMap(Map<String, Double> statMap) {
        this.statMap = statMap;
    }

    public Map<String, String> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<String, String> itemMap) {
        this.itemMap = itemMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountEliteKills() {
        return accountEliteKills;
    }

    public void setAccountEliteKills(int accountEliteKills) {
        this.accountEliteKills = accountEliteKills;
    }

    public int getAccountMaxParagonLevel() {
        return accountMaxParagonLevel;
    }

    public void setAccountMaxParagonLevel(int accountMaxParagonLevel) {
        this.accountMaxParagonLevel = accountMaxParagonLevel;
    }

    public List<Long> getHeroTimes() {
        return heroTimes;
    }

    public void setHeroTimes(List<Long> heroTimes) {
        this.heroTimes = heroTimes;
    }
    
    
}
