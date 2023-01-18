/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.jig.sdl.persistence.patcher;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author josepcanellas
 */
public abstract class PatchCodeClass {
    protected static final Logger logger = LoggerFactory.getLogger(PatchCodeClass.class);
//    protected static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ErrorGettingRemoteResource.class.getName());
//    static {
//        try { 
//            logger.addHandler(new FileHandler("log/DocumentsNoBaixats.txt"));
//        } catch (SecurityException | IOException ex) {
//            logger.error("Error creant el fitxer de registres dels documents no obtinguts a casoa de: ".concat(ex.getMessage()), ex);
//        }
//    }
    protected static final String loggerPrefix = "[PatcherCode] ";
    protected int currentVersion;
    protected Properties properties;
    
    public PatchCodeClass(Integer version, Properties properties){
        currentVersion = version;
        this.properties = properties;
    }
    public abstract void run();
}
