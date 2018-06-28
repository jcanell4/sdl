package org.elsquatrecaps.jig.sdl.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class SearchResourceId implements Serializable{
    private String repository;
    private String searchCriteria;
    private String resourceId;

    public SearchResourceId() {
    }

    public SearchResourceId(SearchId serachId, String resourceId) {
        this.repository = serachId.getRepository();
        this.searchCriteria = serachId.getSearchCriteria();
        this.resourceId = resourceId;
    }

    public SearchResourceId(String repository, String searchCriteria, String resourceId) {
        this.repository = repository;
        this.searchCriteria = searchCriteria;
        this.resourceId = resourceId;
    }

    public SearchId getSerachId() {
        return new SearchId(repository, searchCriteria);
    }

    public void setSerachId(SearchId searchId) {
        this.repository = searchId.getRepository();
        this.searchCriteria = searchId.getSearchCriteria();
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String ResourceId) {
        this.resourceId = ResourceId;
    }
    
    public boolean equals(Object obj){
        boolean ret = false;
        if(obj !=null
                && obj instanceof SearchResourceId){
            SearchResourceId sid = (SearchResourceId) obj;
            ret = this.searchCriteria == sid.searchCriteria 
                    && this.repository.equals(sid.repository)
                    && this.resourceId.equals(sid.resourceId);
        }
        return ret;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.repository, this.searchCriteria, this.resourceId);
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(repository);
        sb.append(",");
        sb.append(searchCriteria);
        sb.append(",");
        sb.append(resourceId);
        return sb.toString();
    }
}
