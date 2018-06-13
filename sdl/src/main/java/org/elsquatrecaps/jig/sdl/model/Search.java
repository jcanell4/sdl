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
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.TableGenerator;

@Entity
@Access(AccessType.FIELD)
public class Search implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "search_gen")
    @TableGenerator(name = "search_gen", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", allocationSize = 1)
    private long id;
    private String searchCriteria;
    private String originalDate;
    private String repository;
    private String updateDate;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "SEARCH_RESOURCE",
            joinColumns = @JoinColumn(name = "searchId"),
            inverseJoinColumns = @JoinColumn(name = "resourceId")
    )
    private Set<Resource> resources = new HashSet<Resource>();
    
    public Search(){
    }
    
    public Search(long id) {
        this.id = id;
    }
    
    public Search(String repository, String searchCriteria, String originalDate) {
        this.searchCriteria = searchCriteria;
        this.originalDate = originalDate;
        this.repository = repository;
    }
    
    public Search(String repository, String searchCriteria, String originalDate, String updateDate) {
        this.searchCriteria = searchCriteria;
        this.originalDate = originalDate;
        this.repository = repository;
        this.updateDate = updateDate;
    }
    
    public Search(long id, String repository, String searchCriteria, String originalDate, String updateDate) {
        this.id = id;
        this.searchCriteria = searchCriteria;
        this.originalDate = originalDate;
        this.repository = repository;
        this.updateDate = updateDate;
    }
    
    public String getSearchCriteria() {
        return searchCriteria;
    }
    
    public String getRepository() {
        return repository;
    }
    
    public long getId() {
        return id;
    }

    public String getOriginalDate() {
        return originalDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public List<Resource> getResourceList() {
        List<Resource> ret = new ArrayList<>(this.resources);
        return ret;
    }
    
    public Iterator<Resource> getResourceIterator() {
        return resources.iterator();
    }    

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public void setOriginalDate(String originalData) {
        this.originalDate = originalData;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public void setUpdateDate(String updateData) {
        this.updateDate = updateData;
    }

    public void addResource(Resource resource) {
        String date = (this.updateDate==null || this.updateDate.isEmpty())?this.originalDate:this.updateDate;
        resource.setSearchDate(date);
        this.resources.add(resource);
    }
    
    public void addAllResources(List<Resource> resources) {
        for(Resource resource : resources){
            addResource(resource);
//            if(!this.resources.contains(resource)){
//                String date = (this.updateDate==null || this.updateDate.isEmpty())?this.originalDate:this.updateDate;
//                resource.setSearchDate(date);
//                this.resources.add(resource);
//            }
        }
    }
    
    public void setResources(List<Resource> resources) {
        this.resources.clear();
        this.resources.addAll(resources);
    }
    
    public String toString(){
        StringBuilder strb =  new StringBuilder();
        strb.append("Search of '");
        strb.append(this.searchCriteria);
        strb.append("' in ");
        strb.append(this.repository);
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
            if(this.id==0 || search.id==0){
                ret = this.searchCriteria.equals(search.searchCriteria)
                        && this.repository.equals(search.repository);
            }else{
                ret = this.id == search.id;
            }
        }
        return ret;
    }

    @Override
    public int hashCode() {
        int hash;
        if(this.id==0){
            hash = this.searchCriteria.hashCode() + this.repository.hashCode();
        }else{
            hash = 7;
            hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        }
        return hash;
    }
}
