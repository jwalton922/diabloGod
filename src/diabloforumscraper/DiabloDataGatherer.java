/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author jwalton
 */
public class DiabloDataGatherer {

    private static Logger logger = Logger.getLogger(DiabloDataGatherer.class);
    private DiabloData database;
    private DiabloItemCreator itemCreator;
    private boolean initialLoad = false;

    public DiabloDataGatherer(DiabloData database, boolean initialLoad) {
        this.database = database;
        this.itemCreator = new DiabloItemCreator();
        this.initialLoad = initialLoad;
    }

    public class DiabloDataGathererThread implements Runnable {

        private int offset;
        private int limit;
        private boolean initialLoad = false;
        private Logger log = Logger.getLogger(DiabloDataGathererThread.class);

        public DiabloDataGathererThread(int offset, int limit, boolean initialLoad) {
            this.offset = offset;
            this.limit = limit;
            this.initialLoad = initialLoad;
        }

        public void run() {
            log.info("Run called on DiabloDatabaseThread with offset "+offset+" limit: "+limit);
            System.out.println("Run called on DiabloDatabaseThread with offset "+offset+" limit: "+limit);
            DBCursor profileCursor = database.getProfileNames(offset, limit);
            int profileCount = 0;
            while (profileCursor.hasNext()) {

                DBObject profileObject = profileCursor.next();
                String profile = (String) profileObject.get("profile");
                if (initialLoad && !database.haveItemDataForProfile(profile)) {
                    log.info("Getting item data for profile: " + profile);
                    List<DiabloItemRecord> items = itemCreator.createDiabloItems(profile);
                    log.debug("Have " + items.size() + " items to insert");
                    System.out.println("Have " + items.size() + " items to insert");
                    for (DiabloItemRecord item : items) {
                        database.insertItemData(item.toDBObject());
                    }
                } else {
                    log.info("skipping profile: " + profile + ". Already have item data");
                }
                profileCount++;
                log.debug("Finished processing profile " + profileCount + " for offset = " + offset);
                System.out.println("Finished processing profile " + profileCount + " for offset = " + offset);
            }
            System.out.println("Run finished for DiabloDatabaseThread with offset"+offset);
        }
    }

    public void gatherItemData() {
        int profileCount = database.getProfileNamesCount();
        logger.info("There are " + profileCount + " profiles to process");
        int threadsNeeded = (int) (Math.ceil(profileCount / (10000.0)));
        logger.info("need " + threadsNeeded + " threads");

        ExecutorService pool = Executors.newFixedThreadPool(6);

        for (int i = 0; i < threadsNeeded; i++) {
            int offset = 10000 * i;
            int limit = 10000;
            DiabloDataGathererThread thread = new DiabloDataGathererThread(offset, limit, initialLoad);
            pool.execute(thread);

        }
        try {
            logger.info("Awaiting termination");
            pool.awaitTermination(5, TimeUnit.DAYS);

            logger.info("Done awaiting termination");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR awaiting termination!", e);
        }


    }

    public static void main(String[] args) {
        DiabloData database = new DiabloData();
        DiabloDataGatherer dataGatherer = new DiabloDataGatherer(database, true);
        dataGatherer.gatherItemData();
    }
}
