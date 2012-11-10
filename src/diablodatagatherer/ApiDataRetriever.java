/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablodatagatherer;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author jwalton
 */
public class ApiDataRetriever {

    public static final String API_ROOT = "http://us.battle.net/api/d3/";
    public static final String PROFILE_URL = "profile/";
    public static final String ROOT_DIR = "/Users/jwalton/diabloRawFiles";
    private long time = System.currentTimeMillis();
    private List<String> itemSlots = new ArrayList<String>();

    public ApiDataRetriever() {

        itemSlots.add("head");
        itemSlots.add("torso");
        itemSlots.add("feet");
        itemSlots.add("hands");
        itemSlots.add("shoulders");
        itemSlots.add("legs");
        itemSlots.add("bracers");
        itemSlots.add("mainHand");
        itemSlots.add("offHand");
        itemSlots.add("waist");
        itemSlots.add("rightFinger");
        itemSlots.add("leftFinger");
        itemSlots.add("neck");

    }

    public List<String> findProfileNames() {
        List<String> profileNames = new ArrayList<String>();
        try {
            Mongo m = new Mongo("localhost", 27017);
            DB db = m.getDB("Downloads");
            DBCollection profiles = db.getCollection("profiles");
            DBCursor cursor = profiles.find();
            while (cursor.hasNext()) {
                DBObject profile = cursor.next();
                profileNames.add((String) profile.get("profile"));
            }
        } catch (Exception e) {
            System.out.println("Error getting profile names");
            e.printStackTrace();
        }
        return profileNames;
    }

    public List<String> sortProfiles(List<String> profiles) {
        System.out.println("Input profile size: " + profiles.size());
        String profileDirName = ROOT_DIR + "/profiles";
        File profileDir = new File(profileDirName);
        String[] profileFiles = profileDir.list();
        ArrayList<String> profileFileNames = new ArrayList<String>();
        ArrayList<String> profileNames = new ArrayList<String>();
        ArrayList<String> profilesToUpdate = new ArrayList<String>();
        System.out.println("# Files: "+profileFiles.length);
        //sort files by time
        for (int i = 0; i < profileFiles.length; i++) {
            //System.out.println(i+": "+profileFiles[i]);
            profileFileNames.add(profileFiles[i]);
        }
        //put them in order from most recent to oldest
        Collections.sort(profileFileNames);
        Collections.reverse(profileFileNames);

        for (int i = 0; i < profileFileNames.size(); i++) {
            //System.out.println("profile file name" + profileFiles[i]);
            String[] profileFileSplit = profileFileNames.get(i).split("_");
            if (profileFileSplit.length == 2) {
                //System.out.println("profileFileSplit[1] = "+profileFileSplit[1]);
                String profileName = profileFileSplit[1].substring(0, profileFileSplit[1].indexOf("."));
                //System.out.println("adding profile name: " + profileName);
                profileNames.add(profileName);
            } else {
                System.out.println("Profile name not as expected: "+Arrays.toString(profileFileSplit));
            }
        }

        //get unique profile names
        for (int i = 0; i < profileNames.size(); i++) {
            //System.out.println("sorted profile: " + profileNames.get(i));
            if (!profilesToUpdate.contains(profileNames.get(i))) {
                //System.out.println("Adding profile to final list: "+profileNames.get(i));
                profilesToUpdate.add(profileNames.get(i));
            }
        }
        //add any profiles that have no data
        for (int i = 0; i < profiles.size(); i++) {
            if (!profilesToUpdate.contains(profiles.get(i))) {
               // System.out.println("Profile has no data: "+profiles.get(i));
                profilesToUpdate.add(profiles.get(i));
            }
        }
        //reverse order should have profiles with no data, then oldest to newest profile data
        Collections.reverse(profilesToUpdate);

//        for (int i = 0; i < profilesToUpdate.size(); i++) {
//            System.out.println("final sorted profile: " + profilesToUpdate.get(i));
//        }
        
        System.out.println("Final sorted profiles to update size: "+profilesToUpdate.size());

        return profilesToUpdate;
    }
    
    public List<String> getSortedProfiles(){
        List<String> profiles = findProfileNames();
        return sortProfiles(profiles);
    }

