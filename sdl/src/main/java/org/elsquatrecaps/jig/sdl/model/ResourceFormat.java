/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.model;

import java.io.Serializable;
import java.util.Map.Entry;
import javax.persistence.Embeddable;

/**
 *
 * @author josep cañellas
 */
@Embeddable
public class ResourceFormat implements Serializable {

    private static final long serialVersionUID = 1L;
    private String format;
    private String contentType;

    public ResourceFormat() {
    }

    public ResourceFormat(String format, String contentType) {
        this.format = format;
        this.contentType = contentType;
    }

    public String getFormat() {
        return format;
    }

    public String getContentType() {
        return contentType;
    } 

    public void setContentType(String contentType) {
        this.contentType = contentType;
    } 

    @Override
    public boolean equals(Object o) {
        return this.format.equalsIgnoreCase(((ResourceFormat) o).format);
    }
}
