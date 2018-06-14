/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.UnsupportedFormat;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.SearchAndCount;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
import org.elsquatrecaps.jig.sdl.persistence.SearchRepository;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchIterator;
import org.elsquatrecaps.jig.sdl.searcher.SearchResource;
import org.elsquatrecaps.jig.sdl.searcher.cfg.ConfigParserOfSearcher;
import org.elsquatrecaps.jig.sdl.services.ExportService;
import org.elsquatrecaps.jig.sdl.services.PersistenceService;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SdlController {

    private DownloaderProperties dp;

    @Autowired(required = true)
    ResourceRepository resourceRepository;
    @Autowired(required = true)
    SearchRepository searchRepository;
    @Autowired(required = true)
    PlatformTransactionManager transactionManager;

    @Autowired
    public void setDp(DownloaderProperties dp) {
        this.dp = dp;
    }

    // TESTS Xavi
    @GetMapping("/get")
    public ModelAndView newHandler() { // TODO: Canviar el nom a index o alguna cosa així

        

        String view = "new";
        ModelAndView ret = new ModelAndView(view);

        // TODO[Xavi]: Això s'ha d'obtenir del fitxer application.properties
        ret.addObject("title", "Biblioteques SDL");

        //List<Search> searches = instance.findAllSearch();
        //ret.addObject("searches", searches);
                
        ret.addObject("searches", getAllSearches());
        


        return ret;
    }

    private List<SearchAndCount> getAllSearches() {
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        List<SearchAndCount> searchesWithCounter = instance.findAllSearchWithResourceCounter();        
        
        for (SearchAndCount searchWithCounter : searchesWithCounter) {
            Hibernate.initialize(searchWithCounter);
        }
        
        return searchesWithCounter;
    }
    
    
    @RequestMapping(value = "/searchDetail/{id}")
    public ModelAndView searchHandler(@PathVariable("id") int id) { 
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);

        String view = "new :: resourcesBySearchDialog";
        ModelAndView ret = new ModelAndView(view);

        Search search = instance.findSearchById(id);
        ret.addObject("search", search);

        List<Resource> resources = instance.findAllResourceBySerach(search.getId());
        ret.addObject("resources", resources);
        ret.addObject("resourcesCount", resources.size());

        for (Resource resource : resources) {
            System.out.println(resource.getId());
        }

        return ret;
    }

    @RequestMapping(value = "/resourceDetail/{id}")
    public ModelAndView resoruceDetailHandler(@PathVariable("id") String id) { // TODO: Canviar el nom per un més adient
        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);

        String view = "new :: resourceDetail";
        ModelAndView ret = new ModelAndView(view);

        Resource resource = instance.findResourceById(id);
        ret.addObject("resource", resource);

        return ret;
    }

    
    
    
    
    
    @RequestMapping({"/search"})
    //public ModelAndView testIteratorBvPh(@RequestParam(defaultValue = "false", name="li") boolean li, @RequestParam(defaultValue = "", name = "criteria") String criteria, @RequestParam(defaultValue = "3", name = "quant") int quantity) {
    public ModelAndView searchHandler(
            @RequestParam(defaultValue = "", name = "criteria") String criteria,
            @RequestParam(defaultValue = "BVPH", name = "repository") String repository,
            //@RequestParam(defaultValue = "", name = "date-end") String dateEnd,
            //@RequestParam(defaultValue = "", name = "date-start") String dateStart,
            @RequestParam(defaultValue = "3", name = "quant") int quantity) {

        ModelAndView ret = new ModelAndView("new :: searches");
        
        if (criteria.length()>0) {
            iterate(criteria, quantity, repository);
        } else {
            // TODO[Xavi] Enviar un dialeg amb un missatge d'error
        }
        

        PersistenceService instance = new PersistenceService(resourceRepository, searchRepository, transactionManager);
        //List<Search> searches = instance.findAllSearch();        
        //ret.addObject("searches", searches);
        ret.addObject("searches", getAllSearches());
        
        
        Optional<Search> optional = searchRepository.findOne(repository, criteria);
        
        if (optional.isPresent()) {
            ret.addObject("selected", optional.get().getId());
        }
        

        return ret;
    }
    
    
    
    private void iterate(String criteria, int quantity, String repository) {
        String fileRepositoryPath = this.dp.getLocalReasourceRepo();
        
        System.out.println("Cercant: " + criteria);
        BvphSearchIterator iterator = (BvphSearchIterator) ConfigParserOfSearcher.getIterator(repository, new BvphSearchCriteria(criteria));
        
        System.out.println("Iterador obtingut");
        
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
                    Utils.copyToFile(ff.getImInputStream(), fileOutputStream);
                }
            }
        }
        pService.saveSearch(search);
    }
    
    @RequestMapping({"/export"})
    public ModelAndView searchHandler(
            @RequestParam(defaultValue = "", name = "ids[]") String[] ids,
            @RequestParam(defaultValue = "", name = "formats") String formats,
            @RequestParam(defaultValue = "", name = "process") String process        
    ) {

        // TODO: Carregar un missatge de confirmiació o alguna altre cosa
        
        ModelAndView ret = new ModelAndView("new :: exportMessages");
        
        
        ExportService instance = new ExportService(resourceRepository, this.dp);
        
        String[] formatArray = formats.split(",");
        
        String errorMessage = null;
        
        for (String format : formatArray) {
            try {
                instance.exportResourcesById(ids, format);
                
            } catch (UnsupportedFormat e) {
                if (errorMessage == null) {
                    errorMessage = "Format no suportat: ".concat(format);
                } else {
                    errorMessage.concat(", ".concat(format));
                }
                
            }
            
        }
        
        if (errorMessage !=null) {
            ret.addObject("errorExportMessage", errorMessage);
        } else {
            ret.addObject("successExportMessage", "Recursos exportats amb éxit");
        }
        

        return ret;
    }
    
}
