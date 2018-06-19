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
public class ErrorWritingPropertyFile extends RuntimeException{

    public ErrorWritingPropertyFile() {
        super();
    }
    
    public ErrorWritingPropertyFile(String message){
        super(message);
    }
    
    public ErrorWritingPropertyFile(Throwable cause){
        super(cause);
    }

    public ErrorWritingPropertyFile(String message, Throwable cause){
        super(message, cause);
    }
}
