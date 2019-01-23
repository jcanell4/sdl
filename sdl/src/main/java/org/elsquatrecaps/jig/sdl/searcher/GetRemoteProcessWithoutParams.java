/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author josep
 */
public class GetRemoteProcessWithoutParams extends AbstractGetRemoteProcess {
    public GetRemoteProcessWithoutParams(){    
    }
    
    public GetRemoteProcessWithoutParams(String url){
        super(url);
    }

    public GetRemoteProcessWithoutParams(String url, Map<String, String> cookies){
        super(url, cookies);
    }
    
    public void setParam(String key, String value){
        throw new UnsupportedOperationException("setParam method is not supported here");
    }
    
    protected Connection getConnection(){
        Connection con;
        con = Jsoup.connect(getUrl().concat(getQueryPath()));
        this.configConnection(con);
        return con;
    }    
}
