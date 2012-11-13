/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import com.mongodb.DBObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Josh
 */
public class Profile {

    private String fileName = "unknown";
    private String profileName = "unknown";
    private int tagNumber = -1;
    private long dataTime = 0; //time profile data was collected
    private long lastUpdatedTime = -1;
    private Map<String, Double> classPlayedTimes = new HashMap<String, Double>();
    private List<HeroMetadata> heroMetadata = new ArrayList<HeroMetadata>();
    private int eliteKills = -1;
    private int kills = -1;
    private int hardcoreKills = -1;
    private int maxParagonLevel = -1;

    public Profile(DBObject profileObject) {
        fileName = (String) profileObject.get("fileName");
        profileName = (String) profileObject.get("battleTag");
        if(profileName == null){
            System.out.println("Profile has null battletag: "+profileObject.toString());
            System.out.println("Invalid profile file, deleting it.");
            File f = new File(DiabloFileReader.PROFILE_DIR+"/"+fileName);
            f.delete();
            return;
        }
        profileName = profileName.replace("#", "-");
        tagNumber = Integer.parseInt(profileName.split("-")[1]);
        dataTime = Long.parseLong(profileObject.get("fileTime").toString());
        lastUpdatedTime = Long.parseLong(profileObject.get("lastUpdated").toString());
        List heroList = (List) profileObject.get("heroes");
        for (int i = 0; i < heroList.size(); i++) {
            try {
                DBObject heroObject = (DBObject) heroList.get(i);
                HeroMetadata heroData = new HeroMetadata(heroObject);
                heroMetadata.add(heroData);
            } catch (Exception e) {
                System.out.println("PROBABLY Unexpected object type in hero list! Type is " + heroList.get(i).getClass().getName());
                e.printStackTrace();
            }
        }

        DBObject classPlayedObject = (DBObject) profileObject.get("timePlayed");
        classPlayedTimes.put(Constants.BARB, Double.parseDouble(classPlayedObject.get(Constants.BARB).toString()));
        classPlayedTimes.put(Constants.DEMON_HUNTER, Double.parseDouble(classPlayedObject.get(Constants.DEMON_HUNTER).toString()));
        classPlayedTimes.put(Constants.MONK, Double.parseDouble(classPlayedObject.get(Constants.MONK).toString()));
        classPlayedTimes.put(Constants.WITCH_DOCTOR, Double.parseDouble(classPlayedObject.get(Constants.WITCH_DOCTOR).toString()));
        classPlayedTimes.put(Constants.WIZARD, Double.parseDouble(classPlayedObject.get(Constants.WIZARD).toString()));

        DBObject killObject = (DBObject) profileObject.get("kills");
        if (killObject.get("elites") != null) {
            eliteKills = (Integer) killObject.get("elites");
        }
        if (killObject.get("monsters") != null) {
            kills = (Integer) killObject.get("monsters");
        }
        if (killObject.get("hardcoreMonsters") != null) {
            hardcoreKills = (Integer) killObject.get("hardcoreMonsters");
        }

    }

    public String getFileName() {
        return fileName;
    }

    public String getProfileName() {
        return profileName;
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public long getDataTime() {
        return dataTime;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public Map<String, Double> getClassPlayedTimes() {
        return classPlayedTimes;
    }

    public List<HeroMetadata> getHeroMetadata() {
        return heroMetadata;
    }

    public int getEliteKills() {
        return eliteKills;
    }

    public int getKills() {
        return kills;
    }

    public int getHardcoreKills() {
        return hardcoreKills;
    }

    public int getMaxParagonLevel() {
        return maxParagonLevel;
    }

    public void setMaxParagonLevel(int maxParagonLevel) {
        this.maxParagonLevel = maxParagonLevel;
    }
    
    
}
