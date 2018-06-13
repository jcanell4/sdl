/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import java.io.InputStream;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;

/**
 *
 * @author josep
 */
public interface FormatedFile{
    InputStream getImInputStream();
    String getFormat();
    String getName();
    String getFileName();
}
