/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.elsquatrecaps.jig.sdl.exception.ErrorCopyingFileFormaException;

/**
 *
 * @author josep
 */
public class Utils {
    
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
        } catch (IOException ex) {
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

}
