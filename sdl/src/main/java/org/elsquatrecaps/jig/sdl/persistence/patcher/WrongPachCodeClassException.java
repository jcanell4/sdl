/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.persistence.patcher;

/**
 *
 * @author josep
 */
public class WrongPachCodeClassException extends Exception{

    public WrongPachCodeClassException() {
    }

    public WrongPachCodeClassException(String message) {
        super(message);
    }
    
    public WrongPachCodeClassException(String message, Throwable th) {
        super(message, th);
    }
    
    public WrongPachCodeClassException(Throwable th) {
        super(th);
    }
}
