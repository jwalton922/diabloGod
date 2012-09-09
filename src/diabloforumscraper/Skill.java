/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package diabloforumscraper;

/**
 *
 * @author jwalton
 */
public class Skill {

     private String name;
     private String rune;

     public Skill(String name, String rune){
          this.name = name;
          this.rune = rune;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getRune() {
          return rune;
     }

     public void setRune(String rune) {
          this.rune = rune;
     }

     
}
