/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import org.elsquatrecaps.jig.sdl.searcher.FormatedFile;
import org.elsquatrecaps.jig.sdl.searcher.Resource;


// ALERTA! Només per fer proves, ja hi ha una versió real implementada al searcher

public class TestResource extends Resource{
    private long id;
    private long searchId;
    private String title;
    private String page;
    private String processingAnalysis;
    private String editionDate;
    private String searchDate;
    private String[] fragments;
    private String[] supportedFormats;
            
    public TestResource(long id, long searchId, String title, String page, String processingAnalysis, String editionDate, String searchDate, String[] fragments, String[] supportedFormats) {
        this.id = id;
        this.searchId = searchId;
        this.title = title;
        this.page = page;
        this.processingAnalysis = processingAnalysis;
        this.editionDate = editionDate;
        this.searchDate = searchDate;
        this.fragments = fragments;
        this.supportedFormats = supportedFormats;        
    }
    
    
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPage() {
        return page;
    }

    @Override
    public String getProcessingAnalysis() {
        return processingAnalysis;
    }

    @Override
    public String getEditionDate() {
        return editionDate;
    }

    @Override
    public String getSearchDate() {
        return searchDate;
    }

    @Override
    public String[] getFragments() {
        return fragments;
    }

    @Override
    public String[] getSupportedFormats() {
        return supportedFormats;
    }
   
    @Override
    protected boolean isFormatSupported(String format) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FormatedFile getFormatedFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected FormatedFile getStrictFormatedFile(String format) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return this.id;
    }
}
