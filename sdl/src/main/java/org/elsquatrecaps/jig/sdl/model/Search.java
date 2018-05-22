/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

public class Search {
    private long id;
    private String searchCriteria;
    private String originalData;
    private String repository;
    private String updateData;
    
    public Search(long id, String repository, String searchCriteria, String originalData, String updateData) {
        this.id = id;
        this.searchCriteria = searchCriteria;
        this.originalData = originalData;
        this.repository = repository;
        this.updateData = updateData;
    }
    
    public String getSearchCriteria() {
        return searchCriteria;
    }
    
    public String getOriginalDate() {
        return originalData;
    }
    
    public String getRepository() {
        return repository;
    }
    
    public String getUpdateDate() {
        return updateData;
    }
    
}
