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
public class MandatoryPatchingBean {    
    public void patch(){
        PatchSDLDB patcher = new PatchSDLDB();
        if(!patcher.isSDLInstalled()){
            patcher.patchAllMandatory();
        }
    }    
}
