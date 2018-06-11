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
public class ErrorCopyingFileFormaException extends RuntimeException {

    public ErrorCopyingFileFormaException() {
        super();
    }
    
    public ErrorCopyingFileFormaException(String message){
        super(message);
    }
    
    public ErrorCopyingFileFormaException(Throwable cause){
        super(cause);
    }

    public ErrorCopyingFileFormaException(String message, Throwable cause){
        super(message, cause);
    }
}
