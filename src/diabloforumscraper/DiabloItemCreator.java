/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * This class will create DiabloItemRecords given a profile
 * @author jwalton
 */
public class DiabloItemCreator {

     private static Logger log = Logger.getLogger(DiabloItemCreator.class);
     public static final String API_ROOT = "http://us.battle.net/api/d3/";
     public static final String PROFILE_URL = "profile/";
     private static final int NUMBER_ACTS = 4;
     private ArrayList<String> difficulties = new ArrayList<String>();
     private ArrayList<String> itemSlots = new ArrayList<String>();

     public DiabloItemCreator() {
          init();
     }

     private void init() {
          difficulties.add("normal");
          difficulties.add("nightmare");
          difficulties.add("hell");
          difficulties.add("inferno");

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
          itemSlots.add("neck ");

     }

     public List<DiabloItemRecord> createDiabloItems(String profile) {
          ArrayList<DiabloItemRecord> diabloItems = new ArrayList<DiabloItemRecord>();

          DBObject profileObject = getProfileObject(profile);
          if (profileObject != null) {
               int accountProgress = calculateProgression(profileObject);
               int accountEliteKills = getEliteKills(profileObject);
               int maxCharacterLevel = Integer.MIN_VALUE;
               int maxParagonLevel = Integer.MIN_VALUE;
               log.debug(profile + " progress = " + accountProgress + " elite kills: " + accountEliteKills);
               List<Integer> characterIds = getCharacterIds(profileObject);
               List<DiabloItemRecord> accountItems = new ArrayList<DiabloItemRecord>();
               for (Integer characterId : characterIds) {
                    DBObject characterObject = getCharacter(profile, characterId);
                    if (characterObject == null) {
                         continue;
                    }
                    String characterClass = getCharacterClass(characterObject);
                    boolean hardcore = isHardcore(characterObject);
                    int characterLevel = getCharacterlevel(characterObject);
                    int paragonLevel = getCharacterlevel(characterObject);
                    if (characterLevel > maxCharacterLevel) {
                         maxCharacterLevel = characterLevel;
                    }
                    if (paragonLevel > maxParagonLevel) {
                         maxParagonLevel = paragonLevel;
                    }
                    int characterEliteKills = getEliteKills(characterObject);
                    int characterProgress = calculateProgression(characterObject);
                    List<Skill> skills = getSkills(characterObject);
                    DBObject itemsObject = (DBObject) characterObject.get("items");
                    for (String itemSlot : itemSlots) {
                         DBObject itemObject = (DBObject) itemsObject.get(itemSlot);
                         String itemURL = (String) itemObject.get("tooltipsParam");
                         DBObject itemInfoObject = getItem(itemURL);
                         if (itemInfoObject == null) {
                              log.warn("Could not get item: " + itemURL);
                         }

                         HashMap<String, Double> itemStats = parseItemObject(itemInfoObject);

                         DiabloItemRecord itemRecord = new DiabloItemRecord();
                         itemRecord.setAccountProgress(accountProgress);
                         itemRecord.setCharacterClass(characterClass);
                         itemRecord.setCharacterEliteKills(characterEliteKills);
                         itemRecord.setCharacterLevel(characterLevel);
                         itemRecord.setCharacterProgress(characterProgress);
                         itemRecord.setHardcore(hardcore);
                         itemRecord.setItemSlot(itemSlot);
                         itemRecord.setItemStats(itemStats);
                         itemRecord.setParagonLevel(paragonLevel);
                         itemRecord.setProfileName(profile);
                         itemRecord.setSkills(skills);
                         itemRecord.setTotalAccountEliteKills(accountEliteKills);
                         accountItems.add(itemRecord);

                    } //end for item loop

               } //end for character loop

               //set account wide max stats that have to be computed
               for (DiabloItemRecord itemRecord : accountItems) {
                    itemRecord.setAccountMaxLevel(maxCharacterLevel);
                    itemRecord.setAccountMaxParagonLevel(maxParagonLevel);
               }
          }

          return diabloItems;
     }

     private HashMap<String, Double> parseItemObject(DBObject itemObject) {
          HashMap<String, Double> itemStats = new HashMap<String, Double>();

          DBObject dpsObject = (DBObject) itemObject.get("dps");
          if (dpsObject != null) {
               itemStats.put("dps", Double.parseDouble((String) dpsObject.get("max")));
          }

          DBObject attributesRawObject = (DBObject) itemObject.get("attributesRaw");
          for (String attribute : attributesRawObject.keySet()) {
               DBObject attributeObject = (DBObject) attributesRawObject.get("attribute");
               String minString = (String) attributeObject.get("min");
               String maxString = (String) attributeObject.get("max");

               Double minVal = Double.parseDouble(minString.trim());
               Double maxVal = Double.parseDouble(maxString.trim());
               Double average = (minVal + maxVal) / 2.0;

               itemStats.put(attribute, average);

          }

          return itemStats;
     }

