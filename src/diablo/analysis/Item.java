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
    private HashMap<String,Double> itemStats;
    private String heroClass = "unknown";
    private int accountEliteKills = -1;
    private int characterEliteKills = -1;
    private int accountParagonlevel = -1;
    private int characterParagonLevel = -1;
    private long dataTime;
    
    
    public Item(DBObject itemObject, Profile profile, Hero hero){
        accountEliteKills = profile.getEliteKills();

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
