/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.elsquatrecaps.jig.sdl.exception.UnsupportedFormat;

/**
 *
 * @author josep
 */
public class FormatedResourceUtils {
    public static final String[] FORMATS ={"pdf", "jpg", "tif", "txt", "xml", "epub"};
    protected static final Map<String, String[]> ALTERNATIVE_FORMATS = new HashMap<>();
    static {
       ALTERNATIVE_FORMATS.put("pdf", new String[]{"tif", "jpg", "epub, txt"});
       ALTERNATIVE_FORMATS.put("tif", new String[]{"jpg", "pdf"});
       ALTERNATIVE_FORMATS.put("jpg", new String[]{"tif", "pdf"});
       ALTERNATIVE_FORMATS.put("xml", new String[]{"txt"});
       ALTERNATIVE_FORMATS.put("epub", new String[]{"pdf", "txt"});
    }
    
    public static String[] getAlternativeFormats(String format) {
        return ALTERNATIVE_FORMATS.get(format);
    }

    public static boolean isFormatSupported(String format, String[] supportedFormats) {
        boolean ret = false;
        for (int i = 0; !ret && i < supportedFormats.length; i++) {
            ret = supportedFormats[i].equalsIgnoreCase(format);
        }
        return ret;
    }

    public static String getFormat(String format, String[] supportedFormats) {
        String formatDef = null;
        boolean found = false;
        LinkedList<String> formats = new LinkedList<>();
        formats.add(format);
        while (!found && !formats.isEmpty()) {
            formatDef = formats.pop();
            found = isFormatSupported(formatDef, supportedFormats);
            if (!found) {
                formats.addAll(Arrays.asList(getAlternativeFormats(formatDef)));
            }
        }
        if (!found) {
            throw new UnsupportedFormat();
        }
        return formatDef;
    }
}
