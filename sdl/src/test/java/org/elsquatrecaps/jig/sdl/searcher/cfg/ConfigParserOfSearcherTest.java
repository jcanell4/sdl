/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher.cfg;

import java.io.File;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.SearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.SearchIterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author josep
 */
public class ConfigParserOfSearcherTest {

    public ConfigParserOfSearcherTest() {
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
     * Test of getIterator method, of class ConfigParserOfSearcher.
     */
    @Test
    public void testGetIterator() {
        System.out.println("getIterator");
        File configRemoteFile = new File("conf/org.elsquatrecaps.jig.sdl.searcher.BvphGetRemoteProcess.xml");
        File configIteratorFile = new File("conf/org.elsquatrecaps.jig.sdl.searcher.BvphSearchIterator.xml");
        
        configRemoteFile.delete();
        configIteratorFile.delete();
        
        String repository = "Bvph";
        SearchCriteria params = new BvphSearchCriteria("marinero");
        SearchIterator result = ConfigParserOfSearcher.getIterator(repository, params);
        boolean hasNext = result.hasNext();
        assertTrue(hasNext);
        boolean hasConfigFile = configRemoteFile.exists();
        assertTrue(hasConfigFile);
        hasConfigFile = configIteratorFile.exists();
        assertTrue(hasConfigFile);
    }
    
}
