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
public class ErrorGettingRemoteResource extends RuntimeException{

    public ErrorGettingRemoteResource() {
        super();
    }
    
    public ErrorGettingRemoteResource(String message){
        super(message);
    }
    
    public ErrorGettingRemoteResource(Throwable cause){
        super(cause);
    }

    public ErrorGettingRemoteResource(String message, Throwable cause){
        super(message, cause);
    }
}
