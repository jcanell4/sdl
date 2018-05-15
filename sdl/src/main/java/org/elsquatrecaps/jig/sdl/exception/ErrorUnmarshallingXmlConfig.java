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
public class ErrorUnmarshallingXmlConfig extends RuntimeException{

    public ErrorUnmarshallingXmlConfig() {
        super();
    }
    
    public ErrorUnmarshallingXmlConfig(String message){
        super(message);
    }
    
    public ErrorUnmarshallingXmlConfig(Throwable cause){
        super(cause);
    }

    public ErrorUnmarshallingXmlConfig(String message, Throwable cause){
        super(message, cause);
    }
}
