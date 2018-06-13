/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public InputStream getImInputStream(DownloaderProperties dp) {
        this.downloaderProperties = dp;
        return this.getImInputStream();
    }
    
    @Override
    public InputStream getImInputStream() {
        String path = this.downloaderProperties.getLocalReasourceRepo();
        File file = new File(path, this.filename);
        
        FileInputStream in = null;
                
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            //throw new FileNotFoundException();
            System.err.println("TEST: File not found." + this.filename);
            //throw new UnsupportedOperationException("TEST: File not found." + this.filename); //To change body of generated methods, choose Tools | Templates.
        }
        
        
        return in;
                
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
    
    
    
    
}
