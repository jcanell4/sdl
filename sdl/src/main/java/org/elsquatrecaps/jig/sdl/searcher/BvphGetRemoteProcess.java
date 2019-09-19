/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class BvphGetRemoteProcess extends BvphTypeGetRemoteProcess{
    private static final String URL = "https://prensahistorica.mcu.es/es/consulta/resultados_ocr.cmd";

    public BvphGetRemoteProcess(){ 
        super.setUrl(URL);
        this._initDefaultParams();
    }
    
    public BvphGetRemoteProcess(BvphSearchCriteria criteria){
        super.setUrl(URL);
        this._initDefaultParams();
        this.setCriteria(criteria);
    }

    public BvphGetRemoteProcess(Map<String, String> params){
        super.setUrl(URL);
        this._initDefaultParams();
        this._setParams(params);
    }
}
