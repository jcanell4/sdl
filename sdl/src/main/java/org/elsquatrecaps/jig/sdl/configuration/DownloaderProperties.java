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
        return ret;
    }

    public void setMaxResourcesPerSearch(String maxResourcesPerSearch) {
        this.maxResourcesPerSearch = maxResourcesPerSearch;
    }
}
