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
public class ErrorClosingDatabaseException extends RuntimeException {

    /**
     * Creates a new instance of <code>ErrorStartingDatabaseException</code>
     * without detail message.
     */
    public ErrorClosingDatabaseException() {
    }

    public ErrorClosingDatabaseException(Throwable th) {
        super(th);
    }

    /**
     * Constructs an instance of <code>ErrorStartingDatabaseException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ErrorClosingDatabaseException(String msg) {
        super(msg);
    }

    public ErrorClosingDatabaseException(String msg, Throwable th) {
        super(msg, th);
    }
}
