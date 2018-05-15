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
public class ErrorParsingDate extends RuntimeException{

    public ErrorParsingDate() {
        super();
    }
    
    public ErrorParsingDate(String message){
        super(message);
    }
    
    public ErrorParsingDate(Throwable cause){
        super(cause);
    }

    public ErrorParsingDate(String message, Throwable cause){
        super(message, cause);
    }
}
