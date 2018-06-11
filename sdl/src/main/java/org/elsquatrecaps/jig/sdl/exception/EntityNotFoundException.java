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
public class EntityNotFoundException extends RuntimeException {

    /**
     * Creates a new instance of <code>EntityNotFoundException</code> without
     * detail message.
     */
    public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", entityName, fieldName, fieldValue.toString()));        
    }
}
