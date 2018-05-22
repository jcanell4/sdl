package org.elsquatrecaps.jig.sdl.searcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.elsquatrecaps.jig.sdl.exception.UnsupportedFormat;

public abstract class Resource {
    static final String[] FORMATS ={"pdf", "jpg", "tif", "txt", "xml", "epub"};
    private static final Map<String, String[]>ALTERNATIVE_FORMATS = new HashMap<>();
    static {
       ALTERNATIVE_FORMATS.put("pdf", new String[]{"tif", "jpg", "epub, txt"});
       ALTERNATIVE_FORMATS.put("tif", new String[]{"jpg", "pdf"});
       ALTERNATIVE_FORMATS.put("jpg", new String[]{"tif", "pdf"});
       ALTERNATIVE_FORMATS.put("xml", new String[]{"txt"});
       ALTERNATIVE_FORMATS.put("epub", new String[]{"pdf", "txt"});
    }
   
    private long id;
    private long searchId;
    private String title;
    private String page;
    private String publicationId;
    private String pageId;
    private String searchDate;
    private String editionDate;
    private String processingAnalysis;
        
    private ArrayList<String> fragments= new ArrayList<>();

    protected String[] getAlternativeFormats(String format){
        return ALTERNATIVE_FORMATS.get(format);
    }
    
    abstract protected boolean isFormatSupported(String format);
    
    abstract public String[] getSupportedFormats();
    
    abstract public FormatedFile getFormatedFile();
    
    public FormatedFile getFormatedFile(String format){
        String formatDef = null;
        boolean found = false;
        LinkedList<String> formats = new LinkedList<>();
        formats.add(format);
        while(!found && !formats.isEmpty()){
            formatDef = formats.pop();
            found = isFormatSupported(formatDef);
            if(!found){
                formats.addAll(Arrays.asList(getAlternativeFormats(formatDef)));
            }            
        }
        if(!found){
            throw new UnsupportedFormat();
        }
        return getStrictFormatedFile(formatDef);
    }
    
    abstract protected FormatedFile getStrictFormatedFile(String format);
    
    public String getPublicationId() {
        return publicationId;
    }

    public String getTitle() {
        return title;
    }

    public String getPage() {
        return page;
    }

    public String getPageId() {
        return pageId;
    }

    public String[] getFragments() {
        String[] ret = new String[fragments.size()];
        return fragments.toArray(ret);
    }

    public String getFragment(int idx) {
        return fragments.get(idx);
    }

    protected void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    protected void setPageId(String pageId) {
        this.pageId = pageId;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setPage(String page) {
        this.page = page;
    }

    protected void addFragment(String fragment) {
        this.fragments.add(fragment);
    }

    protected void addAllFragments(String[] fragment) {
        for(String frg: fragment){
            this.fragments.add(frg);
        }
    }

    public void setSearhDate(String searchDate) {
        this.searchDate = searchDate;
    }
    
    public String getSearchDate() {
        return this.searchDate;
    }

    public void setEditionDate(String editionDate) {
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
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return this.id;
    }
 
    public long getSearchId() {
        return searchId;
    }

    public void setSearchId(long searchId) {
        this.searchId = searchId;
    }
}