     private DBObject getItem(String itemURL) {
          DBObject itemObject = null;

          String fullUrl = API_ROOT + "data/" + itemURL;
          ClientRequest request = new ClientRequest(fullUrl);
          try {
               ClientResponse<String> response = request.get(String.class);
               String itemString = response.getEntity();
               itemObject = (DBObject) JSON.parse(itemString);

          } catch (Exception e) {
               log.error("Error getting character object for " + itemURL, e);
          }

          return itemObject;
     }

     private List<Skill> getSkills(DBObject character) {
          List<Skill> skills = new ArrayList<Skill>();

          DBObject skillsObject = (DBObject) character.get("skills");
          List activeList = (List) skillsObject.get("active");
          for (int i = 0; i < activeList.size(); i++) {
               DBObject activeSkillObject = (DBObject) activeList.get(i);
               DBObject activeSkill = (DBObject) activeSkillObject.get("skill");
               String skillName = (String) activeSkill.get("name");
               DBObject rune = (DBObject) activeSkillObject.get("rune");
               String runeName = (String) rune.get("name");
               Skill skill = new Skill(skillName, runeName);
               skills.add(skill);
          }
          return skills;
     }

     private Boolean isHardcore(DBObject character) {
          return (Boolean) character.get("hardcore");
     }

     private String getCharacterClass(DBObject character) {
          return (String) character.get("class");
     }

     private Integer getCharacterlevel(DBObject character) {
          Integer characterLevel = (Integer) character.get("level");
          return characterLevel;
     }

     private Integer getParagonLevel(DBObject character) {
          Integer paragonLevel = (Integer) character.get("pargonLevel");
          return paragonLevel;
     }

     private DBObject getCharacter(String profileName, Integer characterId) {
          DBObject characterObject = null;
          String heroUrl = ProfileGrabber.API_ROOT + ProfileGrabber.PROFILE_URL + profileName + "/hero/" + characterId;
          ClientRequest request = new ClientRequest(heroUrl);

          try {
               ClientResponse<String> response = request.get(String.class);
               String heroString = response.getEntity();
               characterObject = (DBObject) JSON.parse(heroString);

          } catch (Exception e) {
               log.error("Error getting character object for " + profileName + " character id= " + characterId, e);
          }

          return characterObject;
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
                         log.trace("Found hero id of: " + id);
                         characterIds.add(id);
                    }
               }
          } else if (heroesObject instanceof DBObject) {
               DBObject heroDBObject = (DBObject) heroesObject;
               int id = (Integer) heroDBObject.get("id");
               log.trace("Found hero id of: " + id);
               characterIds.add(id);
          } else {
               log.error("heroes object is of unexpected type: " + heroesObject.getClass().getName());
          }
          return characterIds;
     }

     private int getEliteKills(DBObject profileObject) {
          DBObject killObject = (DBObject) profileObject.get("kills");
          int eliteKills = (Integer) killObject.get("elites");
          return eliteKills;
     }

     private int calculateProgression(DBObject profileObject) {
          int progress = 0;
          DBObject progressionObject = (DBObject) profileObject.get("progression");
          for (int i = 0; i < difficulties.size(); i++) {
               DBObject difficultyObject = (DBObject) progressionObject.get(difficulties.get(i));
               int difficultyProgress = 0;
               for (int j = 1; j <= NUMBER_ACTS; j++) {
                    String actKey = "act" + j;
                    DBObject actObject = (DBObject) difficultyObject.get(actKey);
                    String completionStatus = actObject.get("completed").toString();
                    if (completionStatus.equalsIgnoreCase("true")) {
                         difficultyProgress++;
                    } else {
                         break;
                    }
               }

               progress += difficultyProgress;
               if (difficultyProgress != NUMBER_ACTS) {
                    break;
               }
          }

          return progress;
     }

     private DBObject getProfileObject(String profile) {
          log.trace("Retrieving profile for " + profile);
          DBObject profileObject = null;
          ClientRequest request = new ClientRequest(API_ROOT + PROFILE_URL + "/" + profile + "/");
          try {
               ClientResponse<String> response = request.get(String.class);
               String profileString = response.getEntity();
               log.trace("Success retrieving profile for " + profile);
               profileObject = (DBObject) JSON.parse(profileString);
               log.trace("Success parsing profile for " + profile);
               profileObject.put("profile-name", profile);

          } catch (Exception e) {
               log.error("Error retrieving profile. ", e);
          }
          return profileObject;
     }
}
