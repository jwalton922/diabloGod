/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diablo.analysis;

/**
 *
 * @author Josh
 */
public interface DataListener {
    public void processProfile(Profile profile);
    public void processHero(Hero hero);
    public void processItem(Item item);
    public void outputResults();
}
