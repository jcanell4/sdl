/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import org.elsquatrecaps.jig.sdl.exception.ErrorParsingUrl;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@XmlType()
public class GetRemoteProcess {
    @XmlTransient
    private String url;
    @XmlTransient
    private String queryPath = "";
    @XmlTransient
    private Map<String, String> params=null;
    @XmlTransient
    private Map<String, String> cookies=null;

    public GetRemoteProcess(){    
    }
    
    public GetRemoteProcess(String url){
        this.url = url;
    }
    
    public GetRemoteProcess(String url, Map<String, String> params){
        this.url = url;
        this.params = params;
    }
    
    public GetRemoteProcess(String url, Map<String, String> params, Map<String, String> cookies){
        this.url = url;
        this.params = params;
        this.cookies = cookies;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url){
        this.url=url;
    }
    
    public void setCriteria(SearchCriteria criteria){
        throw new UnsupportedOperationException();
    }
    
    public void setParams(Map<String, String> params){
        this.params = params;
    }
    
    public String getParam(String key){
        return this.params.get(key);
    }
    
    public void setParam(String key, String value){
        if(params==null){
            params = new HashMap<>();
        }
        params.put(key, value);
    }
    
    public Element get(){
        return this.getOriginalSource();
    }

    private Connection getConnection(){
        Connection con;
        if(params!=null){
            con = Jsoup.connect(getUrl().concat(getQueryPath())).data(params);
        }else{
            con = Jsoup.connect(getUrl().concat(getQueryPath()));
        }
        if(getCookies()!=null && !cookies.isEmpty()){
            con.cookies(getCookies());
        }
        con.timeout(60000);
        return con;
    }

    private synchronized Element getOriginalSource() {
        int factor = 200;
        Connection.Response resp;
        Throwable ioe = null;
        Document remote=null;
        for(int c=1; remote==null && c<10; c++){
            try{
                Connection con = getConnection();
                resp = con.execute();
                setCookies(resp.cookies());
                remote = resp.parse();
//                  if(params==null){
//                    remote = getConnection().get();
//                }else{
//                    remote = getConnection().data(params).get();
//                }
            } catch (UncheckedIOException | IOException ex ) {
                ioe = ex;
                try {
                    this.wait(c*factor);
                    if(c==5){
                        factor = 500;
                    }
                } catch (InterruptedException ex1) {
                    ioe = ex1;
                }
            } catch (Exception ex ) {
                ioe = ex;
            }
        }
        if(remote==null){
            throw new ErrorGettingRemoteResource(ioe);
        }
        return remote;
    }
    
    public static String relativeToAbsoluteUrl(String context, String relative){
        String ret;
        try {
            ret = new URL(new URL(context), relative).toString();
        } catch (MalformedURLException ex) {
            throw new ErrorParsingUrl(ex);
        }            
        return ret;
    }    

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String getQueryPath() {
        return queryPath;
    }

    public void setQueryPath(String queryPath) {
        this.queryPath = queryPath;
    }
}
