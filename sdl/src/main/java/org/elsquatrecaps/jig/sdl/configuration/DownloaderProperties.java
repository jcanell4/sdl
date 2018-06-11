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
    private String db;

    public String getLocalReasourceRepo() {
        return localRepository;
    }

    public void setLocalRepository(String localReasourceRepo) {
        this.localRepository = localReasourceRepo;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }    
}
