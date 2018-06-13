package org.elsquatrecaps.jig.sdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
import org.elsquatrecaps.jig.sdl.persistence.SearchRepository;
import org.elsquatrecaps.jig.sdl.services.PersistenceService;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RunWith(SpringRunner.class)
@EnableTransactionManagement
@SpringBootTest
public class SdlApplicationTests {
    @Autowired
    ResourceRepository resourceRepository;
    
    @Autowired
    SearchRepository searchRepository;
    
//    @Autowired
//    PlatformTransactionManager transactionManager;
    

    @Test
    public void contextLoads() {
//        testCreateSearchWithService();
//        testGetSearchWithService();
//        testUpdateDateOfSearchWithService();
//        testGetResourcesFromSearch();
    }
    
        
//    private void testCreateSearchWithService() {
//        System.out.println("testCreateSearchWithService");
//        Search search = new Search("BVPH", "Paraula", "18/03/2018");
//        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
//        ArrayList<Resource> resourceList = new ArrayList<>();
//
//        resourceList.add(new Resource("r00001", "Title 1", "page x", "25/02/1865", "a45671_title1", ((String) null), "21/01/2018", new String[]{"Bla bla bña frg1", "Ble, bli, blo, frg2"}));
//        resourceList.add(new Resource("r00002", "Title 2", "page y", "16/06/1977", "a76032_title2", ((String) null), "21/01/2018", new String[]{"T2: Bla bla bña frg1", "T2: Ble, bli, blo, frg2"}));
//        resourceList.add(new Resource("r00003", "Title 3", "page z", "03/10/1989", "a2385_title3", ((String) null), "21/01/2018", new String[]{"T3: Bla bla bña frg1", "T3: Ble, bli, blo, frg2"}));
//        search.setResources(resourceList);
//
//        instance.saveSearch(search);
//        System.out.println(search.getId());
//    }
//    
//    private void testGetSearchWithService() {
//        System.out.println("createSearch");
//        Search expected = new Search("BVPH", "Paraula", "18/03/2018");
//        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
//        List<Search> returnSearchList = instance.findAllSearch();
//        Search returnSearch = returnSearchList.get(returnSearchList.size()-1);
//        assertEquals(expected, returnSearch);        
//        System.out.println(returnSearch.getId());
//    }
//    
//    private void testUpdateDateOfSearchWithService(){
//        System.out.println("updateSearch");
//        Search expected = new Search("BVPH", "Paraula", "12/05/02018");
//        Search search;
//        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
//        ArrayList<Resource> resourceList = new ArrayList<>();
//        
//        resourceList.add(new Resource("r00001", "Title 1", "page x", "25/02/1865", "a45671_title1", ((String) null), "21/01/2018", new String[]{"Bla bla bña frg1", "Ble, bli, blo, frg2"}));
//        resourceList.add(new Resource("r00002", "Title 2", "page y", "16/06/1977", "a76032_title2", ((String) null), "21/01/2018", new String[]{"T2: Bla bla bña frg1", "T2: Ble, bli, blo, frg2"}));
//        resourceList.add(new Resource("r00003", "Title 3", "page z", "03/10/1989", "a2385_title3", ((String) null), "21/01/2018", new String[]{"T3: Bla bla bña frg1", "T3: Ble, bli, blo, frg2"}));
//        expected.setResources(resourceList);
//
//        expected = instance.saveSearch(expected);
//        search = instance.findAllSearch().get(0);
//        assertEquals(expected, search);                
//    }
//    
//    private void testGetResourcesFromSearch(){
//        System.out.println("updateSearch");
//        Search expected = new Search("BVPH", "Paraula", "12/05/02018");
//        Search search;
//        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
//        search = instance.findAllSearch().get(0);
//        List<Resource>  resources = instance.findAllResourceBySerach(search);
//
//        assertEquals(3, resources.size());                
//        
//        for(Resource r: resources){
//            System.out.println(r);
//        }
//    }

}
