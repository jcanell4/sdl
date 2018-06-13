package org.elsquatrecaps.jig.sdl.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "downloader")
public class Prova1Properties {
    private String localRepository;
    private String db;

    public String getLocalReasourceRepo() {
        return localRepository;
    }

    public void setLocalReasourceRepo(String localReasourceRepo) {
        this.localRepository = localReasourceRepo;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
