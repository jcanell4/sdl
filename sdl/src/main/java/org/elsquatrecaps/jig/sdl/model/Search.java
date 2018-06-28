/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
//import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

@Entity
@Access(AccessType.FIELD)
public class Search implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private SearchId id;
    private String originalDate;
    private String updateDate;
    
    //@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @OneToMany(mappedBy="search", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    /*@JoinTable(name = "SEARCH_RESOURCE",
            joinColumns = @JoinColumn(name = "searchId"),
            inverseJoinColumns = @JoinColumn(name = "resourceId")
    )*/
    private Set<SearchResource> resources = new HashSet<SearchResource>();
    
    public Search(){
    }
    
    public Search(SearchId id) {
        this.id = id;
    }
    
    public Search(String repository, String searchCriteria) {
        this.id = new SearchId(repository, searchCriteria);
    }
    
    public Search(String repository, String searchCriteria, String originalDate) {
        this.id = new SearchId(repository, searchCriteria);
        this.originalDate = originalDate;
    }
    
    public String getSearchCriteria() {
        return id.getSearchCriteria();
    }
    
    public String getRepository() {
        return id.getRepository();
    }
    
    public SearchId getId() {
        return id;
    }

    public String getOriginalDate() {
        return originalDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public List<SearchResource> getResourceList() {
        List<SearchResource> ret = new ArrayList<>(this.resources);
        return ret;
    }
    
    public Iterator<SearchResource> getResourceIterator() {
        return resources.iterator();
    }    

    public void setSearchCriteria(String searchCriteria) {
        this.id.setSearchCriteria(searchCriteria);
    }

    public void setOriginalDate(String originalData) {
        this.originalDate = originalData;
    }

    public void setRepository(String repository) {
        this.setRepository(repository);
    }

    public void setUpdateDate(String updateData) {
        this.updateDate = updateData;
    }

    public void addResource(SearchResource resource) {
        String date = (this.updateDate==null || this.updateDate.isEmpty())?this.originalDate:this.updateDate;
        resource.getResource().setSearchDate(date);
        resource.setSearch(this);
        if(!this.resources.contains(resource)){
            this.resources.add(resource);
        }        
    }
    
    public void addResource(Resource resource) {
        String date = (this.updateDate==null || this.updateDate.isEmpty())?this.originalDate:this.updateDate;
        resource.setSearchDate(date);
        this.resources.add(new SearchResource(this, resource));
    }
    
    public void addAllSearchResources(List<SearchResource> resources) {
        for(SearchResource resource : resources){
            this.resources.add(resource);
        }
    }
    
    public void addAllResources(List<Resource> resources) {
        for(Resource resource : resources){
            addResource(resource);
        }
    }
    
    protected void setResources(List<SearchResource> resources) {
        this.resources.clear();
        this.resources.addAll(resources);
    }
    
    public String toString(){
        StringBuilder strb =  new StringBuilder();
        strb.append("Search of '");
        strb.append(this.id.getSearchCriteria());
        strb.append("' in ");
        strb.append(this.id.getRepository());
        strb.append("(first search date: ");
        strb.append(this.originalDate);
        strb.append(" and last update date: ");
        strb.append(this.updateDate);
        strb.append(" )");
        return strb.toString();
    }
    
    public boolean equals(Object obj){
        boolean ret = false;
        if(obj!=null && obj instanceof Search){
            Search search = (Search) obj;
            ret = this.id.equals(search.id);
        }
        return ret;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