    public void retrieveProfileData(String profile) {
        System.out.println("Processing profile: " + profile);
        ClientRequest request = new ClientRequest(API_ROOT + PROFILE_URL + "/" + profile + "/");
        ClientResponse<String> response = null;
        try {
            response = request.get(String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String profileString = response.getEntity();
        try {
            String dirName = ROOT_DIR + "/" + "profiles";
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String profileFileName = dirName + "/" + time + "_" + profile + ".txt";
            BufferedWriter profileWriter = new BufferedWriter(new FileWriter(profileFileName));
            profileWriter.write(profileString);
            profileWriter.flush();
            profileWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing profile file");
            e.printStackTrace();
        }
        DBObject profileObject = (DBObject) JSON.parse(profileString);
        List<Integer> characterIds = getCharacterIds(profileObject);
        List<String> itemUrls = new ArrayList<String>();
        for (Integer characterId : characterIds) {
            String character = getCharacter(profile, characterId);
            String characterFileDir = ROOT_DIR + "/heroes/";
            File characterDir = new File(characterFileDir);
            if (!characterDir.exists()) {
                characterDir.mkdir();
            }
            String characterFileName = characterFileDir + time + "_" + characterId + "_" + profile + ".txt";


            try {
                BufferedWriter characterWriter = new BufferedWriter(new FileWriter(characterFileName));
                characterWriter.write(character);
                characterWriter.flush();
                characterWriter.close();
            } catch (Exception e) {
                System.out.println("Error writing character data");
                e.printStackTrace();
            }
            try {
                DBObject characterObject = (DBObject) JSON.parse(character);
                DBObject itemsObject = (DBObject) characterObject.get("items");
                if (itemsObject == null) {
                    continue;
                }
                for (String itemSlot : itemSlots) {
                    DBObject itemObject = (DBObject) itemsObject.get(itemSlot);
                    if (itemObject == null) {

                        continue;
                    }
                    String itemURL = (String) itemObject.get("tooltipParams");
                    //System.out.println("itemURl = "+itemURL);
                    String itemString = getItem(itemURL);
                    String itemDirName = ROOT_DIR + "/items/";
                    File itemDir = new File(itemDirName);
                    if (!itemDir.exists()) {
                        itemDir.mkdir();
                    }
                    String itemFileName = itemDirName + time + "_" + itemSlot + "_" + characterId + "_" + profile + ".txt";
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(itemFileName));
                        bw.write(itemString);
                        bw.flush();
                        bw.close();
                    } catch (Exception e) {
                        System.out.println("Error writing item file");
                        e.printStackTrace();
                    }

                }
                System.out.println("Finished processing items for character " + characterId);
            } catch (Exception e) {
                System.out.println("Error parsing character data");
                e.printStackTrace();
            }
        }
        System.out.println("Finished processing characters for profile " + profile);
    }

    private String getItem(String itemURL) {
        DBObject itemObject = null;

        String fullUrl = API_ROOT + "data/" + itemURL;
        ClientRequest request = new ClientRequest(fullUrl);
        String itemString = null;
        try {
            ClientResponse<String> response = request.get(String.class);
            itemString = response.getEntity();
            itemObject = (DBObject) JSON.parse(itemString);

        } catch (Exception e) {
            System.out.println("Error getting character object for " + itemURL + "." + e.getMessage());
        }

        return itemString;
    }

    private String getCharacter(String profileName, Integer characterId) {
        DBObject characterObject = null;
        String heroUrl = API_ROOT + PROFILE_URL + profileName + "/hero/" + characterId;
        System.out.println("character url: " + heroUrl);
        ClientRequest request = new ClientRequest(heroUrl);
        String heroString = null;
        try {
            ClientResponse<String> response = request.get(String.class);
            heroString = response.getEntity();
            characterObject = (DBObject) JSON.parse(heroString);
            System.out.println("Character object: " + characterObject.toString());

        } catch (Exception e) {
            System.out.println("Error getting character object for " + profileName + " character id= " + characterId + "." + e.getMessage());
        }

        return heroString;
    }

    private List<Integer> getCharacterIds(DBObject profileObject) {
        List<Integer> characterIds = new ArrayList<Integer>();
        Object heroesObject = profileObject.get("heroes");
        if (heroesObject instanceof List) {
            List heroList = (List) heroesObject;
            for (int i = 0; i < heroList.size(); i++) {
                Object heroObject = heroList.get(i);
                if (heroObject instanceof DBObject) {
                    DBObject heroDBObject = (DBObject) heroObject;
                    int id = (Integer) heroDBObject.get("id");
                    //log.trace("Found hero id of: " + id);
                    characterIds.add(id);
                }
            }
        } else if (heroesObject instanceof DBObject) {
            DBObject heroDBObject = (DBObject) heroesObject;
            int id = (Integer) heroDBObject.get("id");
            //log.trace("Found hero id of: " + id);
            characterIds.add(id);
        } else {
            // log.error("heroes object is of unexpected type: " + heroesObject.getClass().getName());
        }
        return characterIds;
    }

    public static void main(String[] args) {
        ApiDataRetriever dataRetriever = new ApiDataRetriever();

        List<String> sortedProfiles = dataRetriever.getSortedProfiles();
        
        for(String profile : sortedProfiles){
            dataRetriever.retrieveProfileData(profile);
        }
    }
}
