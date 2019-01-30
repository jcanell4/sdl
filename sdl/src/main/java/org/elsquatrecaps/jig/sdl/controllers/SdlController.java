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
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.logging.FileHandler;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.ErrorCopyingFileFormaException;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteData;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
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
import org.elsquatrecaps.jig.sdl.searcher.ArcaSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.HdSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.SearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.SearchIterator;
import org.jsoup.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Controller
public class SdlController {
    private static final Logger logger = LoggerFactory.getLogger(SdlController.class);
    private static final java.util.logging.Logger errorList = java.util.logging.Logger.getLogger(ErrorGettingRemoteResource.class.getName());
    static {
        try { 
            errorList.addHandler(new FileHandler("log/DocumentsNoBaixats.txt"));
        } catch (SecurityException | IOException ex) {
            logger.error("Error creant el fitxer de registres dels documents no obtinguts a casoa de: ".concat(ex.getMessage()), ex);
        }
    }

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

    private List<SearchAndCount> getAllSearches(PersistenceService instance) {
        List<SearchAndCount> searchesWithCounter = instance.findAllSearchWithResourceCounter();        
        return searchesWithCounter;
    }
    
    private List<SearchAndCount> getAllSearches() {
        PersistenceService instance = new PersistenceService(resourceRepository, searchResourceRepository, searchRepository, transactionManager);
        return getAllSearches(instance);
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
            if(!iterate(criteria, repository, dateStart, dateEnd)){
                // TODO[Xavi] Enviar un dialeg amb un missatge d'error indicant que no s'ha pogut completar la baixada i que es miri el registre de logs
                logger.debug("S'interromp la cerca degut a una excepció que no permet continuar.");
            }
        } else {
            logger.info("S'interromp la cerca degut a que es fa servir un criteri de cerca buit.");
            // TODO[Xavi] Enviar un dialeg amb un missatge d'error?
        }
        
        logger.debug("S'intenta obtenir tots els registres emmagatzemats");
        PersistenceService instance = new PersistenceService(resourceRepository, searchResourceRepository,  searchRepository, transactionManager);
        ret.addObject("searches", getAllSearches(instance));
        logger.debug("S'han obtingut tots els registres de la cerca");
        
        Optional<Search> optional = instance.findOne(repository, criteria);
        
        if (optional.isPresent()) {
            ret.addObject("selected", optional.get().getId());
        }
        
        return ret;
    }
    
    private SearchCriteria buildSearchCriteria(String criteria, String repository, String dateStart, String dateEnd){
        SearchCriteria ret = null;
        if(repository.equalsIgnoreCase("BVPH")){
            ret = new BvphSearchCriteria(criteria, dateStart, dateEnd);
        }else if(repository.equalsIgnoreCase("ARCA")){
            ret = new ArcaSearchCriteria(criteria, dateStart, dateEnd);
        }else if(repository.equalsIgnoreCase("HD")){
            ret = new HdSearchCriteria(criteria, dateStart, dateEnd);
        }
        return ret;
    }
    
    private boolean iterate(String criteria, String repository, String dateStart, String dateEnd){
        SearcherResource res = null;
        boolean ret = true;
        String fileRepositoryPath = this.dp.getLocalReasourceRepo();
        int quantity = this.dp.getQuantity();
        
        logger.info(String.format("S'inicia la cerca de %s des de %s fins a %s a la biblioteca %s", criteria, dateStart, dateEnd, repository));
//        System.out.println("Cercant: " + criteria);
        SearchIterator<SearcherResource> iterator = ConfigParserOfSearcher.getIterator(repository, buildSearchCriteria(criteria, repository, dateStart, dateEnd));
        
//        System.out.println("Iterador obtingut");
        logger.debug("Iterador obtingut");
        
        int c=0;
        Search search = new Search(repository, criteria, String.format("%1$td/%1$tm/%1$tY", Calendar.getInstance()));
        PersistenceService pService = new PersistenceService(resourceRepository, searchResourceRepository, searchRepository, transactionManager);
        
        pService.saveSearch(search);
        logger.debug("Registre principal de la cerca emmagatzemat");
        
        try{
            while((quantity<=0 || c<quantity) && iterator.hasNext()){
                SearchResource searchResource;
                c++;
                try{
                    res = iterator.next();
                    logger.info(String.format("%d.- Recurs obtingut: %s (%s)", c, res.getTitle(), res.getEditionDate()));
                    Resource resource = new Resource(res);
                    searchResource = search.addResource(resource);
                    String[] formats = resource.getSupportedFormats();

                    for(String format: formats){
                        boolean error=false;
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
                                try{
                                    Utils.copyToFile(ff.getImInputStream(), fileOutputStream);
                                } catch (ErrorGettingRemoteResource | ErrorCopyingFileFormaException ex) {
                                    logger.error(ex.getMessage(), ex);
                                    error = true;
                                }
                            } catch (FileNotFoundException ex) {
                                logger.error(ex.getMessage(), ex);
                                error = true;
                            }
                            if(error){
                                logger.info(String.format("Fitxer %s NO copiat", ff.getFileName()));
                            }else{
                                logger.info(String.format("Fitxer %s copiat.", ff.getFileName()));
                            }
                        }else{
                            logger.info(String.format("Fitxer %s copiat anteriorment.", ff.getFileName()));
                        }
                    }
                    pService.saveSearchResource(searchResource);
                    logger.debug("Registre del recurs emmagatzemat");
                }catch(ErrorGettingRemoteResource ex){
                    if(res!=null){
                        errorList.info(res.toString());
                    }else{
                        errorList.severe(ex.getMessage());
                    }
                }
            }
            logger.debug("Cerca acabada");
        }catch(ErrorGettingRemoteData ex){
            ret = false;
            if(ex.getCause() instanceof IOException
                    || ex.getCause() instanceof UncheckedIOException ){
                logger.error("Obtenció de recursos interrumpuda per TIMEOUT en el servidor: ".concat(ex.getMessage()));
            }else{
                logger.error("Obtenció de recursos interrumpuda per: ". concat(ex.getMessage()), ex);
            }
        }
        return ret;
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
