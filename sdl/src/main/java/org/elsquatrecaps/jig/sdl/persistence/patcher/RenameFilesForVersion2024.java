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
public class RenameFilesForVersion2024 extends PatchCodeClassWithDBConnection{

    public RenameFilesForVersion2024(Integer version, Properties properties){
        super(version, properties);
    }

    @Override
    public void run() {
        File of;
        File nf;
        try {
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("Select r.*, rf.FORMAT, rf.CONTENTTYPE from RESOURCE as r inner join RESOURCE_FORMAT as rf on r.ID = rf.RESOURCE_ID");
            while(rs.next()){
                String oldFile = getOldFileName(
                        rs.getString("EDITIONDATE"),
                        rs.getString("CALCDATE_ID"),
                        rs.getString("CONTENTTYPE"),
                        rs.getString("DOCID"),
                        rs.getString("PAGEID"),
                        rs.getString("PAGE"), 
                        rs.getString("TITLE"),
                        false);
                String newFile = Utils.getFileName(
                        rs.getString("EDITIONDATE"),
                        rs.getString("CALCDATE_ID"),
                        rs.getString("CONTENTTYPE"),
                        rs.getString("DOCID"),
                        rs.getString("PAGEID"),
                        rs.getString("PAGE"), 
                        rs.getString("TITLE"));
                oldFile = oldFile.concat(".").concat(rs.getString("FORMAT"));;        
                newFile = newFile.concat(".").concat(rs.getString("FORMAT"));;      
                of = new File(".".concat(File.separator).concat(properties.getProperty("downloader.localRepository")).concat(File.separator).concat(oldFile));                
                if(!of.exists()){
                    oldFile = getOldFileName(
                        rs.getString("EDITIONDATE"),
                        rs.getString("CALCDATE_ID"),
                        rs.getString("CONTENTTYPE"),
                        rs.getString("DOCID"),
                        rs.getString("PAGEID"),
                        rs.getString("PAGE"), 
                        rs.getString("TITLE"),
                        false);
                    of= new File(".".concat(File.separator).concat(properties.getProperty("downloader.localRepository")).concat(File.separator).concat(oldFile));                
                }
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
    
    private static String getOldFileName(String editionDate, String calcDate, String contentType, String docId, String pageId, String page, String title, boolean without_InTitle) {
        StringBuilder strBuffer = new StringBuilder();
        if(editionDate!=null && !editionDate.isEmpty() && editionDate.matches("[0-9]{2}\\/[0-9]{2}\\/[0-9]{2,4}")){            
            String[] aDate = editionDate.split("\\/");
            strBuffer.append(aDate[2]);
            strBuffer.append("_");
            strBuffer.append(aDate[1]);
            strBuffer.append("_");
            strBuffer.append(aDate[0]);
        }else if(editionDate!=null && !editionDate.isEmpty() && editionDate.matches("[0-9]{4}.+?[0-9]{1,2}")){            
            strBuffer.append(editionDate.substring(0, 4));
            strBuffer.append("_00_00");
        }else if(editionDate!=null && !editionDate.isEmpty() && editionDate.matches("[0-9]{2}.+?[0-9]{4}")){            
            strBuffer.append(editionDate.substring(editionDate.length()-4, editionDate.length()));
            strBuffer.append("_00_00");
        }else if(editionDate!=null && !editionDate.isEmpty() && editionDate.matches("[0-9]{1}.+?[0-9]{4}")){            
            strBuffer.append(editionDate.substring(editionDate.length()-4, editionDate.length()));
            strBuffer.append("_00_00");
        }else{
            strBuffer.append("0000_00_00");
        }
        strBuffer.append("_");
        strBuffer.append(calcDate);
        strBuffer.append("_");
        strBuffer.append(docId);            
        if(!contentType.equals("D")){
            strBuffer.append("_");
            strBuffer.append(pageId.trim());                        
            strBuffer.append("_");
            strBuffer.append(Utils.buildNormalizedFilename(page.trim()));            
        }
        if(!without_InTitle){
            strBuffer.append("_");
        }
        strBuffer.append(Utils.buildNormalizedFilename(title.trim()));
        return strBuffer.toString().substring(0,Math.min(75, strBuffer.length()));        
    }

}
