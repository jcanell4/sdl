/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType()
public class ArcaGetRemoteProcess  extends BvphTypeGetRemoteProcess{
    private static final String URL = "https://arca.bnc.cat/arcabib_pro/ca/consulta/resultados_ocr.do";

    public ArcaGetRemoteProcess(){ 
        super.setUrl(URL);
        this._initDefaultParams();
    }
    
    public ArcaGetRemoteProcess(ArcaSearchCriteria criteria){
        super.setUrl(URL);
        this._initDefaultParams();
        this.setCriteria(criteria);
    }

    public ArcaGetRemoteProcess(Map<String, String> params){
        super.setUrl(URL);
        this._initDefaultParams();
        this._setParams(params);
    }

    protected void _initDefaultParams() {
        this.setParam(ocrKey, defaultOcrValue);
    }
}
