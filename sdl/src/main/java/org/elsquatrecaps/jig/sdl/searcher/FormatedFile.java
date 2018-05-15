/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.io.InputStream;

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
