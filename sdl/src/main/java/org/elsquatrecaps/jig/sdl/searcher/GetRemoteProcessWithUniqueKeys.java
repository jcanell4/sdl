/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author josep
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetRemoteProcessWithUniqueKeys extends AbstractGetRemoteProcess {
    @XmlTransient
    private Map<String, String> params=null;
    
    public GetRemoteProcessWithUniqueKeys(){    
    }
    
    public GetRemoteProcessWithUniqueKeys(String url){
        super(url);
    }
    
    public GetRemoteProcessWithUniqueKeys(String url, Map<String, String> params){
        super(url);
        this.params = params;
    }
    
    public GetRemoteProcessWithUniqueKeys(String url, Map<String, String> params, Map<String, String> cookies){
        super(url, cookies);
        this.params = params;
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
    
    protected Connection getConnection(){
        Connection con;
        if(getParams()!=null){
            con = getConnection(getUrl().concat(getQueryPath())).data(getParams());
        }else{
            con = getConnection(getUrl().concat(getQueryPath()));
        }
        return con;
    }
    
    public Map<String, String> getParams(){
        return  params;
    } 
    
}
