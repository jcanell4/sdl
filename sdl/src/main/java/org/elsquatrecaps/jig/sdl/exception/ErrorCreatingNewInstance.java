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
public class ErrorCreatingNewInstance extends RuntimeException{

    public ErrorCreatingNewInstance() {
        super();
    }
    
    public ErrorCreatingNewInstance(String message){
        super(message);
    }
    
    public ErrorCreatingNewInstance(Throwable cause){
        super(cause);
    }

    public ErrorCreatingNewInstance(String message, Throwable cause){
        super(message, cause);
    }
}
