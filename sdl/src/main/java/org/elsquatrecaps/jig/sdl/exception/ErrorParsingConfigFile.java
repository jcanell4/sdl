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
public class ErrorParsingConfigFile extends RuntimeException{

    public ErrorParsingConfigFile() {
        super();
    }
    
    public ErrorParsingConfigFile(String message){
        super(message);
    }
    
    public ErrorParsingConfigFile(Throwable cause){
        super(cause);
    }

    public ErrorParsingConfigFile(String message, Throwable cause){
        super(message, cause);
    }
}
