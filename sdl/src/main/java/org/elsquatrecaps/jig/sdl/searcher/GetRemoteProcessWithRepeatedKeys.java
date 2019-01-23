/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlTransient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author josep
 */
public class GetRemoteProcessWithRepeatedKeys extends AbstractGetRemoteProcess {
    @XmlTransient
    private List<String> params;
    
    public GetRemoteProcessWithRepeatedKeys(){    
        this.params = null;
    }
    
    public GetRemoteProcessWithRepeatedKeys(String url){
        super(url);
        this.params = null;
    }
    
    public GetRemoteProcessWithRepeatedKeys(String url, List<String> params){
        super(url);
        this.params = null;
        this.params = params;
    }
    
    public GetRemoteProcessWithRepeatedKeys(String url, List<String> params, Map<String, String> cookies){
        super(url, cookies);
        this.params = null;
        this.params = params;
    }
    
    public void setParams(List<String> params){
        this.params = params;
    }
    
    public String getParam(String key){
        String ret = null;
        int pos=0;
        while(pos<params.size() && !params.get(pos).equals(key)){
            pos+=2;
        }
        if(pos<params.size()){
            ret = params.get(pos+1);
        }
        return ret;
    }
    
    public void setParam(String key, String value){
        if(params==null){
            params = new ArrayList<>();
        }
        params.add(key);
        params.add(value);
    }
    
    protected Connection getConnection(){
        Connection con;
        if(getParams()!=null){            
            String[] aParams = new String[getParams().size()];
            aParams = getParams().toArray(aParams);
            con = Jsoup.connect(getUrl().concat(getQueryPath())).data(aParams);
        }else{
            con = Jsoup.connect(getUrl().concat(getQueryPath()));
        }
        this.configConnection(con);
        return con;
    }
    
    public List<String> getParams(){
        return  params;
    }     
}
