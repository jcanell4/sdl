/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.nodes.Element;
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
public class BvphGetRemoteProcessTest {
    BvphGetRemoteProcess instance = new BvphGetRemoteProcess();
    static Map<String, String> params = new HashMap<>();
    
    public BvphGetRemoteProcessTest() {
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
     * Test of setParam method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testSetParam() {
        testSetParam0();
        testSetParamDate0();
        testSetParamDate1();
    }

    private void testSetParam0() {
        System.out.println("setParam0");
        String key = "busq_general";
        String value = "aliacalil";
        instance.setParam(key, value);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("aliacalil", instance.getParam("busq_general"));
    }

    private void testSetParamDate0() {
        System.out.println("setParam-date0");
        String key = "busq_rango0_fechapubinicial__fechapubfinal";
        String value = "05/05/2005";
        instance.setParam(key, value);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("05/05/2005", instance.getParam("busq_rango0_fechapubinicial__fechapubfinal"));
        assertEquals(2005, instance.getDefaultSmallerYear());
    }

    private void testSetParamDate1() {
        System.out.println("setParam-date1");
        String key = "busq_rango1_fechapubinicial__fechapubfinal";
        String value = "05/05/2005";
        instance.setParam(key, value);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("05/05/2005", instance.getParam("busq_rango1_fechapubinicial__fechapubfinal"));
        assertEquals(2005, instance.getDefaultBiggerYear());
    }

    /**
     * Test of setCriteria method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testSetCriteria() {
        testSetCriteria0();
        testSetCriteria1();
        
    }
    
    private void testSetCriteria0() {
        System.out.println("setCriteria0");
        SearchCriteria criteria = new BvphSearchCriteria("Marinera");
        instance.setCriteria(criteria);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("Marinera", instance.getParam("busq_general"));
    }

    private void testSetCriteria1() {
        System.out.println("setCriteria1");
        SearchCriteria criteria = new BvphSearchCriteria("Capitana", 1997, 2015);
        instance.setCriteria(criteria);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("Capitana", instance.getParam("busq_general"));
        assertEquals("01/01/1997", instance.getParam("busq_rango0_fechapubinicial__fechapubfinal"));
        assertEquals(1997, instance.getDefaultSmallerYear());
        assertEquals("31/12/2015", instance.getParam("busq_rango1_fechapubinicial__fechapubfinal"));
        assertEquals(2015, instance.getDefaultBiggerYear());
    }

    /**
     * Test of setParams method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testSetParams() {
        testSetParams0();
        testSetParams1();
        testSetParams2();
    }

    private void testSetParams0() {
        System.out.println("setParams");   String key = "busq_general"; String value = "marinero";  params.put(key, value);
        instance.setParams(params);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("marinero", instance.getParam("busq_general"));
    }

    private void testSetParams1() {
        System.out.println("setParams-date0");
        params.put("busq_rango0_fechapubinicial__fechapubfinal", "05/05/2005");
        instance.setParams(params);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("05/05/2005", instance.getParam("busq_rango0_fechapubinicial__fechapubfinal"));
        assertEquals(2005, instance.getDefaultSmallerYear());
    }

    private void testSetParams2() {
        System.out.println("setParams-date1");
        params.put("busq_rango1_fechapubinicial__fechapubfinal", "05/05/2005");
        instance.setParams(params);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("05/05/2005", instance.getParam("busq_rango1_fechapubinicial__fechapubfinal"));
        assertEquals(2005, instance.getDefaultBiggerYear());
    }

    /**
     * Test of getDefaultBiggerYear method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testGetDefaultBiggerYear() {
        System.out.println("getDefaultBiggerYear");
        int expResult = Calendar.getInstance().get(Calendar.YEAR);
        int result = instance.getDefaultBiggerYear();
        // TODO review the generated test code and remove the default call to fail.
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefaultSmallerYear method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testGetDefaultSmallerYear() {
        System.out.println("getDefaultSmallerYear");
        int expResult = 1500;
        int result = instance.getDefaultSmallerYear();
        assertEquals(expResult, result);
    }

    /**
     * Test of setText method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testSetText() {
        System.out.println("setText");
        String criteria = "Veler";
        instance.setText(criteria);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("Veler", instance.getParam("busq_general"));
    }

    /**
     * Test of setBiggerYear method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testSetBiggerYear() {
        System.out.println("setBiggerYear");
        int bigger = 2000;
        instance.setBiggerYear(bigger);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("31/12/2000", instance.getParam("busq_rango1_fechapubinicial__fechapubfinal"));
        assertEquals(2000, instance.getDefaultBiggerYear());
    }

    /**
     * Test of setSmallerYear method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testSetSmallerYear() {
        System.out.println("setSmallerYear");
        int smaller = 1900;
        instance.setSmallerYear(smaller);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("01/01/1900", instance.getParam("busq_rango0_fechapubinicial__fechapubfinal"));
        assertEquals(1900, instance.getDefaultSmallerYear());
    }

    /**
     * Test of get method, of class BvphGetRemoteProcess.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        instance.setText("capitán");
        Element result = instance.get();
        // TODO review the generated test code and remove the default call to fail.
        assertNotNull(result);
    }
}
