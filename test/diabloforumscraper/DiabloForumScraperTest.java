/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diabloforumscraper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jwalto
 */
public class DiabloForumScraperTest {
    
    public DiabloForumScraperTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getForumRoot method, of class DiabloForumScraper.
     */
    @Test
    public void testGetForumRoot() {
        System.out.println("getForumRoot");
        DiabloForumScraper instance = new DiabloForumScraper();
        instance.getForumRoot();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHTML method, of class DiabloForumScraper.
     */
    @Test
    public void testGetHTML() {
        System.out.println("getHTML");
        String urlToRead = "";
        DiabloForumScraper instance = new DiabloForumScraper();
        String expResult = "";
        String result = instance.getHTML(urlToRead);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class DiabloForumScraper.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        DiabloForumScraper.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
