/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteData;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.Connection;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
abstract public class AbstractGetRemoteProcess {
    @XmlElement
    private int connectionAttempts = 30;
    @XmlElement
    private String url;
    @XmlTransient
    private String queryPath = "";
    @XmlTransient
    private Map<String, String> cookies=null;

    public AbstractGetRemoteProcess(){    
    }
    
    public AbstractGetRemoteProcess(String url){
        this.url = url;
    }
    
    public AbstractGetRemoteProcess(String url, Map<String, String> cookies){
        this.url = url;
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
    
    abstract public void setParam(String key, String value);
    
    public Element get(){
        return this.getOriginalSource();
    }

    abstract protected Connection getConnection();
    
    protected  void configConnection(Connection con){
        if(getCookies()!=null && !cookies.isEmpty()){
            con.cookies(getCookies());
        }
        con.timeout(60000).maxBodySize(0);
        con.ignoreContentType(true);
    }
    
    private synchronized Element getOriginalSource() {
        int factor = 200;
        Connection.Response resp;
        Throwable ioe = null;
        Document remote=null;
        for(int c=1; remote==null && c<=connectionAttempts; c++){
            try{
                Connection con = getConnection();
                resp = con.execute();
                mergeCookies(resp.cookies());
                remote = resp.parse();
            } catch (UncheckedIOException | IOException ex ) {
                ioe = ex;
                try {
                    this.wait(c*factor);
                    if(c==5){
                        factor = 500;
                    }else if(c%10==0){
                        factor = (c/10)*1000;
                    }
                } catch (InterruptedException ex1) {
                    ioe = ex1;
                }
            } catch (Exception ex ) {
                ioe = ex;
            }
        }
        if(remote==null){
            throw new ErrorGettingRemoteData(String.format("Error de TIMEOUT successiu. S'han realitzat %d intents de 60 segons de durada", connectionAttempts), ioe);
        }
        return remote;
    }
    
    public static String relativeToAbsoluteUrl(String context, String relative){
        return Utils.relativeToAbsoluteUrl(context, relative);
    }    

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setDefaultCookies(Map<String, String> cookies) {
        if(this.getCookies()==null || this.getCookies().size()==0){
            this.setCookies(cookies);
        }        
    }
    
    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void mergeCookies(Map<String, String> cookies) {
        if(this.cookies==null){
            this.cookies = cookies;
        }else{
            AbstractGetRemoteProcess self = this;
            cookies.forEach(new BiConsumer<String, String>(){
                @Override
                public void accept(String key, String value) {
                    self.cookies.put(key, value);
                }
            });
        }
    }

    public String getQueryPath() {
        return queryPath;
    }

    public void setQueryPath(String queryPath) {
        this.queryPath = queryPath;
    }
//    
//    
//    static public void doTrustToCertificates() throws Exception {
//        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//        TrustManager[] trustAllCerts = new TrustManager[]{
//            new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//
//                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
//                    return;
//                }
//
//                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
//                    return;
//                }
//            }
//        };
//
//        SSLContext sc = SSLContext.getInstance("SSL");
//        sc.init(null, trustAllCerts, new SecureRandom());
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        HostnameVerifier hv = new HostnameVerifier() {
//            public boolean verify(String urlHostName, SSLSession session) {
//                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
//                     throw new RuntimeException("URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
//                }
//                return true;
//            }
//        };
//        HttpsURLConnection.setDefaultHostnameVerifier(hv);
//    }    
}
