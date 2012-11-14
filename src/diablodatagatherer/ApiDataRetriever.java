/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablodatagatherer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.ConnectException;
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
    public static final String ROOT_DIR = "/C:/Users/Josh/diabloRawData";
    private long time = System.currentTimeMillis();
    private List<String> itemSlots = new ArrayList<String>();
    private long wait = 1000;
    Mongo m;
    DB db;
    DBCollection notFoundCollection = null;

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
            m = new Mongo("ds037907.mongolab.com", 37907);
            db = m.getDB("diablo");
            db.authenticate("diabloUser", "diabloUser".toCharArray());
            DBCollection profiles = db.getCollection("profiles");
            notFoundCollection = db.getCollection("notfound");
            DBCursor cursor = profiles.find();
            cursor = cursor.skip(1000);
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
        System.out.println("# Files: " + profileFiles.length);
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
                System.out.println("Profile name not as expected: " + Arrays.toString(profileFileSplit));
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

        System.out.println("Final sorted profiles to update size: " + profilesToUpdate.size());

        return profilesToUpdate;
    }

    public List<String> getSortedProfiles() {
        List<String> profiles = findProfileNames();
        return sortProfiles(profiles);
    }

    public void retrieveProfileData(String profile) {
        System.out.println("Processing profile: " + profile);
        ClientRequest request = new ClientRequest(API_ROOT + PROFILE_URL + profile + "/");
        ClientResponse<String> response = null;
        try {
            response = request.get(String.class);
        } catch (ConnectException ce) {
            wait = wait * 2;
            System.out.println("Connect exception");
            try {
                System.out.println("Sleeping " + wait + " ms");
                Thread.sleep(wait);
                System.out.println("Done sleeping");
                return;
            } catch (Exception ex) {
            }
        } catch (Exception e) {
            System.out.println("Could not get profile, waiting for : " + wait);
            wait = wait * 2;

            e.printStackTrace();
            try {
                System.out.println("Sleeping " + wait + " ms");
                Thread.sleep(wait);
                return;
            } catch (Exception ex) {
            }
        }
        String profileString = response.getEntity();
        DBObject profileObject = null;
        try {
            profileObject = (DBObject) JSON.parse(profileString);
            if ((String) profileObject.get("code") != null) {
                System.out.println("API error retrieving profile for " + profile + ": " + profileObject.toString() + ". Profile url: " + request.getUri());
                if (profileObject.get("code").toString().equalsIgnoreCase("OOPS")) {
                   
                } else if(profileObject.get("code").toString().equalsIgnoreCase("NOTFOUND")){
                    
                    DBObject notFoundObject = new BasicDBObject();
                    notFoundObject.put("profile", profile);
                    notFoundCollection.insert(notFoundObject);
                }else {
                    wait+= 2000;
                    try {
                        System.out.println("Sleeping " + wait + " ms");
                        Thread.sleep(wait);

                    } catch (Exception ex) {
                    }
                   
                }
                return;
            }
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
            System.out.println("Error writing profile file: " + profileString);
            e.printStackTrace();
        }

        List<Integer> characterIds = getCharacterIds(profileObject);
        if (characterIds == null || characterIds.size() == 0) {
            try {
                wait = wait * 2;
                System.out.println("Waiting for " + wait + " seconds because could not get character ids");
                System.out.println("Bad profile string: "+profileObject.toString());
                Thread.sleep(wait);
                return;
            } catch (Exception e) {
            }
        } else {
            System.out.println("call worked, resetting wait");
            wait = 1000;
        }

        for (Integer characterId : characterIds) {
            String character = getCharacter(profile, characterId);
            String characterFileDir = ROOT_DIR + "/heroes/";
            File characterDir = new File(characterFileDir);
            if (!characterDir.exists()) {
                characterDir.mkdir();
            }
            String characterFileName = characterFileDir + time + "_" + characterId + "_" + profile + ".txt";

            DBObject characterObject = null;
            try {
                characterObject = (DBObject) JSON.parse(character);
            } catch (Exception e) {
                System.out.println("Error parsing character for profile = " + profile + " id = " + characterId + ". " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            if (characterObject == null || !(isValidAPIData(characterObject))) {
                System.out.println("Invalid character object, trying next character");
                continue;
            }

            try {
                BufferedWriter characterWriter = new BufferedWriter(new FileWriter(characterFileName));
                characterWriter.write(character);
                characterWriter.flush();
                characterWriter.close();
            } catch (Exception e) {
                System.out.println("Error writing character data: " + character);
                e.printStackTrace();
            }
            try {
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

                    DBObject itemApiObject = null;
                    try {
                        itemApiObject = (DBObject) JSON.parse(itemString);
                    } catch (Exception e) {
                        System.out.println("Error parsing item object for url: " + itemURL + ". " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    if (itemApiObject == null || !(isValidAPIData(itemApiObject))) {
                        System.out.println("Error in API retrieving item.");
                        continue;
                    }

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
                        System.out.println("Error writing item file: " + itemString);
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

    public static boolean isValidAPIData(DBObject object) {
        if (object != null && object.get("code") != null) {
            return false;
        } else {
            return true;
        }
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
            //System.out.println("Character object: " + characterObject.toString());

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
        int count = 0;
        for (String profile : sortedProfiles) {
            dataRetriever.retrieveProfileData(profile);
            count++;
            System.out.println("Processed " + count + "/" + sortedProfiles.size() + " profiles");
        }
    }
}
