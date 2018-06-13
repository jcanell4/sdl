/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;

/**
 *
 * @author josep
 */
public class LocalFormatedFile implements FormatedFile{
    private String filename;
    private String path;
    private String format;
    private String name;


    public LocalFormatedFile(String repository, String filename, String format, String name) {
        this.filename = filename;
        this.path = repository;
        this.format = format;
        this.name = name;
    }

    public InputStream getImInputStream(DownloaderProperties dp) {
        return this.getImInputStream();
    }
    
    @Override
    public InputStream getImInputStream() {
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
        return format;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileName() {
        return  filename;
    }
}
