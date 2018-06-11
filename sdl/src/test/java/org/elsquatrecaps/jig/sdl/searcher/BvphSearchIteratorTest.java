/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.HashMap;
import java.util.Map;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BvphSearchIteratorTest {
        static Map<String, String> params;
        static BvphGetRemoteProcess remote;
    
    public BvphSearchIteratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        params = new HashMap<>();
            
        params.put("busq_general", "marinero");
        params.put("descrip_idlistpais", "España");
        params.put("general_ocr", "on");

        remote = new BvphGetRemoteProcess(params);
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
     * Test of init method, of class BvphSearchIterator.
     */
    @Test
    public void testInit_GetRemoteProcess() {
        System.out.println("init");
        BvphSearchIterator instance = new BvphSearchIterator();
        instance.init(remote);
        int bigger0 = remote.getDefaultBiggerYear();
        int bigger1 = instance.getCurrentBiggerYear();
        // TODO review the generated test code and remove the default call to fail.
        assertEquals(bigger0, bigger1);
    }

    /**
     * Test of init method, of class BvphSearchIterator.
     */
    @Test
    public void testInit_Element_BvphGetRemoteProcess() {
        System.out.println("init");
        Element element = remote.get();
        BvphSearchIterator instance = new BvphSearchIterator();
        instance.init(element, remote);
        boolean toomuch = instance.thereIsTooMuchResources();
        // TODO review the generated test code and remove the default call to fail.
        assertTrue(toomuch);
    }

    /**
     * Test of hasNext method, of class BvphSearchIterator.
     */
    @Test
    public void testHasNext() {
         System.out.println("hasNext");
         testHasNext0();
         testHasNext1();
         testHasNext2();
    }

    private void testHasNext0() {
        try{
            System.out.println("hasNext - marinero");
            remote.setParam("busq_general", "marinero");
            BvphSearchIterator iterator= new BvphSearchIterator(remote);
            assertTrue(iterator.hasNext());
        }catch (Exception ex){
            fail("S'ha produit una excepció");
        }        
    }

    private void testHasNext1() {
        try{
            System.out.println("hasNext - aliacalil");
            remote.setParam("busq_general", "aliacalil");
            BvphSearchIterator iterator= new BvphSearchIterator(remote);
            assertFalse(iterator.hasNext());
        }catch (Exception ex){
            fail("S'ha produit una excepció");
        }        
    }

    private void testHasNext2() {
        try{
            System.out.println("hasNext - streptococo");
            remote.setParam("busq_general", "streptococo");
            BvphSearchIterator iterator= new BvphSearchIterator(remote);
            assertTrue(iterator.hasNext());
        }catch (Exception ex){
            fail("S'ha produit una excepció");
        }        
    }
    
    /**
     * Test of next method, of class BvphSearchIterator.
     */
@Test
    public void testNext() {
        FormatedFile ff;
        BvphResource resource;
        System.out.println("next");
        System.out.println("next - comando");
        remote.setParam("busq_general", "comando");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        if(iterator.hasNext()){
            resource = iterator.next();
            assertNotNull(resource);
            ff = resource.getFormatedFile("txt");
            assertNotNull(ff);
            ff = resource.getFormatedFile("xml");
            assertNotNull(ff);
            ff = resource.getFormatedFile("jpg");
            assertNotNull(ff);
        }else{
            fail("hasNext() return bad value");
        }
        
//        BvphSearchIterator instance = null;
//        BvphResource expResult = null;
//        BvphResource result = instance.next();
//        assertEquals(expResult, result);
//         TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    /**
     * Test of getCurrentBiggerYear method, of class BvphSearchIterator.
     */
        @Test
    public void testGetCurrentBiggerYear() {
        System.out.println("getCurrentBiggerYear");
        remote.setBiggerYear(2010);
        BvphSearchIterator instance = new BvphSearchIterator(remote);
        int expResult = 2010;
        int result = instance.getCurrentBiggerYear();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentSmallerYear method, of class BvphSearchIterator.
     */
        @Test
    public void testGetCurrentSmallerYear() {
        System.out.println("getCurrentSmallerYear");
        remote.setParam("busq_general", "marinero");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        int year = iterator.getCurrentSmallerYear();
        System.out.println("Year: " + year);
        remote.setParam("busq_rango0_fechapubinicial__fechapubfinal", String.format("01/01/%d", year+1));
        iterator= new BvphSearchIterator(remote);
        iterator.hasNext();
        assertFalse(iterator.thereIsTooMuchResources());
    }


    /**
     * Test of noResources method, of class BvphSearchIterator.
     */
        @Test
    public void testNoResources() {
        System.out.println("noResources");
        testNoResources0();
        testNoResources1();
        testNoResources2();
    }
    
    /**
     * Test of noResources method, of class BvphSearchIterator.
     */
    private void testNoResources0() {
        System.out.println("noResources-false");
        remote.setParam("busq_general", "aliacalil");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        boolean expResult = true;
        boolean result = iterator.noResources();
        assertEquals(expResult, result);
    }

    /**
     * Test of noResources method, of class BvphSearchIterator.
     */
    private void testNoResources1() {
        System.out.println("noResources-true1");
        remote.setParam("busq_general", "streptococo");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        boolean expResult = false;
        boolean result = iterator.noResources();
        assertEquals(expResult, result);
    }

    /**
     * Test of noResources method, of class BvphSearchIterator.
     */
    private void testNoResources2() {
        System.out.println("noResources-true1");
        remote.setParam("busq_general", "marinero");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        boolean expResult = false;
        boolean result = iterator.noResources();
        assertEquals(expResult, result);
    }


    /**
     * Test of thereIsTooMuchResources method, of class BvphSearchIterator.
     */
    @Test
    public void testThereIsTooMuchResources() {
        System.out.println("thereIsTooMuchResources");
        testThereIsTooMuchResources0();
        testThereIsTooMuchResources1();
        testThereIsTooMuchResources2();
    }
    
    /**
     * Test of thereIsTooMuchResources method, of class BvphSearchIterator.
     */
    private void testThereIsTooMuchResources0() {
        System.out.println("thereIsTooMuchResources - true");
        remote.setParam("busq_general", "marinero");
        remote.setBiggerYear(2018);
        remote.setParam("busq_rango0_fechapubinicial__fechapubfinal", "01/01/1500");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        boolean expResult = true;
        boolean result = iterator.thereIsTooMuchResources();
        assertEquals(expResult, result);
    }

    /**
     * Test of thereIsTooMuchResources method, of class BvphSearchIterator.
     */
    private void testThereIsTooMuchResources1() {
        System.out.println("thereIsTooMuchResources - false1");
        remote.setParam("busq_general", "streptococo");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        boolean expResult = false;
        boolean result = iterator.thereIsTooMuchResources();
        assertEquals(expResult, result);
    }

    /**
     * Test of thereIsTooMuchResources method, of class BvphSearchIterator.
     */
    private void testThereIsTooMuchResources2() {
        System.out.println("thereIsTooMuchResources - false1");
        remote.setParam("busq_general", "aliacalil");
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        iterator.hasNext();        
        boolean expResult = false;
        boolean result = iterator.thereIsTooMuchResources();
        assertEquals(expResult, result);
    }
}
