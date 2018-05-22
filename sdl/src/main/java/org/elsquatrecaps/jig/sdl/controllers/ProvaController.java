/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.controllers;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.elsquatrecaps.jig.sdl.model.ProvaDada;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.TestResource;
import org.elsquatrecaps.jig.sdl.searcher.Resource;
import org.elsquatrecaps.jig.sdl.services.ProvaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author josep
 */
@Controller
public class ProvaController { 
    private Prova1Properties dp;
    private Prova2Properties pp;
    private ProvaUserService userService;
    
    
    
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


    @Autowired
    public void setDp(Prova1Properties dp) {
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
    
    public static Resource[] resources = InitTestResources();

    public static Resource[] InitTestResources() {
        Resource[] resources = new TestResource[numResources];
        
        
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
            
            resources[i] = new TestResource(i, searchId, title, page, process, editionDate, searchDate, empty, empty);
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
