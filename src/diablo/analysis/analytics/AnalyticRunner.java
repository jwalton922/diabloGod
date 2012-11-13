/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis.analytics;

import diablo.analysis.DataListener;
import diablo.analysis.LatestDataGatherer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josh
 */
public class AnalyticRunner {
    
    public static void main(String[] args){
        AccountParagonLevelAnalytic analytic1 = new AccountParagonLevelAnalytic();
        
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
