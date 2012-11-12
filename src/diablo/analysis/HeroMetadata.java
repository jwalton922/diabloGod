/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import com.mongodb.DBObject;

/**
 *
 * @author Josh
 */
public class HeroMetadata {
    
    String name = "unknown";
    String heroId = "unknown";
    int level = -1;
    int paragonLevel = -1;
    boolean hardcore = false;
    int gender = -1;
    boolean dead = false;
    String heroClass = "unknown";
    long lastUpdated = -1;
    
    public HeroMetadata(DBObject heroMetadataObject){
        name = (String) heroMetadataObject.get("name");
        heroId = heroMetadataObject.get("id").toString();
        level = (Integer) heroMetadataObject.get("level");
        hardcore = (Boolean) heroMetadataObject.get("hardcore");
        paragonLevel = (Integer) heroMetadataObject.get("paragonLevel");
        gender = (Integer) heroMetadataObject.get("gender");
        dead = (Boolean) heroMetadataObject.get("dead");
        heroClass =(String) heroMetadataObject.get("class");
        lastUpdated = Long.parseLong(heroMetadataObject.get("last-updated").toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public int getLevel() {
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    
}

