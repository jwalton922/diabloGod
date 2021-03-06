/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis.analytics;

import diablo.analysis.DataListener;
import diablo.analysis.LatestDataGatherer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Josh
 */
public class AnalyticRunner {
    
    public static void main(String[] args){
        boolean test = false;
        MongoOutputter mongoOutputter = new MongoOutputter(test);
        long startTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        Date date = new Date(startTime);
        String dateString = format.format(date);
        
        AccountParagonLevelAnalytic analytic1 = new AccountParagonLevelAnalytic(mongoOutputter, dateString);
        AccountKillsAnalytic analytic2 = new AccountKillsAnalytic(mongoOutputter, dateString);
        CharacterStatsAnalytic analytic3 = new CharacterStatsAnalytic(mongoOutputter, dateString);
        
        List<DataListener> analytics = new ArrayList<DataListener>();
        
        analytics.add(analytic1);
        analytics.add(analytic2);
        analytics.add(analytic3);
        
        LatestDataGatherer ldg = new LatestDataGatherer(analytics, true, true);
        ldg.setMinCharacterLevel(60);
        //ldg.setMaxProfilesToCheck(100);
        ldg.gatherLatestData();
        
        
        for(DataListener analytic : analytics){
            analytic.outputResults();
        }
    }
}
