/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import org.elsquatrecaps.jig.sdl.searcher.FormatedResourceUtils;
import org.elsquatrecaps.jig.sdl.searcher.SearchResource;

@Entity
@Access(AccessType.FIELD)
public class Resource implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String title;
    private String page;
    private String searchDate;
    private String editionDate;
    private String processingAnalysis;
    private String fileName;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="RESOURCE_FORMAT")
    @OrderColumn
    private List<String> supportedFormats=new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="RESOURCE_FRAGMENT")
    @OrderColumn
    private List<String> fragments= new ArrayList<String>();
//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private Search ownerSearch;

    public Resource() {        
    }

    public Resource(String id) {        
        this.id = id;
    }

    public Resource(String id, String title, String page, String editionDate, String fileName, String processing, String searchDate, String[] fragments) {        
        this.id = id;
        this.title = title;
        this.page = page;
        this.editionDate = editionDate;
        this.fileName = fileName;
        _addAllSupportedFormat(new String[] {"jpg", "xml", "text"});
        _addAllFragments(fragments);
    }

    public Resource(SearchResource resource) {
        id = resource.getPublicationId().concat("_").concat(resource.getPageId());
        title = resource.getTitle();
        page = resource.getPage();
        editionDate = resource.getEditionDate();
        fileName = resource.getFileName();
        _addAllSupportedFormat(resource.getSupportedFormats());
        _addAllFragments(resource.getFragments());
    }
    
    public String getId() {
        return id;
    }
    
    public String getTitle(){
        return title;
    }
    
    public String getPage() {    
        return page;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setPage(String page) {
        this.page = page;
    }

    protected void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }
    
    public String getSearchDate() {
        return this.searchDate;
    }

    protected void setEditionDate(String editionDate) {
        this.editionDate = editionDate;
    }
    
    public String getEditionDate() {
        return this.editionDate;
    }
    
    public void setProcessingAnalysis(String processingAnalysis) {
        this.processingAnalysis = processingAnalysis;
    }
    
    public String getProcessingAnalysis() {
        return processingAnalysis;
    }
    
    public String[] getFragments() {
        String[] ret = new String[fragments.size()];
        return fragments.toArray(ret);
    }

    public String getFragment(int idx) {
        return fragments.get(idx);
    }

    protected void addFragment(String fragment) {
        this.fragments.add(fragment);
    }

    protected void addAllFragments(String[] fragment) {
        _addAllFragments(fragment);
    }
    
    private void _addAllFragments(String[] fragment) {
        for(String frg: fragment){
            this.fragments.add(frg);
        }
    }
    
    public String[] getSupportedFormats() {
        String[] ret = new String[supportedFormats.size()];
        return supportedFormats.toArray(ret);
    }

    public String getSupportedFormat(int idx) {
        return supportedFormats.get(idx);
    }

    protected void addSupportedFormat(String format) {
        this.supportedFormats.add(format);
    }

    protected void addAllSupportedFormat(String[] formats) {
        _addAllSupportedFormat(formats);;
    }
    
    private void _addAllSupportedFormat(String[] formats) {
        for(String format: formats){
            this.supportedFormats.add(format);
        }
    }
    
    protected String[] getAlternativeFormats(String format) {
        return FormatedResourceUtils.getAlternativeFormats(format);
    }

    protected boolean isFormatSupported(String format) {
        return FormatedResourceUtils.isFormatSupported(format, getSupportedFormats());
    }

    @JsonIgnore
    public FormatedFile getFormatedFile(){
        return this.getFormatedFile(this.getSupportedFormat(0));
    }

    @JsonIgnore
    public FormatedFile getFormatedFile(String format) {
        String formatDef = FormatedResourceUtils.getFormat(format, getSupportedFormats());
        return getStrictFormatedFile(formatDef);
    }

    @JsonIgnore
    protected FormatedFile getStrictFormatedFile(String format){
        LocalFormatedFile ff = new LocalFormatedFile(this.getFileName());
        return ff;
    }


    public String toString(){
        StringBuilder strb =  new StringBuilder();
        strb.append("Resource with title: '");
        strb.append(this.title);
        strb.append("', page:  '");
        strb.append(this.page);
        strb.append("' and id:  ");
        strb.append(this.id);
        strb.append(". Searched on; ");
        strb.append(this.searchDate);
        strb.append(" )");
        return strb.toString();
    }
    
    public boolean equals(Object obj){
        boolean ret = false;
        if(obj!=null && obj instanceof Resource){
            Resource toCompare = (Resource) obj;
            ret = this.id.equals(toCompare.id);
        }
        return ret;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public String getFileName() {
        return fileName;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    protected void setSupportedFormats(List<String> supportedFormats) {
        this.supportedFormats = supportedFormats;
    }
}
