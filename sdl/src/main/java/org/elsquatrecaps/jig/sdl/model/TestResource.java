/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

public class TestResource extends Resource{
    private static final String[] SUPPORTED_FORMATS={"jpg", "txt", "xml"};
 
    public TestResource(long id, long searchId, String title, String page, String processingAnalysis, String editionDate, String searchDate, String[] fragments) {
        super();
//        this.setId(id);
//        this.setSearchId(searchId);
        this.setTitle(title);
        this.setPage(page);
        this.setProcessingAnalysis(processingAnalysis);
        this.setEditionDate(editionDate);
        this.setSearchDate(searchDate);
        this.addAllFragments(fragments);
    }
    
    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }

}
