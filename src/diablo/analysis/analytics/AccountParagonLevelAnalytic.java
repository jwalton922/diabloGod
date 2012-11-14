/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis.analytics;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import diablo.analysis.DataListener;
import diablo.analysis.Hero;
import diablo.analysis.Item;
import diablo.analysis.Profile;
import java.util.Arrays;

/**
 *
 * @author Josh
 */
public class AccountParagonLevelAnalytic implements DataListener {

    private MongoOutputter outputter;
    private double paragonLevelSum = 0;
    private int profileCount = 0;
    private int[] paragonBins = new int[101];
    private String date;

    public AccountParagonLevelAnalytic(MongoOutputter outputter, String date) {
        this.outputter = outputter;
        this.date = date;
        for (int i = 0; i < paragonBins.length; i++) {
            paragonBins[i] = 0;
        }
    }

    @Override
    public void processProfile(Profile profile) {
        if (profile.getMaxParagonLevel() >= 0) {
            paragonLevelSum += profile.getMaxParagonLevel();
            profileCount++;
            paragonBins[profile.getMaxParagonLevel()] = paragonBins[profile.getMaxParagonLevel()] + 1;
        }
    }

    @Override
    public void processHero(Hero hero) {
    }

    @Override
    public void processItem(Item item) {
    }

    public void outputResults() {
        double avg = paragonLevelSum / (1.0 * profileCount);
        System.out.println("average max paragon level = " + avg + " for " + profileCount + " profiles");
        System.out.println("Paragon bins: " + Arrays.toString(paragonBins));
        
        DBObject resultObject = new BasicDBObject();
        
        resultObject.put("averageParagonLevel", avg);
        resultObject.put("profileCount", profileCount);
        resultObject.put("paragonLevelBins", paragonBins);
        resultObject.put("analytic-name", this.getClass().getName());
        resultObject.put("date", date);
        
        
    }
}
