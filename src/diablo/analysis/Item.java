/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import com.mongodb.DBObject;
import java.util.HashMap;

/**
 *
 * @author Josh
 */
public class Item {
    private String slot = "unknown";
    private HashMap<String, Double> itemStats = new HashMap<String, Double>();
    private String heroClass = "unknown";
    private int accountEliteKills = -1;
    private int characterEliteKills = -1;
    private int accountParagonlevel = -1;
    private int characterParagonLevel = -1;
    private long dataTime;

    public Item(DBObject itemObject, Profile profile, Hero hero, String slot) {
        this.slot = slot;
        heroClass = hero.getHeroClass();
        accountEliteKills = profile.getEliteKills();
        characterEliteKills = hero.getEliteKills();
        characterParagonLevel = hero.getParagonLevel();
        if (itemObject != null && itemObject.get("attributesRaw") != null) {
            DBObject attributes = (DBObject) itemObject.get("attributesRaw");
            for (String attribute : attributes.keySet()) {
                DBObject valueObject = (DBObject) attributes.get(attribute);
                double value = Double.parseDouble(valueObject.get("max").toString());
                itemStats.put(attribute, value);
            }
        }

    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    } 
    

    public HashMap<String, Double> getItemStats() {
        return itemStats;
    }

    public void setItemStats(HashMap<String, Double> itemStats) {
        this.itemStats = itemStats;
    }

    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public int getAccountEliteKills() {
        return accountEliteKills;
    }

    public void setAccountEliteKills(int accountEliteKills) {
        this.accountEliteKills = accountEliteKills;
    }

    public int getAccountParagonlevel() {
        return accountParagonlevel;
    }

    public void setAccountParagonlevel(int accountParagonlevel) {
        this.accountParagonlevel = accountParagonlevel;
    }

    public int getCharacterParagonLevel() {
        return characterParagonLevel;
    }

    public void setCharacterParagonLevel(int characterParagonLevel) {
        this.characterParagonLevel = characterParagonLevel;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    public int getCharacterEliteKills() {
        return characterEliteKills;
    }

    public void setCharacterEliteKills(int characterEliteKills) {
        this.characterEliteKills = characterEliteKills;
    }
}
