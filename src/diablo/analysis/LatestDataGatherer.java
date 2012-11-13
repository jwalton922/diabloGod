/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private int minCharacterLevel; //filter out items and heroes that are below this level
    private int maxProfilesToCheck = Integer.MAX_VALUE;

    public LatestDataGatherer(List<DataListener> dataListeners, boolean getHeroData, boolean getItemData) {
        fileReader = new DiabloFileReader();
        this.listeners.addAll(dataListeners);
        this.getHeroData = getHeroData;
        this.getItemData = getItemData;
    }
    
    public void setMaxProfilesToCheck(int maxValue){
        this.maxProfilesToCheck = maxValue;
    }

    public int getMinCharacterLevel() {
        return minCharacterLevel;
    }

    public void setMinCharacterLevel(int minCharacterLevel) {
        
        this.minCharacterLevel = minCharacterLevel;
        System.out.println("Min level: "+this.minCharacterLevel);
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
        int maxProfileIt = profileFiles.size();
        if(maxProfilesToCheck < profileFiles.size()){
            maxProfileIt = maxProfilesToCheck;
        }
        for (int i = 0; i < maxProfileIt; i++) {
            long start = System.currentTimeMillis();
            Profile profile = fileReader.getProfileFromFile(profileFiles.get(i));
            if(profile == null){
                System.out.println("Invalid profile: "+profileFiles.get(i).getName()+". File should be deleted now");
                continue;
            }
            List<HeroInfoContainer> heroFiles = gatherLatestHeroFiles(profile);
            List<Hero> heroes = new ArrayList<Hero>();
            List<Item> items = new ArrayList<Item>();
            int maxParagonLevel = -1;
            for (int j = 0; j < heroFiles.size(); j++) {
                Hero hero = fileReader.getHeroFromFile(heroFiles.get(j).latestHeroFile, profile);
                if(hero == null){
                    continue;
                }
                
                if(hero.getLevel() < minCharacterLevel){
                    continue;
                }
                
                hero.setHeroTimes(heroFiles.get(j).heroTimes);
                heroes.add(hero);
                if (hero.getParagonLevel() > maxParagonLevel) {
                    maxParagonLevel = hero.getParagonLevel();
                }
                for (int k = 0; k < Constants.SLOTS.size(); k++) {
                    Item item = getLatestItemForSlot(Constants.SLOTS.get(k), profile, hero);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }

            profile.setMaxParagonLevel(maxParagonLevel);

            for (int j = 0; j < heroes.size(); j++) {
                heroes.get(j).setAccountMaxParagonLevel(maxParagonLevel);
            }
            for (int k = 0; k < items.size(); k++) {
                items.get(k).setAccountParagonlevel(maxParagonLevel);
            }
            //emit data
            for (DataListener listener : listeners) {
                listener.processProfile(profile);
                for (Hero hero : heroes) {
                    listener.processHero(hero);
                }

                for (Item item : items) {
                    listener.processItem(item);
                }
            }

            long end = System.currentTimeMillis();
            long processTime = end - start;
            System.out.println("Finished "+i+"/"+profileFiles.size()+". It took " + processTime + " ms to process profile " + profile.getProfileName());
            
        }
    }

    public Item getLatestItemForSlot(String slot, Profile profile, Hero hero) {

        String itemEnding = slot + "_" + hero.getId() + "_" + profile.getProfileName();
        long latestTime = -1;
        String latestFileName = null;

        List<Long> heroTimes = hero.getHeroTimes();
        File latestFile = null;
        for (int i = 0; i < heroTimes.size(); i++) {
            String testFile = DiabloFileReader.ITEMS_DIR + "/" + heroTimes.get(i) + "_" + slot + "_" + hero.getId() + "_" + profile.getProfileName() + ".txt";
            File itemFile = new File(testFile);
            if (itemFile.exists()) {
                latestFile = itemFile;
                break;
            }
        }
        if (latestFile != null) {
            return fileReader.getItemFromFile(latestFile, profile, hero);
        } else {
            return null;
        }
    }

    public List<HeroInfoContainer> gatherLatestHeroFiles(Profile profile) {
        ArrayList<HeroInfoContainer> heroFiles = new ArrayList<HeroInfoContainer>();

        List<HeroMetadata> heroMetaDataList = profile.getHeroMetadata();
        File heroDir = new File(DiabloFileReader.HERO_DIR);
        String[] allHeroFiles = heroDir.list();
        //System.out.println("there are " + allHeroFiles.length + " hero files");
        Map<String, HeroInfoContainer> heroToLatestMap = new HashMap<String, HeroInfoContainer>();
        for (int i = 0; i < heroMetaDataList.size(); i++) {
            String heroId = heroMetaDataList.get(i).getHeroId();
            String heroFileEnding = heroMetaDataList.get(i).getHeroId() + "_" + profile.getProfileName();
            // System.out.println("Hero file ending: " + heroFileEnding);
            List<Long> heroTimes = new ArrayList<Long>();
            long latestTime = -1;
            String latestFileName = "";
            for (int j = 0; j < allHeroFiles.length; j++) {
                if (allHeroFiles[j].indexOf(heroFileEnding) >= 0) {
                    String testFile = allHeroFiles[j];
                    String[] testFileSplit = testFile.split("_");
                    Long testTime = Long.parseLong(testFileSplit[0]);
                    heroTimes.add(testTime);
                    if (testTime > latestTime) {
                        latestTime = testTime;
                        latestFileName = testFile;
                    }

                }
            }
            if (latestFileName.length() > 0) {
                HeroInfoContainer hic = new HeroInfoContainer(new File(DiabloFileReader.HERO_DIR + "/" + latestFileName), heroTimes);
                heroFiles.add(hic);
            }
        }

        return heroFiles;
    }

    public static void main(String[] args) {
        LatestDataGatherer ldg = new LatestDataGatherer(new ArrayList<DataListener>(), true, true);
        ldg.gatherLatestData();
    }

    private class HeroInfoContainer {

        public List<Long> heroTimes; //descending list of times of hero data
        public File latestHeroFile;

        public HeroInfoContainer(File latestHeroFile, List<Long> heroTimes) {
            this.latestHeroFile = latestHeroFile;
            this.heroTimes = heroTimes;
            Collections.sort(this.heroTimes);
            Collections.reverse(this.heroTimes);
        }
    }
}
