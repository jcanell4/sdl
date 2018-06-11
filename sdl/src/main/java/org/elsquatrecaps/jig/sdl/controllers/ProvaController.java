/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.elsquatrecaps.jig.sdl.configuration.Prova1Properties;
import org.elsquatrecaps.jig.sdl.configuration.Prova2Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.ErrorCopyingFileFormaException;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.TestResource;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
import org.elsquatrecaps.jig.sdl.persistence.SearchRepository;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchIterator;
import org.elsquatrecaps.jig.sdl.searcher.SearchResource;
import org.elsquatrecaps.jig.sdl.searcher.cfg.ConfigParserOfSearcher;
import org.elsquatrecaps.jig.sdl.services.PersistenceService;
import org.elsquatrecaps.jig.sdl.services.ProvaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProvaController { 
    private DownloaderProperties dp;
    private Prova2Properties pp;
    private ProvaUserService userService;
    
    @Autowired(required = true)
    ResourceRepository resourceRepository;
    @Autowired(required = true)
    SearchRepository searchRepository;
    @Autowired(required = true)
    PlatformTransactionManager transactionManager;
    
    
    
    
    @RequestMapping(value = "/")
    public ModelAndView rootHandler(){
        String view = "aaa";
        ModelAndView ret = new ModelAndView(view);
        ret.addObject("localResRep", dp.getLocalReasourceRepo());
        ret.addObject("db", dp.getDb());
        ret.addObject("dp", dp);
        ret.addObject("pp", pp);
        return ret;          
    }     
    
    @GetMapping("/ajax")
    public String ajaxHandler() {
        return "ajax";
    }

    @RequestMapping("/ajax/usernames")
    public ModelAndView handleUsersViaAjax() {
        String view = "ajax :: users";
        ModelAndView ret = new ModelAndView(view);
        ret.addObject("usernames", userService.findUserNames());
        return ret;          
    }
    
    @RequestMapping("/ajax/user")
    public ModelAndView ajaxUserHandler(@RequestParam String username) {
        String view = "ajax :: user";
        ModelAndView ret = new ModelAndView(view);
        ret.addObject("user", userService.findByUserName(username).get(0));
        return ret;          
    }

    @RequestMapping("/test/test1")
    public ModelAndView testCreateSearchWithServiceRequest(@RequestParam(defaultValue = "false", name="li") boolean li) {
        String testNum = "1";
        String testName = "testCreateSearchWithService";
        String testResult = "passed";
        String view = li?"tests :: resultTest":"test";
        ModelAndView ret = new ModelAndView(view);
        try {
            testCreateSearchWithService();
        } catch (Exception e) {
            testResult = "Error (" + e.getLocalizedMessage() + ")";
        }
        
        ret.addObject("testNum", testNum);
        ret.addObject("testName", testName);
        ret.addObject("testResult",testResult);
        return ret;          
    }

    
    private void testCreateSearchWithService() {
        System.out.println("testCreateSearchWithService");
        Search search = new Search("BVPH", "Paraula", "18/03/2018");
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        ArrayList<Resource> resourceList = new ArrayList<>();

        resourceList.add(new Resource("r00001", "Title 1", "page x", "25/02/1865", "a45671_title1", ((String) null), "21/01/2018", new String[]{"Bla bla bña frg1", "Ble, bli, blo, frg2"}));
        resourceList.add(new Resource("r00002", "Title 2", "page y", "16/06/1977", "a76032_title2", ((String) null), "21/01/2018", new String[]{"T2: Bla bla bña frg1", "T2: Ble, bli, blo, frg2"}));
        resourceList.add(new Resource("r00003", "Title 3", "page z", "03/10/1989", "a2385_title3", ((String) null), "21/01/2018", new String[]{"T3: Bla bla bña frg1", "T3: Ble, bli, blo, frg2"}));
        search.setResources(resourceList);

        instance.saveSearch(search);
        System.out.println(search.getId());
    }
    
    @RequestMapping("/test/test2")
    public ModelAndView testGetSearchWithServiceRequest(@RequestParam(defaultValue = "false", name="li") boolean li) {
        String testNum = "2";
        String testName = "testGetSearchWithService";
        String testResult = "passed";
        String view = li?"tests :: resultTest":"test";
        ModelAndView ret = new ModelAndView(view);
        try {
            testGetSearchWithService();
        } catch (Exception e) {
            testResult = "Error (" + e.getLocalizedMessage() + ")";
        }
        
        ret.addObject("testNum", testNum);
        ret.addObject("testName", testName);
        ret.addObject("testResult",testResult);
        return ret;          
    }

    private void testGetSearchWithService() {
        Search returnSearch = null;
        boolean trobat=false;
        System.out.println("testGetSearchWithService");
        Search expected = new Search("BVPH", "Paraula", "18/03/2018");
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        List<Search> returnSearchList = instance.findAllSearch();
        for(int i =0; !trobat && i<returnSearchList.size(); i++){
            if(returnSearchList.get(i).getSearchCriteria().equalsIgnoreCase("paraula")){
                returnSearch = returnSearchList.get(i);
                trobat = true;
            }
        }
        if(!expected.equals(returnSearch)){
            throw new RuntimeException("Error! No s'ha obtingt l'objecte adequat");
        }
        System.out.println(returnSearch.getId());
    }
    
    @RequestMapping("/test/test3")
    public ModelAndView testUpdateDateOfSearchWithServiceRequest(@RequestParam(defaultValue = "false", name="li") boolean li) {
        String testNum = "3";
        String testName = "testUpdateDateOfSearchWithService";
        String testResult = "passed";
        String view = li?"tests :: resultTest":"test";
        ModelAndView ret = new ModelAndView(view);
        try {
            testUpdateDateOfSearchWithService();
        } catch (Exception e) {
            testResult = "Error (" + e.getLocalizedMessage() + ")";
        }
        
        ret.addObject("testNum", testNum);
        ret.addObject("testName", testName);
        ret.addObject("testResult",testResult);
        return ret;          
    }

    private void testUpdateDateOfSearchWithService(){
        System.out.println("updateSearch");
        Search expected = new Search("BVPH", "Paraula", "12/05/02018");
        Search search;
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        ArrayList<Resource> resourceList = new ArrayList<>();
        
        resourceList.add(new Resource("r00001", "Title 1", "page x", "25/02/1865", "a45671_title1", ((String) null), "21/01/2018", new String[]{"Bla bla bña frg1", "Ble, bli, blo, frg2"}));
        resourceList.add(new Resource("r00002", "Title 2", "page y", "16/06/1977", "a76032_title2", ((String) null), "21/01/2018", new String[]{"T2: Bla bla bña frg1", "T2: Ble, bli, blo, frg2"}));
        resourceList.add(new Resource("r00004", "Title 4", "page k", "07/11/1979", "a2788_title4", ((String) null), "27/06/2018", new String[]{"T4: Bla bla bña frg4", "T4: Ble, bli, blo, frg5"}));
        expected.setResources(resourceList);

        expected = instance.saveSearch(expected);
        search = instance.findAllSearch().get(0);
        if(!expected.equals(search)){
            throw new RuntimeException("Error! No s'ha obtingt l'objecte adequat");
        }
        if(search.getResourceList().size()!=4){
            throw new RuntimeException("Error! No s'ha obtingt l'objecte adequat");
        }

        
        
    }
    
    @RequestMapping("/test/test4")
    public ModelAndView testGetResourcesFromSearchRequest(@RequestParam(defaultValue = "false", name="li") boolean li) {
        String testNum = "4";
        String testName = "testGetResourcesFromSearch";
        String testResult = "passed";
        String view = li?"tests :: resultTest":"test";
        ModelAndView ret = new ModelAndView(view);
        try {
            testGetResourcesFromSearch();
        } catch (Exception e) {
            testResult = "Error (" + e.getLocalizedMessage() + ")";
        }
        
        ret.addObject("testNum", testNum);
        ret.addObject("testName", testName);
        ret.addObject("testResult",testResult);
        return ret;          
    }

    private void testGetResourcesFromSearch(){
        System.out.println("testGetResouvaporrcesFromSearch");
        Search expected = new Search("BVPH", "Paraula", "12/05/02018");
        Search search;
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        search = instance.findAllSearch().get(0);
        List<Resource>  resources = instance.findAllResourceBySerach(search);
        if(resources.size()!=4){
            throw new RuntimeException("Error! NO s'ha obtingut la llista de recursos associats a una cerca");
        }
        
//        assertEquals(3, resources.size());                
        
        for(Resource r: resources){
            System.out.println(r);
        }
    }

    @RequestMapping("/test/test5")
    public ModelAndView testGetResourcesFromSearchPagRequest(@RequestParam(defaultValue = "false", name="li") boolean li) {
        String view = li?"tests :: resultTest":"test";
        ModelAndView ret = new ModelAndView(view);
        String testNum = "5";
        String testName = "testGetResourcesFromSearchPagRequest";
        String testResult = "passed";
        try {
            testGetResourcesFromSearchPag();
        } catch (Exception e) {
            testResult = "Error (" + e.getLocalizedMessage() + ")";
        }
        
        ret.addObject("testNum", testNum);
        ret.addObject("testName", testName);
        ret.addObject("testResult",testResult);
        return ret;          
    }

    private void testGetResourcesFromSearchPag(){
        System.out.println("testGetResourcesFromSearchPag");
        Search expected = new Search("BVPH", "Paraula", "12/05/02018");
        Search search;
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        search = instance.findAllSearch().get(0);
        Page<Resource>  resources = instance.findAllResourceBySerach(search, 0, 2);
        if(resources.getContent().size()!=2){
            throw new RuntimeException("Error! NO s'ha obtingut la llista de recursos associats a una cerca");
        }
        
//        assertEquals(3, resources.size());                
        
        for(Resource r: resources.getContent()){
            System.out.println(r);
        }

        resources = instance.findAllResourceBySerach(search, 1, 2);
        if(resources.getContent().size()!=2){
            throw new RuntimeException("Error! NO s'ha obtingut la llista de recursos associats a una cerca");
        }
        
//        assertEquals(3, resources.size());                
        
        for(Resource r: resources.getContent()){
            System.out.println(r);
        }
    }
    
    @RequestMapping("/test/tests")
    public ModelAndView tests(@RequestParam(defaultValue = "1", name = "fromTest") String fromTest, @RequestParam(defaultValue = "6", name = "toTest") String toTest) {
        String view = "tests";
        ModelAndView ret = new ModelAndView(view);
        ret.addObject("fromTest", fromTest);
        ret.addObject("toTest", toTest);
        return ret;          
    }

    @RequestMapping({"/test/test6", "/test/iterator/bvph"})
    public ModelAndView testIteratorBvPh(@RequestParam(defaultValue = "false", name="li") boolean li, @RequestParam(defaultValue = "", name = "criteria") String criteria, @RequestParam(defaultValue = "3", name = "quant") int quantity) {
        Random random = new Random(System.currentTimeMillis());
        String[] defaultCriteria = {"marinero", "velero", "estibador", "puerto", "mercante", "vapor"};
        String view = li?"tests :: resultTest":"test";
        ModelAndView ret = new ModelAndView(view);
        String testNum = "6";
        String testName = "testIteratorBvPh";
        String testResult = "passed";
        if(criteria.isEmpty()){
            criteria = defaultCriteria[random.nextInt(defaultCriteria.length)];
        }
        try{
            iterate(criteria, quantity);
        }catch(Exception e){
            testResult = "Error (" + e.getLocalizedMessage() + ")";
        }

        ret.addObject("testNum", testNum);
        ret.addObject("testName", testName);
        ret.addObject("testResult",testResult);
        return ret;          
    }
    
    private void iterate(String criteria, int quantity){
        String fileRepositoryPath =this.dp.getLocalReasourceRepo();
        String repository = "BVPH";
        BvphSearchIterator iterator = (BvphSearchIterator) ConfigParserOfSearcher.getIterator(repository, new BvphSearchCriteria(criteria));
        int c=0;
        Search search = new Search(repository, criteria, String.format("%1$td/%1$tm/%1$tY", Calendar.getInstance()));
        PersistenceService pService = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        
        while(c<quantity && iterator.hasNext()){
            c++;
            SearchResource res = iterator.next();
            Resource resource = new Resource(res);
            search.addResource(resource);
            String[] formats = resource.getSupportedFormats();
            
            for(String format: formats){
                FileOutputStream fileOutputStream = null;
                File path = new File(fileRepositoryPath);
                File file = new File(fileRepositoryPath, res.getFileName().concat(".").concat(format));
                FormatedFile ff = res.getFormatedFile(format);
                if(!path.exists()){
                    path.mkdirs();
                }
                if(!file.exists()){
                    try {
                        fileOutputStream = new FileOutputStream(file);
                    } catch (FileNotFoundException ex) {}
                    copyToFile(ff.getImInputStream(), fileOutputStream);
                }
            }
        }
        pService.saveSearch(search);
    }
        
    private void copyToFile(InputStream in, FileOutputStream out){
        try {
            byte[] buffer = new byte[1024];
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
        } catch (IOException ex) {
            throw  new ErrorCopyingFileFormaException(ex);
        }finally{
            try {out.close();} catch (IOException ex) {}
        }
    }

    @Autowired
    public void setDp(DownloaderProperties dp) {
        this.dp = dp;
    }

    @Autowired
    public void setPp(Prova2Properties pp) {
        this.pp = pp;
    }
    
    @Autowired
    public void setUserService(ProvaUserService userService) {
        this.userService = userService;
    }

    // TESTS Xavi
    
    // Inicialització de les dades
    Search[] resultats = {
            new Search(0, "Arca", "Marinero", "22/02/2015", "12/08/2017"),
            new Search(1, "Arca", "Camaleón", "01/04/2018", "02/04/2018"),
            new Search(2, "BVPH", "Trucha", "11/08/2014", "03/12/2015"),
        };
    
    public static final int numResources = 30;
    
    public static TestResource[] resources = InitTestResources();

    public static TestResource[] InitTestResources() {
        TestResource[] resources = new TestResource[numResources];
        
        
        String[] paraules={"Fluir", "Javelina", "Panteixar", "Llenya", "Duplicat", 
            "Turó", "Consulta", "Soterrani", "Gaudir", "Prémer"};
        String[] tipus = {"Revista", "Fascicle", "Publicació", "Diari"};
        
        String[] processos = {"", "Atlas-ti", "Selecció AI"};
        
        
        
        for (int i = 0; i<numResources; i++) {
            long searchId = ThreadLocalRandom.current().nextInt(0, 4);
            String title = tipus[ThreadLocalRandom.current().nextInt(0, 4)] + " "
                    + paraules[ThreadLocalRandom.current().nextInt(0, 10)] + 
                    " " + (ThreadLocalRandom.current().nextInt(1, 100));
            
            String page = ""+ThreadLocalRandom.current().nextInt(1, 1000);
            
            String  process = processos[ThreadLocalRandom.current().nextInt(0, 3)];
            
            int dia = ThreadLocalRandom.current().nextInt(5, 29);
            int mes = ThreadLocalRandom.current().nextInt(3, 13);
            int any = 2000 + ThreadLocalRandom.current().nextInt(0, 19);
                            
            String searchDate = dia + "/"+mes + "/" +any;            
            String editionDate = (dia-4) + "/"+(mes-2) + "/" +(any-3);
            
            String[] empty = {};
            
            resources[i] = new TestResource (i, searchId, title, page, process, editionDate, searchDate, empty);
        }
        
        
        return resources;
    }
    
    @GetMapping("/new")
    public ModelAndView newHandler() {
        
        
        String view = "new";
        ModelAndView ret = new ModelAndView(view);
        
        // TODO[Xavi]: Això s'ha d'obtenir del fitxer application.properties
        ret.addObject("title","Biblioteques SDL");
        
        ret.addObject("resultats",resultats);
        ret.addObject("resources",resources);
                
        return ret;
    }
}
