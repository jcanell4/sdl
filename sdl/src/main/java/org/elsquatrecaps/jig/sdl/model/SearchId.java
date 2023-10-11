package org.elsquatrecaps.jig.sdl.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class SearchId implements Serializable{
    private String repository;
    private String searchCriteria;

    public SearchId() {
    }

    public SearchId(String repository, String searchCriteria) {
        this.repository = repository.toLowerCase();
        this.searchCriteria = searchCriteria.toLowerCase();
    }
    
    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository.toLowerCase();
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria.toLowerCase();
    }
    
    @Override
    public boolean equals(Object obj){
        boolean ret = false;
        if(obj!=null && obj instanceof SearchId){
            SearchId sid =(SearchId) obj;
            ret = this.searchCriteria.equals(sid.searchCriteria)
                    && this.repository.equals(sid.repository);
        }
        return ret;
    }
    
    @Override
    public int hashCode(){
        return Objects.hash(repository, searchCriteria);
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(repository);
        sb.append(",");
        sb.append(searchCriteria);
        return sb.toString();
    }
}
