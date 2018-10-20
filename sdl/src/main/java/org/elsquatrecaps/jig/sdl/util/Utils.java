/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.elsquatrecaps.jig.sdl.exception.ErrorCopyingFileFormaException;

/**
 *
 * @author josep
 */
public class Utils {
    
    public static String getDateFromText(String date, String sep){
        return getDateFromText(date, sep, false);
    }
    
    public static String getDateFromText(String date, String sep, boolean reversedate){
        String ret;
        if(reversedate){
            ret="0000".concat(sep).concat("01").concat(sep).concat("01");
        }else{
            ret="01".concat(sep).concat("01").concat(sep).concat("0000");
        }
        boolean found = false;
        int id = 0;
        String[] monthPatterns = {
            ".*(\\d{1,4})\\s+(enero|gener|january)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(febrero|febrer|february)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(marzo|març|march)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(abril|april)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(mayo|maig|may)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(junio|juny|june)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(julio|juliol|july)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(agosto|agost|august)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(septiembre|setembre|september)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(octubre|october)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(noviembre|novembre|november)\\s+(\\d{1,4}).*",
            ".*(\\d{1,4})\\s+(diciembre|desembre|december)\\s+(\\d{1,4}).*"
        };
        
        while(!found && id<monthPatterns.length){
            Pattern pattern = Pattern.compile(monthPatterns[id], Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(date);
            found = matcher.find();
            if(found){
                String year = matcher.group(1);
                if(year.length()<4){
                    year = matcher.group(3);
                    if(year.length()<4){
                        year = "0000";
                    }
                }
                if(reversedate){
                    ret = year.concat(sep).concat(String.valueOf(id+1)).concat(sep).concat("01");
                }else{
                    ret = "01".concat(sep).concat(String.valueOf(id+1)).concat(sep).concat(year);
                }
            }
        }
        return ret;
    }
    
    public static void copyToFile(InputStream in, FileOutputStream out) {
        
        copyToFile(in, out, true, true);
    }
    
    public static void copyToFile(InputStream in, FileOutputStream out, boolean closein, boolean closeout) {
        try {
            byte[] buffer = new byte[1024];
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
        } catch (IOException | NullPointerException ex) {
            throw new ErrorCopyingFileFormaException(ex);        
        } finally {
            if(closein && in!=null){
                try {
                    in.close();
                } catch (IOException ex) {}
            }
            if(closeout && out!=null){
                try {
                    out.close();
                } catch (IOException ex) {}
            }
        }
    }
    
    public static String buildNormalizedFilename(String name, int maxLength){
        return buildNormalizedFilename(name).substring(0,maxLength);
    }
    
    public static String buildNormalizedFilename(String name){
        StringBuilder strBuffer = new StringBuilder();
        String locTitle;
        String ret;
        Pattern pattern1 = Pattern.compile(
                              "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
        Pattern pattern2 = Pattern.compile(
                              "[+-.:,;<>\\{\\}\\[\\]\\*\\^\\¿\\?\\=\\)\\(\\/\\&\\%ºº$\\·\\#\\@\\|\\\\!\"]+");
        locTitle = Normalizer.normalize(name, Normalizer.Form.NFD);
        locTitle = pattern1.matcher(locTitle).replaceAll("");
        locTitle = pattern2.matcher(locTitle).replaceAll("");
        String[] words = locTitle.split(" ");
        if(words.length>0){
            strBuffer.append("_");
        }
        for (String word : words){
            if(word.length()>0){
                strBuffer.append(word.substring(0, 1).toUpperCase());
                strBuffer.append(word.substring(1).toLowerCase());
            }
        }
        return strBuffer.toString();
    }

}
