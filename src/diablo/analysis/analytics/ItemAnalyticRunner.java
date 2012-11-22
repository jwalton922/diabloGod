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
public class ItemAnalyticRunner {

    public static void main(String[] args) {
        boolean test = false;
        MongoOutputter mongoOutputter = new MongoOutputter(test);
        long startTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        Date date = new Date(startTime);
        String dateString = format.format(date);

        ItemAttributeCommonalityAnalytic analytic1 = new ItemAttributeCommonalityAnalytic(mongoOutputter, dateString);


        List<DataListener> analytics = new ArrayList<DataListener>();

        analytics.add(analytic1);


        LatestDataGatherer ldg = new LatestDataGatherer(analytics, true, true);
        ldg.setMinCharacterLevel(60);
        //ldg.setMaxProfilesToCheck(50);
        ldg.gatherLatestData();


        for (DataListener analytic : analytics) {
            analytic.outputResults();
        }
    }
}
