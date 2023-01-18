/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.elsquatrecaps.jig.sdl.persistence.patcher;

import java.sql.Connection;
import java.util.Properties;

/**
 *
 * @author josepcanellas
 */
public abstract class PatchCodeClassWithDBConnection extends PatchCodeClass{
    protected Connection conn;
    
    public PatchCodeClassWithDBConnection(Integer version, Properties properties){
        super(version, properties);
    }
    
    public void init(Connection con){
        this.conn = con;
    }
    
    public abstract void run();
}
