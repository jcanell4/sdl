package org.elsquatrecaps.jig.sdl.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author josep
 */

@Entity
@Access(AccessType.FIELD)
public class CalcDateMap implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @Column(length = 3)
    private String id;
    private String desc;

    public CalcDateMap() {        
    }

    public CalcDateMap(String id) {        
        this.id = id;
    }

    public CalcDateMap(String id, String value) {
        this.id = id;
        this.desc = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String value) {
        this.desc = value;
    }
}
