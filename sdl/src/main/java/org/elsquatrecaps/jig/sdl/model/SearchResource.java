package org.elsquatrecaps.jig.sdl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class SearchResource implements Serializable {
    @EmbeddedId
    private SearchResourceId id;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="REPOSITORY", insertable = false, updatable = false),
        @JoinColumn(name="SEARCHCRITERIA", insertable = false, updatable = false)
    })
    @JsonIgnore
    private Search search;
    
    @ManyToOne
    @JoinColumn(name="RESOURCEID", insertable = false, updatable = false)
    //@MapsId("resourceId")
    private Resource resource;

    private String processingAnalysis;

    public SearchResource() {
    }
    
    public SearchResource(Search search, Resource resource){
        this.search=search;
        this.resource=resource;
        this.id = new SearchResourceId(search.getId(), resource.getId());
    }
    
    public void addProcessingAnalysis(String processingAnalysis) {
        boolean add=true;
        String[] processes = new String[0];
        if(this.processingAnalysis!=null && !this.processingAnalysis.isEmpty()){
            processes = this.processingAnalysis.split(",");
            for(int i=0; add && i<processes.length; i++){
                add = !(processes[i].trim().equalsIgnoreCase(processingAnalysis));
            }
            if(add){
                this.processingAnalysis = this.processingAnalysis.concat(", ").concat(processingAnalysis);
            }                
        }else{
            this.processingAnalysis=processingAnalysis;
        }        
    }
    
    public void setProcessingAnalysis(String processingAnalysis) {
        this.processingAnalysis = processingAnalysis;
    }
    
    public String getProcessingAnalysis() {
        return processingAnalysis;
    }
    
    public Search getSearch(){
        return search;
    }
    
    public Resource getResource(){
        return resource;
    }
    
    
    public String getSearchCriteria() {
        return search.getSearchCriteria();
    }
    
    public String getRepository() {
        return search.getRepository();
    }
    
    public String getResourceId() {
        return resource.getId();
    }
    
    public String getTitle(){
        return resource.getTitle();
    }
    
    public String getPage() {    
        return resource.getPage();
    }

    public String getSearchDate() {
        return resource.getSearchDate();
    }

    public String getEditionDate() {
        return resource.getEditionDate();
    }
    
    public String getCalcDate() {
        return resource.getCalcDate().getDesc();
    }
    
    public String[] getFragments() {
        return resource.getFragments();                
    }

    public String getFragment(int idx) {
        return resource.getFragment(idx);                
    }

    public String getSupportedFormatsAsSingleString() {
        return resource.getSupportedFormatsAsSingleString();                
    }
    
    public String[] getSupportedFormats() {
        return resource.getSupportedFormats();                
    }

    public String getSupportedFormat(int idx) {
        return resource.getSupportedFormat(idx);                
    }


    protected String[] getAlternativeFormats(String format) {
        return resource.getAlternativeFormats(format);
    }

    protected boolean isFormatSupported(String format) {
        return resource.isFormatSupported(format);
    }

    void setSearch(Search s) {
        this.search = s;
        this.getId().setSerachId(s.getId());
    }

    public SearchResourceId getId() {
        return id;
    }

    public void setId(SearchResourceId id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
    
    public boolean equals(Object obj){
        boolean ret = false;
        if(obj instanceof SearchResource){
            SearchResource sr = (SearchResource) obj;
            ret = this.getId().equals(sr.getId());
        }
        return ret;
    }
}
