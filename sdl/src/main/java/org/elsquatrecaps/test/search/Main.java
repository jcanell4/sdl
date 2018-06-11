package org.elsquatrecaps.test.search;

import org.elsquatrecaps.jig.sdl.searcher.BvphSearchIterator;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.searcher.BvphGetRemoteProcess;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elsquatrecaps.jig.sdl.searcher.BvphGetRemoteProcess;
import org.elsquatrecaps.jig.sdl.searcher.BvphSearchIterator;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;

/**
 *
 * @author josep
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File carpeta = new File("xml");
        Map<String, String> params = new HashMap<>();
            
        params.put("busq_general", "marinero");
        params.put("descrip_idlistpais", "España");
        params.put("general_ocr", "on");

        BvphGetRemoteProcess remote = new BvphGetRemoteProcess(params);
        remote.setBiggerYear(1980);
        
        BvphSearchIterator iterator= new BvphSearchIterator(remote);
        
        System.out.print("marinero: ");
        
        if(!carpeta.exists()){
            carpeta.mkdir();
        }
        while(iterator.hasNext()){
            FileOutputStream outStream = null;
            InputStream is = null;
;
            try {
                FormatedFile ff = iterator.next().getFormatedFile("xml");
                outStream = new FileOutputStream(new File(carpeta, ff.getFileName()));
                is =  ff.getImInputStream();
                
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                try {
                    outStream.close();
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
//        while(iterator.hasNext()){
//            iterator.next();
//        }
        
        
//        remote.setParam("busq_general", "aliacalil");
//        iterator= new BvphSearchIterator(remote);
//
//        System.out.print("aliacalil: ");
//        System.out.println(iterator.hasNext());
//        
//        
//
//        remote.setParam("busq_general", "streptococo");
//        remote.setParam("busq_rango0_fechapubinicial__fechapubfinal", "01/01/1700");
//        remote.setParam("busq_rango1_fechapubinicial__fechapubfinal", "31/12/1700");
//        iterator= new BvphSearchIterator(remote);
//
//        System.out.print("streptococo: ");
//        System.out.println(iterator.hasNext());
    }
    
}
