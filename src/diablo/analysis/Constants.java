/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josh
 */
public class Constants {
    public static final String BARB = "barbarian";
    public static final String DEMON_HUNTER = "demon-hunter";
    public static final String MONK = "monk";
    public static final String WITCH_DOCTOR = "witch-doctor";
    public static final String WIZARD = "wizard";
    
    public static List<String> STATS = new ArrayList<String>();
    public static List<String> SLOTS = new ArrayList<String>();
    public static List<String> CHARACTER_CLASSES = new ArrayList<String>();
    static {
        CHARACTER_CLASSES.add(BARB);
        CHARACTER_CLASSES.add(DEMON_HUNTER);
        CHARACTER_CLASSES.add(MONK);
        CHARACTER_CLASSES.add(WITCH_DOCTOR);
        CHARACTER_CLASSES.add(WIZARD);
        
        
        STATS.add("life");
        STATS.add("damage");
        STATS.add("attackSpeed");
        STATS.add("armor");
        STATS.add("strength");
        STATS.add("dexterity");
        STATS.add("intelligence");
        STATS.add("physicalResist");
        STATS.add("fireResist");
        STATS.add("coldResist");
        STATS.add("lightningResist");
        STATS.add("poisonResist");
        STATS.add("arcaneResist");
        STATS.add("critDamage");
        STATS.add("blockChance");
        STATS.add("blockAmountMin");
        STATS.add("blockAmountMax");
        STATS.add("damageIncrease");
        STATS.add("critChance");
        STATS.add("damageReduction");
        STATS.add("thorns");
        STATS.add("lifeSteal");
        STATS.add("lifePerKill");
        STATS.add("goldFind");
        STATS.add("magicFind");
        STATS.add("lifeOnHit");
        STATS.add("primaryResource");
        STATS.add("secondaryResource");
        
        SLOTS.add("head");
        SLOTS.add("torso");
        SLOTS.add("feet");
        SLOTS.add("hands");
        SLOTS.add("shoulders");
        SLOTS.add("legs");
        SLOTS.add("bracers");
        SLOTS.add("mainHand");
        SLOTS.add("offHand");
        SLOTS.add("waist");
        SLOTS.add("rightFinger");
        SLOTS.add("leftFinger");
        SLOTS.add("neck");
    }
}
