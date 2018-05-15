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
public class ErrorParsingUrl extends RuntimeException{

    public ErrorParsingUrl() {
        super();
    }
    
    public ErrorParsingUrl(String message){
        super(message);
    }
    
    public ErrorParsingUrl(Throwable cause){
        super(cause);
    }

    public ErrorParsingUrl(String message, Throwable cause){
        super(message, cause);
    }
}
