/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;

/**
 *
 * @author josep
 */
public class BvphFormatedFile implements FormatedFile{
    String format;
    String name;
    String fileName;
    String url;

    public BvphFormatedFile(String url, String format, String Name, String fileName) {
        this.format = format;
        this.name = Name;
        this.fileName = fileName;
        this.url = url;
    }
            
    @Override
    public InputStream getImInputStream() {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            return connection.getInputStream();
        } catch (IOException ex) {
           throw new ErrorGettingRemoteResource("Error getting "+url );
        }
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
        return fileName;
    }

    @Override
    public InputStream getImInputStream(DownloaderProperties dp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
