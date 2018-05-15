/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.controllers;

import java.util.List;
import org.elsquatrecaps.jig.sdl.controllers.Prova1Properties;
import org.elsquatrecaps.jig.sdl.model.ProvaDada;
import org.elsquatrecaps.jig.sdl.services.ProvaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    
}
