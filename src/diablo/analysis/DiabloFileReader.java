/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josh
 */
public class DiabloFileReader {

    public static String ROOT_DIR = "C:/Users/Josh/Dropbox/Public/diabloRawFiles";
    public static String PROFILE_DIR = ROOT_DIR + "/profiles";
    public static String HERO_DIR = ROOT_DIR + "/heroes";
    public static String ITEMS_DIR = ROOT_DIR + "/items";

    public List<Profile> getProfileObjects() {
        List<Profile> profileObjects = new ArrayList<Profile>();

        File profileDir = new File(PROFILE_DIR);
        File[] profileFiles = profileDir.listFiles();

        for (int i = 0; i < profileFiles.length; i++) {
            try {
                String fileName = profileFiles[i].getName();
                long time = Long.parseLong(fileName.split("_")[0]);
                BufferedReader br = new BufferedReader(new FileReader(profileFiles[i]));
                String profileData = "";
                String line = null;
                while ((line = br.readLine()) != null) {
                    profileData += line;
                }
                br.close();

                DBObject profileObject = (DBObject) JSON.parse(profileData);
                profileObject.put("fileTime", time);
                profileObject.put("fileName", fileName);
                profileObjects.add(new Profile(profileObject));

                //System.out.println("Profile object: "+profileObject.toString());

            } catch (Exception e) {
                System.out.println("Error reading or parsing file");
                e.printStackTrace();
            }



        }

        return profileObjects;
    }

    public Hero getHeroFromFile(String file, Profile profile) {
        return getHeroFromFile(new File(file), profile);
    }

    public Hero getHeroFromFile(File f, Profile profile) {
        String heroData = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = br.readLine()) != null) {
                heroData += line;
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error reading hero data: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        DBObject heroObject = null;
        try {
            heroObject = (DBObject) JSON.parse(heroData);
        } catch (Exception e) {
            System.out.println("Error parsing hero data: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        Hero h = null;
        if (heroObject != null) {
            h = new Hero(heroObject, profile.getProfileName(), "" + profile.getDataTime());

        }
        
        return h;
    }

    public Profile getProfileFromFile(String file) {
        return getProfileFromFile(new String(file));
    }

    public Profile getProfileFromFile(File f) {
        String profileData = "";
        String fileName = null;
        long time = -1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            fileName = f.getName();
            time = Long.parseLong(fileName.split("_")[0]);
            String line = null;
            while ((line = br.readLine()) != null) {
                profileData += line;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error retrieving profile from file: " + f.getName() + ". " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        DBObject profileObject = (DBObject) JSON.parse(profileData);
        profileObject.put("fileTime", time);
        profileObject.put("fileName", fileName);

        Profile profile = new Profile(profileObject);

        return profile;
    }

    /**
     * This method will return hero data collected at the same time as the
     * profile TODO add method to get latest hero data per profile
     *
     * @param profile
     * @return
     */
    public List<Hero> getHeroes(Profile profile) {
        ArrayList<Hero> heroes = new ArrayList<Hero>();
        for (HeroMetadata heroMetadata : profile.getHeroMetadata()) {
            String heroFileName = profile.getDataTime() + "_" + heroMetadata.getName() + "_" + profile.getProfileName();
            String fullHeroFilename = DiabloFileReader.ROOT_DIR + DiabloFileReader.HERO_DIR + "/" + heroFileName;
            File heroFile = new File(fullHeroFilename);
            String heroData = "";
            try {
                BufferedReader br = new BufferedReader(new FileReader(heroFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    heroData += line;
                }

                br.close();

            } catch (Exception e) {
                System.out.println("Error reading hero data: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
            DBObject heroObject = null;
            try {
                heroObject = (DBObject) JSON.parse(heroData);
            } catch (Exception e) {
                System.out.println("Error parsing hero data: " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            if (heroObject != null) {
                Hero h = new Hero(heroObject, profile.getProfileName(), "" + profile.getDataTime());
                heroes.add(h);
            }
        }

        return heroes;
    }

    public static void main(String[] args) {
        DiabloFileReader dfr = new DiabloFileReader();
        dfr.getProfileObjects();
    }
}
