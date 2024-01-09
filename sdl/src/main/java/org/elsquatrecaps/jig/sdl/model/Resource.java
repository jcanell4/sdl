/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.Serializable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import org.elsquatrecaps.jig.sdl.searcher.FormatedResourceUtils;
import org.elsquatrecaps.jig.sdl.searcher.SearcherResource;
import org.elsquatrecaps.jig.sdl.util.Utils;

@Entity
@Access(AccessType.FIELD)
public class Resource implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String title;
    private String page;
    private String editionDate;
    @OneToOne
    private CalcDateMap calcDate;
    private String docId;
    private String pageId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="RESOURCE_FORMAT")
    @OrderColumn
    private List<ResourceFormat> resourceFormats=new ArrayList<>();
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name="RESOURCE_FRAGMENT")
//    @Column(length = 500)
//    @OrderColumn
//    private List<String> fragments= new ArrayList<String>();
    @Transient
    private String localFilePath="";
    @OneToOne
    @JoinColumn(name="PREVIOUS_PAGE")
    private Resource nextPage;
    @OneToOne
    @JoinColumn(name="NEXT_PAGE")
    private Resource previousPage;

    public Resource() {        
    }

    public Resource(String id) {        
        this.id = id;
    }

//    public Resource(String id, String title, String page, String editionDate, String fileName, String processing, String searchDate, String[] fragments) {        
//        this.id = id;
//        this.title = title;
//        this.page = page;
//        this.editionDate = editionDate;
//        this.fileName = fileName;
//        addSupportedFormat("pdf");
//        addSupportedFormat("jpg");
//        addSupportedFormat("xml");
//        addSupportedFormat("text");
//        _addAllFragments(fragments);
//    }

    public Resource(SearcherResource resource) {
        docId = resource.getPublicationId();
        if(resource.getPageId()!=null && !resource.getPageId().isEmpty()){
            pageId = resource.getPageId();
            id = docId.concat("_").concat(pageId);
        }else{
            id = docId;
        }
        title = resource.getTitle();
        page = resource.getPage();
        editionDate = resource.getEditionDate();
        __setCalcDate(resource.getProcessDateResult());
        _addAllSupportedFormat(resource);
//        _addAllFragments(resource.getFragments());        
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

    protected void setEditionDate(String editionDate) {
        this.editionDate = editionDate;
    }
    
    protected void setCalcDate(CalcDateMap datemap) {
        this.calcDate = datemap;
    }
    
    protected void setCalcDate(String calcDate) {
        this.__setCalcDate(calcDate);
    }
    
    private void __setCalcDate(String calcDate) {
        this.calcDate = new CalcDateMap(calcDate);
    }

    
    public String getEditionDate() {
        return this.editionDate;
    }
    
    public CalcDateMap getCalcDate() {
        return this.calcDate;
    }
    
    public String getCalcDateDescription() {
        return this.calcDate.getDesc();
    }
    
    public String getCalcDateId() {
        return this.calcDate.getId();
    }
    
    public String getSupportedFormatsAsSingleString() {
        return String.join(" ", getSupportedFormats());
    }
    
    public ResourceFormat[] getResourceFormats() {
        ResourceFormat[] ret = new ResourceFormat[resourceFormats.size()];        
        for(int i=0; i<resourceFormats.size(); i++){
            ret[i]= resourceFormats.get(i);
        }
        return resourceFormats.toArray(ret);
    }

    public String[] getSupportedFormats() {
        String[] ret = new String[resourceFormats.size()];        
        for(int i=0; i<resourceFormats.size(); i++){
            ret[i]= resourceFormats.get(i).getFormat();
        }
        return ret;
    }

    public String getSupportedFormat(int idx) {
        return resourceFormats.get(idx).getFormat();
    }

    public void deleteSupportedFormat(String format) {
        int i=0;
        while(i<resourceFormats.size()&&resourceFormats.get(i).getFormat().equalsIgnoreCase(format)){
            i++;
        }
        if(i<resourceFormats.size()){
            this.resourceFormats.remove(i);
        }
    }

    protected void deleteSupportedFormat(int ind) {
        this.resourceFormats.remove(ind);
    }

    protected void addSupportedFormat(String format) {
        this.resourceFormats.add(new ResourceFormat(format, "P"));
    }

    protected void addSupportedFormat(String format, String type) {
        this.resourceFormats.add(new ResourceFormat(format, type));
    }

//    protected void addAllSupportedFormat(String[] formats) {
//        _addAllSupportedFormat(formats);;
//    }
//    
    private void _addAllSupportedFormat(SearcherResource sr) {
        String[] formats = sr.getSupportedFormats();
        for(String format: formats){
            addResourceFormat(format, sr.getContentTypeFormat(format));
        }
    }
    
    protected void addResourceFormat(String format, String type) {
        ResourceFormat rf = new ResourceFormat(format, type);
        if(!this.resourceFormats.contains(rf)){
            this.resourceFormats.add(rf);
        }
    }

    protected void addAllResourceFormat(SimpleImmutableEntry<String,String>[] formats) {
        _addAllResourceFormat(formats);;
    }
    
    private void _addAllResourceFormat(SimpleImmutableEntry<String,String>[] formats) {
        for(SimpleImmutableEntry<String, String> format: formats){
            this.resourceFormats.add(new ResourceFormat(format.getKey(), format.getValue()));
        }
    }
    
    protected String[] getAlternativeFormats(String format) {
        return FormatedResourceUtils.getAlternativeFormats(format);
    }

    public boolean isFormatSupported(String format) {
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
        String filaName = this.getFileName(format);
        LocalFormatedFile ff = new LocalFormatedFile(this.getLocalFilePath(), filaName.concat(".").concat(format), format, filaName);
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
//        strb.append(". Searched on; ");
//        strb.append(this.searchDate);
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

    public String getFileName(String format) {
        return Utils.getFilename(this, format);
    }
    
    public String getDocId(){
        return docId;
    }

    public String getPageId(){
        return pageId;
    }

    public String getContentTypeFromFormat(String format){
        String ret = "";
        for(int i=0; i<resourceFormats.size()&&ret.length()==0; i++){
            if(format.equalsIgnoreCase(resourceFormats.get(i).getFormat())){
                ret = resourceFormats.get(i).getContentType();
            }
        }
        return ret;
    }
    
    protected void setSupportedFormats(List<ResourceFormat> supportedFormats) {
        this.resourceFormats = supportedFormats;
    }

    protected void setSupportedFormats(ResourceFormat[] supportedFormats) {
        this.resourceFormats = Arrays.asList(supportedFormats);
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }
    
    public void updateSingleData(Resource res, boolean updateFormats){
        int oldi;
        if(!getTitle().equals(res.getTitle())){
            setTitle(res.getTitle());
        }
        if(!getPage().equals(res.getPage())){
            setPage(res.getPage());
        }
        if(!getEditionDate().equals(res.getEditionDate())){
            setEditionDate(res.getEditionDate());
        }
        if(!getCalcDate().equals(res.getCalcDate())){
            setCalcDate(res.getCalcDate());
        }
        if(updateFormats){
            List<ResourceFormat> aux = Arrays.asList(res.getResourceFormats());
            for(int i=this.resourceFormats.size()-1; i>=0; i--){
                if(!aux.contains(this.resourceFormats.get(i))){
                    this.resourceFormats.remove(i);
                }
            }
            for(int i=0; i<aux.size(); i++){
                if((oldi = this.resourceFormats.indexOf(aux.get(i)))!=-1){
                    this.resourceFormats.get(oldi).setContentType(aux.get(i).getContentType());
                }else{
                    this.resourceFormats.add(aux.get(i));
                }
            }
        }
    }
    
    public void updateSingleData(Resource res){
        updateSingleData(res, true);
    }    

//    /**
//     * @return the associatedPages
//     */
//    public List<AssociatedPage> getAssociatedPages() {
//        return associatedPages;
//    }

    /**
     * @return the nextPage
     */
    public Resource getNextPage() {
        return nextPage;
    }

    /**
     * @param nextPage the nextPage to set
     */
    public void setNextPage(Resource nextPage) {
        if(this.nextPage!=null){
            this.nextPage.previousPage= null;
        }
        this.nextPage = nextPage;
        if(nextPage!=null){
            nextPage.previousPage = this;
        }
    }

    /**
     * @return the previousPage
     */
    public Resource getPreviousPage() {
        return previousPage;
    }

    /**
     * @param previousPage the previousPage to set
     */
    public void setPreviousPage(Resource previousPage) {
        if(this.previousPage!=null){
            this.previousPage.nextPage=null;
        }
        this.previousPage = previousPage;
        if(previousPage!=null){
            previousPage.nextPage= this;
        }
        
    }
}
