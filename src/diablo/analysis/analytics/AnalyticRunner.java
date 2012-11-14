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
        
        MongoOutputter mongoOutputter = new MongoOutputter();
        long startTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        Date date = new Date(startTime);
        String dateString = format.format(date);
        
        AccountParagonLevelAnalytic analytic1 = new AccountParagonLevelAnalytic(mongoOutputter, dateString);
        
        List<DataListener> analytics = new ArrayList<DataListener>();
        
        analytics.add(analytic1);
        
        LatestDataGatherer ldg = new LatestDataGatherer(analytics, true, true);
        ldg.setMinCharacterLevel(60);
        ldg.gatherLatestData();
        
        for(DataListener analytic : analytics){
            analytic.outputResults();
        }
    }
}
