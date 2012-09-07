/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package diabloforumscraper;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.List;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author jwalton
 */
public class HeroGrabber {

     private DiabloData database;

     public HeroGrabber(DiabloData database){
          this.database = database;
     }

     public DBObject getHeroFromAPI(String heroUrl) {
          ClientRequest request = new ClientRequest(heroUrl);
          DBObject heroObject = null;
          try {
               ClientResponse<String> response = request.get(String.class);
               String heroString = response.getEntity();
               heroObject = (DBObject) JSON.parse(heroString);

          } catch (Exception e) {
               e.printStackTrace();
          }

          return heroObject;
     }

     public void getHeroes(){
          DBCursor cursor = database.getProfileData(0);
          int count = 0;
          while(cursor.hasNext()){
               DBObject profileData = cursor.next();
               ArrayList<DBObject> heroObjectList = new ArrayList<DBObject>();
               String profileName = (String) profileData.get("profile-name");
               Object object = profileData.removeField("heroes");
               if(object instanceof List){
                    List objectList = (List)object;
                    for(Object objectInList : objectList){
                         if(objectInList instanceof DBObject){
                              DBObject heroDBObject = (DBObject) objectInList;
                              Integer heroId = (Integer)heroDBObject.get("id");
//                              System.out.println("Found hero id: "+heroId);
                              String heroUrl = ProfileGrabber.API_ROOT+ProfileGrabber.PROFILE_URL+profileName+"/hero/"+heroId;
//                              System.out.println("Retrieving url: "+heroUrl);
                              DBObject heroObject = getHeroFromAPI(heroUrl);
                              if(heroObject != null){
                                   System.out.println(heroObject.toString());
                                   heroObjectList.add(heroObject);
                              }
                         } else {
                              System.out.println("Inner list object is not DBObject!");
                         }
                    }
                    profileData.put("heroes", heroObjectList);
                    System.out.println(profileData.get("heroes").toString());
                    database.insertProfileWithCharacterData(profileData);
                    count++;
                    System.out.println("Inserted "+count+" profiles with character data");
               } else {
                    System.out.println("heroes value was not list!");
               }

          }


     }


     public static void main(String[] args){
          DiabloData data = new DiabloData();

          HeroGrabber heroGrabber = new HeroGrabber(data);
          heroGrabber.getHeroes();
     }
}
