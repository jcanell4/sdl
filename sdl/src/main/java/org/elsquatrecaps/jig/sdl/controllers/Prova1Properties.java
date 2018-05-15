package org.elsquatrecaps.jig.sdl.controllers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "downloader")
public class Prova1Properties {
    private String localReasourceRepo;
    private String db;

    public String getLocalReasourceRepo() {
        return localReasourceRepo;
    }

    public void setLocalReasourceRepo(String localReasourceRepo) {
        this.localReasourceRepo = localReasourceRepo;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
