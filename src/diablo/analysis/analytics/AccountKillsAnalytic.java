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
public class AccountKillsAnalytic implements DataListener {

    private MongoOutputter outputter;
    private double eliteSum = 0;
    private double killSum = 0;
    private int profileCount = 0;
    private int[] eliteBins = new int[101];
    private int[] killBins = new int[101];
    private String date;

    public AccountKillsAnalytic(MongoOutputter outputter, String date) {
        this.outputter = outputter;
        this.date = date;
        for (int i = 0; i < eliteBins.length; i++) {
            eliteBins[i] = 0;
        }
        
        for (int i = 0; i < killBins.length; i++) {
            killBins[i] = 0;
        }
    }

    @Override
    public void processProfile(Profile profile) {
        if (profile.getMaxParagonLevel() >= 0) {
            eliteSum+= profile.getEliteKills();
            killSum+= profile.getKills();
            profileCount++;
            int eliteBinIndex = (int)(Math.floor(profile.getEliteKills()/1000.0));
            if(eliteBinIndex > 100){
                eliteBinIndex = 100;
            }
            int killsBinIndex = (int)(Math.floor(profile.getKills()/10000.0));
            if(killsBinIndex > 100){
                killsBinIndex = 100;
            }
            
            eliteBins[eliteBinIndex] = eliteBins[eliteBinIndex] +1;
            killBins[killsBinIndex] = killBins[killsBinIndex]+1;
            
            
        }
    }

    @Override
    public void processHero(Hero hero) {
    }

    @Override
    public void processItem(Item item) {
    }

    public void outputResults() {
        double eliteAvg = eliteSum / (1.0 * profileCount);
        double killAvg = killSum / (1.0 * profileCount);
        System.out.println("average elite kills = " + eliteAvg + " for " + profileCount + " profiles");
        System.out.println("average monster kills = " + killAvg + " for " + profileCount + " profiles");
        System.out.println("Elite kill bins: " + Arrays.toString(eliteBins));
        System.out.println("Monster kill bins: " + Arrays.toString(killBins));
        
        DBObject resultObject = new BasicDBObject();
        
        resultObject.put("averageEliteKills", eliteAvg);
        resultObject.put("averageKills", killAvg);
        resultObject.put("profileCount", profileCount);
        resultObject.put("eliteBins", eliteBins);
        resultObject.put("killBins", killBins);
        resultObject.put("analytic-name", this.getClass().getName());
        resultObject.put("date", date);
        
        this.outputter.writeStat(resultObject);
        
    }
}

