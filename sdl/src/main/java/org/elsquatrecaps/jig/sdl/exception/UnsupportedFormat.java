/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.exception;

/**
 *
 * @author josep
 */
public class UnsupportedFormat extends RuntimeException {

    /**
     * Creates a new instance of <code>UnsupportedFormat</code> without detail
     * message.
     */
    public UnsupportedFormat() {
    }

    /**
     * Constructs an instance of <code>UnsupportedFormat</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnsupportedFormat(String msg) {
        super(msg);
    }
}
