/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.UnsupportedFormat;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.SearchAndCount;
import org.elsquatrecaps.jig.sdl.model.SearchId;
import org.elsquatrecaps.jig.sdl.model.SearchResource;
import org.elsquatrecaps.jig.sdl.model.SearchResourceId;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
import org.elsquatrecaps.jig.sdl.persistence.SearchRepository;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchIterator;
import org.elsquatrecaps.jig.sdl.searcher.SearcherResource;
import org.elsquatrecaps.jig.sdl.searcher.cfg.ConfigParserOfSearcher;
import org.elsquatrecaps.jig.sdl.services.ExportService;
import org.elsquatrecaps.jig.sdl.services.PersistenceService;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.elsquatrecaps.jig.sdl.persistence.SearchResourceRepository;
import org.springframework.http.HttpStatus;

@Controller
public class SdlController {

    private DownloaderProperties dp;

    @Autowired(required = true)
    ResourceRepository resourceRepository;
    @Autowired(required = true)
    SearchResourceRepository searchResourceRepository;
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
    public ModelAndView newHandler() {

        

        String view = "new";
        ModelAndView ret = new ModelAndView(view);

        ret.addObject("title", "Biblioteques SDL");
                
        ret.addObject("searches", getAllSearches());
        


        return ret;
    }

    private List<SearchAndCount> getAllSearches() {
        PersistenceService instance = new PersistenceService(resourceRepository, searchResourceRepository, searchRepository, transactionManager);
        List<SearchAndCount> searchesWithCounter = instance.findAllSearchWithResourceCounter();        
        

        return searchesWithCounter;
    }
    
    
    @RequestMapping(value = "/searchDetail/{id}")
    public ModelAndView searchHandler(@PathVariable("id") String id) { 
        PersistenceService instance = new PersistenceService(resourceRepository, searchResourceRepository, searchRepository, transactionManager);
        String[] aId = id.split(",");
        String view = "new :: resourcesBySearchDialog";
        ModelAndView ret = new ModelAndView(view);

        Search search = instance.findSearchById(new SearchId(aId[0], aId[1]));
        ret.addObject("search", search);

        List<SearchResource> resources = instance.findAllResourceBySearch(search.getId());
        ret.addObject("resources", resources);
        ret.addObject("resourcesCount", resources.size());

        for (SearchResource resource : resources) {
            System.out.print(resource.getSearchCriteria());
            System.out.print("->");
            System.out.println(resource.getResourceId());
        }

        return ret;
    }

    @RequestMapping(value = "/resourceDetail/{id}")
    public ModelAndView resoruceDetailHandler(@PathVariable("id") String id) { 
        PersistenceService instance = new PersistenceService(resourceRepository, searchResourceRepository, searchRepository, transactionManager);
        String[] aId = id.split(",");

        String view = "new :: resourceDetail";
        ModelAndView ret = new ModelAndView(view);

        SearchResource resource = instance.findResourceById(new SearchResourceId(aId[0], aId[1], aId[2]));
        ret.addObject("resource", resource);

        return ret;
    }

    
    
    
    
    
    @RequestMapping({"/search"})
    public ModelAndView searchHandler(
            @RequestParam(defaultValue = "", name = "criteria") String criteria,
            @RequestParam(defaultValue = "BVPH", name = "repository") String repository,
            @RequestParam(defaultValue = "", name = "date-end") String dateEnd,
            @RequestParam(defaultValue = "", name = "date-start") String dateStart){
            //@RequestParam(defaultValue = "3", name = "quant") int quantity) {

        ModelAndView ret = new ModelAndView("new :: searches");
        
        if(dateStart.matches("[0-9]{4}[\\/\\-][0-9]{2}[\\/\\-][0-9]{2}")){
            String[] aDate = dateStart.split("[\\/\\-]");
            dateStart = aDate[2].concat("/").concat(aDate[1]).concat("/").concat(aDate[0]);
        }
        
        if(dateEnd.matches("[0-9]{4}[\\/\\-][0-9]{2}[\\/\\-][0-9]{2}")){
            String[] aDate = dateEnd.split("[\\/\\-]");
            dateEnd = aDate[2].concat("/").concat(aDate[1]).concat("/").concat(aDate[0]);
        }
        
        if (criteria.length()>0) {
            iterate(criteria, repository, dateStart, dateEnd);
        } else {
            // TODO[Xavi] Enviar un dialeg amb un missatge d'error?
        }
        

        PersistenceService instance = new PersistenceService(resourceRepository, searchResourceRepository,  searchRepository, transactionManager);
        ret.addObject("searches", getAllSearches());
        
        Optional<Search> optional = instance.findOne(repository, criteria);
        
        if (optional.isPresent()) {
            ret.addObject("selected", optional.get().getId());
        }
        
        return ret;
    }
    
    
    
    private void iterate(String criteria, String repository, String dateStart, String dateEnd){
        String fileRepositoryPath = this.dp.getLocalReasourceRepo();
        int quantity = this.dp.getQuantity();
        
        System.out.println("Cercant: " + criteria);
        BvphSearchIterator iterator = (BvphSearchIterator) ConfigParserOfSearcher.getIterator(repository, new BvphSearchCriteria(criteria, dateStart, dateEnd));
        
        System.out.println("Iterador obtingut");
        
        int c=0;
        Search search = new Search(repository, criteria, String.format("%1$td/%1$tm/%1$tY", Calendar.getInstance()));
        PersistenceService pService = new PersistenceService(resourceRepository, searchResourceRepository, searchRepository, transactionManager);
        
        while((quantity<=0 || c<quantity) && iterator.hasNext()){
            c++;
            SearcherResource res = iterator.next();
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
        
        
        ExportService instance = new ExportService(searchResourceRepository, this.dp);
        
        String[] formatArray = formats.split(",");
        
        String errorMessage = null;
        
        for (String format : formatArray) {
            try {
                instance.exportResourcesById(ids, format, process);
                
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
            ret.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ret.addObject("successExportMessage", "Recursos exportats amb éxit");
        }
        return ret;
    }
    
}
