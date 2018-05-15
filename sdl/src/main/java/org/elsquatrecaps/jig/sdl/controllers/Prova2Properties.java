package org.elsquatrecaps.jig.sdl.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author josep
 */
@Component
@ConfigurationProperties(prefix = "prova")
public class Prova2Properties {
    private String prova1;
    private String prova2;
    private List<String> prova = new ArrayList<String>();

    public String getProva1() {
        return prova1;
    }

    public void setProva1(String prova1) {
        this.prova1 = prova1;
    }

    public String getProva2() {
        return prova2;
    }

    public void setProva2(String prova2) {
        this.prova2 = prova2;
    }

    public List<String> getProva() {
        return prova;
    }

    public void setProva(List<String> prova) {
        this.prova = prova;
    }    
}
