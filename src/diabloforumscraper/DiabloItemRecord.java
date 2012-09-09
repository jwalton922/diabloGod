/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package diabloforumscraper;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jwalton
 */
public class DiabloItemRecord {

     private HashMap<String,Double> itemStats = new HashMap<String,Double>();
     private String itemSlot;
     private int characterLevel;
     private int paragonLevel;
     private boolean hardcore;
     private int accountMaxLevel;
     private int accountMaxParagonLevel;
     private int characterEliteKills;
     private int totalAccountEliteKills;
     private String characterClass;
     private int characterProgress;
     private int accountProgress;
     private List<Skill> skills;
     private String profileName;


     public DiabloItemRecord(){

     }

     public int getAccountMaxLevel() {
          return accountMaxLevel;
     }

     public void setAccountMaxLevel(int accountMaxLevel) {
          this.accountMaxLevel = accountMaxLevel;
     }

     public int getAccountMaxParagonLevel() {
          return accountMaxParagonLevel;
     }

     public void setAccountMaxParagonLevel(int accountMaxParagonLevel) {
          this.accountMaxParagonLevel = accountMaxParagonLevel;
     }

     public int getAccountProgress() {
          return accountProgress;
     }

     public void setAccountProgress(int accountProgress) {
          this.accountProgress = accountProgress;
     }

     public String getCharacterClass() {
          return characterClass;
     }

     public void setCharacterClass(String characterClass) {
          this.characterClass = characterClass;
     }

     public int getCharacterEliteKills() {
          return characterEliteKills;
     }

     public void setCharacterEliteKills(int characterEliteKills) {
          this.characterEliteKills = characterEliteKills;
     }

     public int getCharacterLevel() {
          return characterLevel;
     }

     public void setCharacterLevel(int characterLevel) {
          this.characterLevel = characterLevel;
     }

     public int getCharacterProgress() {
          return characterProgress;
     }

     public void setCharacterProgress(int characterProgress) {
          this.characterProgress = characterProgress;
     }

     public boolean isHardcore() {
          return hardcore;
     }

     public void setHardcore(boolean hardcore) {
          this.hardcore = hardcore;
     }

     public HashMap<String, Double> getItemStats() {
          return itemStats;
     }

     public void setItemStats(HashMap<String, Double> itemStats) {
          this.itemStats = itemStats;
     }

     public int getParagonLevel() {
          return paragonLevel;
     }

     public void setParagonLevel(int paragonLevel) {
          this.paragonLevel = paragonLevel;
     }

     public String getProfileName() {
          return profileName;
     }

     public void setProfileName(String profileName) {
          this.profileName = profileName;
     }

     public List<Skill> getSkills() {
          return skills;
     }

     public void setSkills(List<Skill> skills) {
          this.skills = skills;
     }

     public int getTotalAccountEliteKills() {
          return totalAccountEliteKills;
     }

     public void setTotalAccountEliteKills(int totalAccountEliteKills) {
          this.totalAccountEliteKills = totalAccountEliteKills;
     }

     public String getItemSlot() {
          return itemSlot;
     }

     public void setItemSlot(String itemSlot) {
          this.itemSlot = itemSlot;
     }

     

}
