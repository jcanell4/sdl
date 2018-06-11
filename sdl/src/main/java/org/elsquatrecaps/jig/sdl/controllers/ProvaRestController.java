package org.elsquatrecaps.jig.sdl.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.elsquatrecaps.jig.sdl.model.ProvaAjaxResponseBody;
import org.elsquatrecaps.jig.sdl.model.ProvaDada;
import org.elsquatrecaps.jig.sdl.model.ProvaSearchCriteria;
import org.elsquatrecaps.jig.sdl.model.ProvaUser;
import org.elsquatrecaps.jig.sdl.model.TestResource;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.SearchResource;
import org.elsquatrecaps.jig.sdl.searcher.SearchIterator;
import org.elsquatrecaps.jig.sdl.searcher.cfg.ConfigParserOfSearcher;
import org.elsquatrecaps.jig.sdl.services.ProvaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProvaRestController {
    ProvaUserService userService;

    @Autowired
    public void setUserService(ProvaUserService userService) {
        this.userService = userService;
    }
    @RequestMapping("/provaSearcher")
    public Boolean getProvaSearcher() {
        BvphSearchCriteria criteria = new BvphSearchCriteria("soldador", 1970, 1970);
        SearchIterator it = ConfigParserOfSearcher.getIterator("bvph", criteria);
        SearchResource res = null;
        
        for(int i=0; i<3 && it.hasNext(); i++){
            res = it.next();
        }
        return true;
    }

    @PostMapping("/api/search")
    public ResponseEntity<?> getSearchResultViaAjax(@Valid @RequestBody ProvaSearchCriteria search, Errors errors) {

        ProvaAjaxResponseBody result = new ProvaAjaxResponseBody();

        //If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {

            result.setMsg(errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);

        }

        List<ProvaUser> users = userService.findByUserName(search.getUsername());
        if (users.isEmpty()) {
            result.setMsg("no user found!");
        } else {
            result.setMsg("success");
        }
        result.setResult(users);

        return ResponseEntity.ok(result);

    }
    
    @RequestMapping(value = "/pojo")
    public ArrayList<ProvaDada> getPojoData(@RequestParam String x, @RequestParam int length){
        ArrayList<ProvaDada> ret = new ArrayList<ProvaDada>();
        for(int i=0; i<length; i++){
            ret.add(new ProvaDada(x+i, i, ""+i+x));
        }
        return ret;
    }
    
    
    
    @RequestMapping(value = "/api/resource")
    public ArrayList<TestResource> getResourceViaAjax(@RequestParam int id){
        // Resultats de prova, no provenen de la base de dades, es generan aleatoriament en arrancar l'aplicació
        
        System.out.println(ProvaController.resources.length);
        
        ArrayList<TestResource> result = new ArrayList<>();
        result.add(ProvaController.resources[id]);
        
        return result;
        
    }
    
    
}
