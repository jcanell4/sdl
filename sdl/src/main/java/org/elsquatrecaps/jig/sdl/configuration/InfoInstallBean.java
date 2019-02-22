/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import org.elsquatrecaps.jig.sdl.exception.ErrorWritingPropertyFile;
import org.elsquatrecaps.jig.sdl.persistence.patcher.PatchSDLDB;

public class InfoInstallBean {
    File fout  = new File("info/install.info");
    
    public void close(){
        PrintWriter out = null;
        Calendar today=  Calendar.getInstance();
        try {
            if(!fout.exists()){
                fout.getParentFile().mkdirs();
                out = new PrintWriter(fout);
                out.println(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", today));
            }
        } catch (FileNotFoundException ex) {
            throw new ErrorWritingPropertyFile(ex);
        } catch (IOException ex) {
            throw new ErrorWritingPropertyFile(ex);
        } finally {
            if(out!=null){
                out.close();
                PatchSDLDB patcher = new PatchSDLDB();
                patcher.setDbAsUpgraded();
            }
        }
    }    
    
    public boolean isDataBaseInstalled(){
        return fout.exists();
    }
}
