/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets only the latest information available for a profile/hero/item
 *
 * Emits the data to the listeners as data comes in
 *
 * Emits happen in order: one Profile - profile's heroes - profile's heroes'
 * items
 *
 * @author Josh
 */
public class LatestDataGatherer {

    private List<DataListener> listeners = new ArrayList<DataListener>();
    private boolean getHeroData = false; //if true, retrieves information in hero files
    private boolean getItemData = false; //if true retrieves information in item files //must get hero data for this
    private DiabloFileReader fileReader;

    public LatestDataGatherer(List<DataListener> dataListeners, boolean getHeroData, boolean getItemData) {
        fileReader = new DiabloFileReader();
        this.listeners.addAll(dataListeners);
        this.getHeroData = getHeroData;
        this.getItemData = getItemData;
    }

    /**
     * Returns full file names of latest profile files
     *
     * @return
     */
    private List<File> getLatestProfileFiles() {
        long start = System.currentTimeMillis();
        File profileDir = new File(DiabloFileReader.PROFILE_DIR);
        if (!profileDir.exists()) {
            System.out.println("COULD NOT FIND PROFILE DIR. ABORTING");
            return null;
        }
        String[] fileNameArray = profileDir.list();
        Map<String, Long> profileToTimeMap = new HashMap<String, Long>();
        for (int i = 0; i < fileNameArray.length; i++) {
            String fileName = fileNameArray[i].substring(0, fileNameArray[i].indexOf(".txt"));
            String[] fileNameSplit = fileName.split("_");
            long time = Long.parseLong(fileNameSplit[0]);
            String profileName = fileNameSplit[1];
            long latest = -1;
            if (profileToTimeMap.get(profileName) != null) {
                latest = profileToTimeMap.get(profileName);
            }

            if (time > latest) {
                profileToTimeMap.put(profileName, time);
            }

        }

        long end = System.currentTimeMillis();
        long methodTime = end - start;
        System.out.println("It took " + methodTime + " ms to get latest profile names. Found: " + profileToTimeMap.size() + " profiles");
        List<File> profileFiles = new ArrayList<File>();
        for (String profile : profileToTimeMap.keySet()) {
            long time = profileToTimeMap.get(profile);
            String fullFileName = DiabloFileReader.PROFILE_DIR + "/" + time + "_" + profile + ".txt";
            profileFiles.add(new File(fullFileName));
        }

        return profileFiles;
    }

    public void gatherLatestData() {
        List<File> profileFiles = getLatestProfileFiles();
        for (int i = 0; i < profileFiles.size(); i++) {
            Profile profile = fileReader.getProfileFromFile(profileFiles.get(i));
            List<File> heroFiles = gatherLatestHeroFiles(profile);
            for(int j = 0; j < heroFiles.size(); j++){
                Hero hero = fileReader.getHeroFromFile(heroFiles.get(j), profile);
                for(int k = 0; k < Constants.SLOTS.size(); k++){
                    
                }
            }

        }
    }
    
    public Item getLatestItemForSlot(String slot, Profile profile, Hero hero){
        
        String itemEnding = slot+"_"+hero.get+"_"+profileName;
        File itemDir = new File(DiabloFileReader.ITEMS_DIR);
        String[] itemFiles = itemDir.list();
        long latestTime = -1;
        String latestFileName = null;
        for(int i = 0; i < itemFiles.length; i++){
            if(itemFiles[i].indexOf(itemEnding) >= 0){
                String[] testFileSplit = itemFiles[i].split("_");
                Long testTime = Long.parseLong(testFileSplit[0]);
                if(testTime > latestTime){
                    latestTime = testTime;
                    latestFileName = itemFiles[i];
                }
            }
        }
        
        Item
    }

    public List<File> gatherLatestHeroFiles(Profile profile) {
        ArrayList<File> heroFiles = new ArrayList<File>();

        List<HeroMetadata> heroMetaDataList = profile.getHeroMetadata();
        File heroDir = new File(DiabloFileReader.HERO_DIR);
        String[] allHeroFiles = heroDir.list();
        //System.out.println("there are " + allHeroFiles.length + " hero files");
        Map<String, String> heroToLatestMap = new HashMap<String, String>();
        for (int i = 0; i < heroMetaDataList.size(); i++) {
            String heroId = heroMetaDataList.get(i).getHeroId();

            String heroFileEnding = heroMetaDataList.get(i).getHeroId() + "_" + profile.getProfileName().replace("#", "-");
           // System.out.println("Hero file ending: " + heroFileEnding);
            for (int j = 0; j < allHeroFiles.length; j++) {
                if (allHeroFiles[j].indexOf(heroFileEnding) >= 0) {
                    //System.out.println("Found matching hero file: " + allHeroFiles[j]);
                    if (heroToLatestMap.get(heroId) == null) {
                        heroToLatestMap.put(heroId, allHeroFiles[j]);
                    } else {
                        String latestFile = heroToLatestMap.get(heroId);
                        String[] latestFileSplit = latestFile.split("_");
                        Long latestTime = Long.parseLong(latestFileSplit[0]);
                        String testFile = allHeroFiles[j];
                        String[] testFileSplit = testFile.split("_");
                        Long testTime = Long.parseLong(testFileSplit[0]);
                        if (testTime > latestTime) {
                            heroToLatestMap.put(heroId, testFile);
                        }
                    }
                }
            }
        }
        for (String hero : heroToLatestMap.keySet()) {
            heroFiles.add(new File(DiabloFileReader.HERO_DIR+"/"+heroToLatestMap.get(hero)));
//            System.out.println("Hero: " + hero + " latest file: " + heroToLatestMap.get(hero));
        }

        return heroFiles;
    }

    public static void main(String[] args) {
        LatestDataGatherer ldg = new LatestDataGatherer(new ArrayList<DataListener>(), true, true);
        ldg.gatherLatestData();
    }
}
