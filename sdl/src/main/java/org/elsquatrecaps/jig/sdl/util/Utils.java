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
        String year;
        if(reversedate){
            ret="0000".concat(sep).concat("01").concat(sep).concat("01");
        }else{
            ret="01".concat(sep).concat("01").concat(sep).concat("0000");
        }
        boolean found = false;
        int id = 0;
        String[] datePatterns = {".*(\\d{4})\\s+.{0,5}\\s*(%s)\\s+.*", ".*\\s+(%s)\\s+.{0,5}\\s*(\\d{4}).*"};
        String[]monthPatterns = {
            "ene|enero|gen|gener|jan|january",
            "feb|febrero|febrer|february",
            "mar|marzo|març|march",
            "abr|abril|apr|april",
            "may|mayo|mai|maig",
            "jun|junio|juny|june",
            "jul|julio|juliol|july",
            "ago|agosto|agost|aug|august",
            "sep|septiembre|set|setembre|september",
            "oct|octubre|october",
            "nov|noviembre|novembre|november",
            "dic|diciembre|des|desembre|dec|december"
        };
        
        while(!found && id<monthPatterns.length){
            for(int alt=0; !found && alt<2; alt++){
                Pattern pattern = Pattern.compile(String.format(datePatterns[alt], monthPatterns[id]), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(date);
                found = matcher.find();
                if(found){
                    year = matcher.group(alt+1);
                    if(reversedate){
                        ret = year.concat(sep).concat(String.valueOf(id+1)).concat(sep).concat("01");
                    }else{
                        ret = "01".concat(sep).concat(String.valueOf(id+1)).concat(sep).concat(year);
                    }
                }
            }
            ++id;
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
