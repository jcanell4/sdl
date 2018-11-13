/*  
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "downloader")
public class DownloaderProperties {
    private String localRepository;
    private String localExportPath;
    private String db;
    private String maxResourcesPerSearch = "0";
    private String infiniteLoopAvoided = "true";

    public String getLocalReasourceRepo() {
        return localRepository;
    }

    public void setLocalRepository(String localReasourceRepo) {
        this.localRepository = localReasourceRepo;
    }
    
    
    public String getLocalExportPath() {
        return localExportPath;
    }

    public void setLocalExportPath(String localExportPath) {
        this.localExportPath = localExportPath;
    }
    

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }    

    public String getMaxResourcesPerSearch() {
        return maxResourcesPerSearch;
    }

    public int getQuantity() {
        int ret;
        try{
            ret = Integer.parseInt(maxResourcesPerSearch);
        }catch(NumberFormatException e){
            ret = 0;
        }
        if(ret<=0 && mayAvoidInfiniteLoop()){
            ret = 300000;
        }
        return ret;
    }
    
    private boolean mayAvoidInfiniteLoop(){
        return (infiniteLoopAvoided.toLowerCase().startsWith("t")
                || infiniteLoopAvoided.toLowerCase().startsWith("s")
                || infiniteLoopAvoided.toLowerCase().startsWith("y"));
    }

    public void setMaxResourcesPerSearch(String maxResourcesPerSearch) {
        this.maxResourcesPerSearch = maxResourcesPerSearch;
    }

    /**
     * @return the infiniteLoopAvoided
     */
    public String getInfiniteLoopAvoided() {
        return infiniteLoopAvoided;
    }

    /**
     * @param infiniteLoopAvoided the infiniteLoopAvoided to set
     */
    public void setInfiniteLoopAvoided(String infiniteLoopAvoided) {
        this.infiniteLoopAvoided = infiniteLoopAvoided;
    }
}
