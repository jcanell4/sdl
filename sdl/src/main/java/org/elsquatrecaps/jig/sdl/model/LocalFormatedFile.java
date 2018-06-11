/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import java.io.InputStream;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author josep
 */
public class LocalFormatedFile implements FormatedFile{
    private String filename;
    private DownloaderProperties downloaderProperties;

    public LocalFormatedFile() {
        
    }
    
    public LocalFormatedFile(String filename) {
        this.filename = filename;
    }

    @Override
    public InputStream getImInputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFormat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFileName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Autowired
    protected void setDownloaderProperies(DownloaderProperties dp) {
        this.downloaderProperties = dp;
    }
}
