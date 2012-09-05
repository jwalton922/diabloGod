/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jsoup.Connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author jwalto
 */
public class DiabloForumScraper {

    //private static Logger log = Logger.getLogger(DiabloForumScraper.class);
    
    private static final String DIABLO_ROOT = "http://us.battle.net/d3/en/";
    private static final String GENERAL_DISCUSSION_ROOT = DIABLO_ROOT+"forum/3354739/";
    private static final String FORUM_TOPIC_ROOT = DIABLO_ROOT+"forum/topic/";
    private static final String USER_AGENT = "Mozilla";
    
    
    public Document getDocument(String url){
        Document doc = null;
        Connection connection = Jsoup.connect(url);
        connection.userAgent(USER_AGENT);
        try {
            doc = connection.get();
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return doc;
    }
    
    public List<String> getForumTopicUrls(Document doc){
        List<String> topicIds = new ArrayList<String>();
        Elements forumTopicRows = doc.select("tbody tr");
        System.out.println("Found "+forumTopicRows.size()+" topics");
        for(Element forumTopicRow : forumTopicRows){
            System.out.println("row html id = "+forumTopicRow.id());
            String topicId = forumTopicRow.id().replace("postRow", "");
            topicIds.add(topicId);
            System.out.println("topicId = "+topicId);
            
        }
        
        return topicIds;
    }
    
    private TopicScraperResult processTopic(String topicId, String pageLink){
        List<String> profiles = new ArrayList<String>();
        String topicPage = FORUM_TOPIC_ROOT+topicId;
        if(pageLink != null){
            topicPage+=pageLink;
        }
        System.out.println("Retrieving topic page: "+topicPage);
        Document topicDocument = getDocument(topicPage);
        Elements profileLinks = topicDocument.select(".view-d3-profile");
        System.out.println("Found "+profileLinks.size()+" profile links");
        for(Element profileLink : profileLinks){
           String link = profileLink.attr("href") ;
           String profile = link.replace("/d3/en/profile/", "");
           //remove ending forward slash
           profile = profile.substring(0, profile.length()-1);
           System.out.println("Found profile: "+profile);
        }
        
        Elements nextPageSpans = topicDocument.select("li.cap-item a span");
        System.out.println("Found "+nextPageSpans.size()+" next page spans");
        String nextPageLink = null;
        for(Element nextPageSpan : nextPageSpans){
            String spanText = nextPageSpan.text();
            System.out.println("Span text = "+spanText);
            if(spanText.equalsIgnoreCase("Next")){
                //get parent which is the link tag
                Element parent = nextPageSpan.parent();
                nextPageLink = parent.attr("href");
                System.out.println("Next page link = "+nextPageLink);
                break;
            }
        }
        
        TopicScraperResult result= new TopicScraperResult(profiles, nextPageLink);
        
        return result;
    }
    
    
    
    
    public String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DiabloForumScraper scraper = new DiabloForumScraper();
        Document doc = scraper.getDocument(GENERAL_DISCUSSION_ROOT);
        List<String> topicIds = scraper.getForumTopicUrls(doc);
        scraper.processTopic("6490010268", null);
    }
    
    private class TopicScraperResult {
        public List<String> profiles;
        public String nextPageLink;
        
        public TopicScraperResult(List<String> profiles, String nextPageLink){
            this.profiles = profiles;
            this.nextPageLink = nextPageLink;
        }
    }
}
