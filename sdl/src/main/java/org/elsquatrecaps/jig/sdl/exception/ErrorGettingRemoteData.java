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
public class ErrorGettingRemoteData extends RuntimeException{

    public ErrorGettingRemoteData() {
        super();
    }
    
    public ErrorGettingRemoteData(String message){
        super(message);
    }
    
    public ErrorGettingRemoteData(Throwable cause){
        super(cause);
    }

    public ErrorGettingRemoteData(String message, Throwable cause){
        super(message, cause);
    }
}
