/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author josep
 */
public class BvphJpgFile extends BasicSearcherFormatedFile{
    
    
    public BvphJpgFile(String url, String Name, String fileName) {
        super(url, "jpg", Name, fileName);
    }
    
//    @Override
//    public InputStream getImInputStream() {
//        AbstractGetRemoteProcess remoteProcess = new GetRemoteProcessWithoutParams(url);
//        Element elem = remoteProcess.get();
//        Element elemForm = elem.selectFirst("form[name='imprimirForm']");
//        String sep = "?";
//        url = relativeToAbsoluteUrl(remoteProcess.getUrl(), elemForm.attr("action"));
//        Elements inputElems =  elemForm.select("input[type='hidden']");
//        for(Element e : inputElems){
//            url+=sep+e.attr("name")+"="+e.attr("value");
//            sep="&";
//        }
//        return _getImInputStream();
//    }
//    
//    private InputStream _getImInputStream() {
//        try {
//            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
//            return connection.getInputStream();
//        } catch (IOException ex) {
//           throw new ErrorGettingRemoteResource("Error getting "+url );
//        }
//    }
//    private String relativeToAbsoluteUrl(String base, String relative){
//        return  AbstractGetRemoteProcess.relativeToAbsoluteUrl(base, relative);
//    }
    
}
