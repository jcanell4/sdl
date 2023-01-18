/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.jig.sdl.persistence.patcher;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.elsquatrecaps.jig.sdl.util.Utils;

/**
 *
 * @author josepcanellas
 */
public class RenameFilesForVersion2023 extends PatchCodeClassWithDBConnection{

    public RenameFilesForVersion2023(Integer version, Properties properties){
        super(version, properties);
    }

    @Override
    public void run() {
        File of;
        File nf;
        try {
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Select r.*, rf.SUPPORTEDFORMATS from RESOURCE as r inner join RESOURCE_FORMAT as rf on r.ID = rf.RESOURCE_ID");
            while(rs.next()){
                String oldFile = rs.getString("FILENAME");
                String newFile = Utils.getFileName(
                        rs.getString("EDITIONDATE"),
                        rs.getString("CALCDATE_ID"),
                        "D",
                        rs.getString("ID"),
                        "", 
                        rs.getString("PAGE"), 
                        rs.getString("TITLE"));
                oldFile = oldFile.substring(0,Math.min(60, oldFile.length()));
                oldFile = oldFile.concat(".").concat(rs.getString("SUPPORTEDFORMATS"));;        
                newFile = newFile.concat(".").concat(rs.getString("SUPPORTEDFORMATS"));;      
                of = new File(".".concat(File.separator).concat(properties.getProperty("downloader.localRepository")).concat(File.separator).concat(oldFile));                
                nf = new File(".".concat(File.separator).concat(properties.getProperty("downloader.localRepository")).concat(File.separator).concat(newFile));   
                if(of.renameTo(nf)){
                    logger.debug("Renamed file ".concat(of.toString()).concat(" to ").concat(nf.toString()));
                }else{
                    logger.debug("Unable to rename file ".concat(of.toString()).concat(" to ").concat(nf.toString()));
                }
            }
        } catch (SQLException ex) {
            logger.error(loggerPrefix.concat(ex.getMessage()));
            throw new RuntimeException(ex);
        }        
    }
    
}
