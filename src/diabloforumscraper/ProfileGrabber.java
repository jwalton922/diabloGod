/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author jwalton
 */
public class ProfileGrabber {

     public static final String API_ROOT = "http://us.battle.net/api/d3/";
     public static final String PROFILE_URL = "profile/";
     private DiabloData database;

     public ProfileGrabber(DiabloData database){
          this.database = database;
     }

     public void insertProfileData(){
          DBCursor cursor = database.getProfileNames(24893, 10000);
          int count = 0;
          while(cursor.hasNext()){
               DBObject profileNameObject = cursor.next();
               String profileName = (String)profileNameObject.get("profile");
               System.out.println("getting profile data for:  "+profileName);
               getProfileAndSave(profileName);
               count++;
               System.out.println("Saved "+count+" profiles");
          }
     }
     

     public void getProfileAndSave(String profile) {
          ClientRequest request = new ClientRequest(API_ROOT + PROFILE_URL + "/" + profile + "/");
          try {
               ClientResponse<String> response = request.get(String.class);
               String profileString = response.getEntity();
               DBObject profileObject = (DBObject) JSON.parse(profileString);
               profileObject.put("profile-name", profile);
               this.database.insertProfileData(profileObject);
               

          } catch (Exception e) {
               e.printStackTrace();
          }
     }

     public static void main(String[] args){
          DiabloData data = new DiabloData();
          ProfileGrabber profileGrabber = new ProfileGrabber(data);
          profileGrabber.insertProfileData();
     }
}
