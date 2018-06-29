/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.elsquatrecaps.jig.sdl.exception.ErrorParsingDate;
import org.jsoup.nodes.Element;

@XmlRootElement
@XmlType()
public class BvphGetRemoteProcess extends GetRemoteProcess{
    @XmlElement
    private String textKey = "busq_general";
    @XmlElement
    private String paisKey = "descrip_idlistpais";
    @XmlElement
    private String defaultPaisValue = "España";
    @XmlElement
    private String ocrKey = "general_ocr";
    @XmlElement
    private String defaultOcrValue = "on";
    @XmlElement
    private String smallerYearKey = "busq_rango0_fechapubinicial__fechapubfinal";
    @XmlElement
    private String biggerYearKey = "busq_rango1_fechapubinicial__fechapubfinal";
    @XmlElement
    private String dateFormatKey = "formato_fechapublicacion";
    @XmlElement
    private String dateFormat = "dd/MM/yyyy";
    @XmlElement
    private int smallerYear = 1500;
    @XmlElement
    private int biggerYear = Calendar.getInstance().get(Calendar.YEAR);
    @XmlElement
    private String url ="http://prensahistorica.mcu.es/es/consulta/resultados_ocr.cmd";


    public BvphGetRemoteProcess(){   
        super.setUrl(url);
        this._initDefaultParams();
    }
    
    public BvphGetRemoteProcess(BvphSearchCriteria criteria){
        super.setUrl(url);
        this._initDefaultParams();
        this.setCriteria(criteria);
    }

    public BvphGetRemoteProcess(Map<String, String> params){
        super.setUrl(url);
        this._initDefaultParams();
        this._setParams(params);
    }

    @Override
    public void setParam(String key, String value) {
        if(smallerYearKey.equals(key)){
            smallerYear=getYearFromStringDate(value);
            if(getParam(dateFormatKey)==null){
                setParam(dateFormatKey, dateFormat);
            }
        }
        if(biggerYearKey.equals(key)){
            biggerYear=getYearFromStringDate(value);
            if(getParam(dateFormatKey)==null){
                setParam(dateFormatKey, dateFormat);
            }
        }
        super.setParam(key, value);
    }

    
    public void setCriteria(SearchCriteria criteria){
        BvphSearchCriteria bvphSearchCriteria = (BvphSearchCriteria) criteria;
        this.setText(criteria.getText());
        if(bvphSearchCriteria.getSmallerYear()!=null
                && bvphSearchCriteria.getSmallerYear()>0){
            this.setSmallerYear(bvphSearchCriteria.getSmallerYear());
        }else if(bvphSearchCriteria.getDateStart()!=null
                && !bvphSearchCriteria.getDateStart().isEmpty()){
            this.setParam(this.smallerYearKey, bvphSearchCriteria.getDateStart());
        }else{
            this.setSmallerYear(smallerYear);
        }
        if(bvphSearchCriteria.getBiggerYear()!=null
                && bvphSearchCriteria.getBiggerYear()>0){
            this.setBiggerYear(bvphSearchCriteria.getBiggerYear());
        }else if(bvphSearchCriteria.getDateEnd()!=null
                && !bvphSearchCriteria.getDateEnd().isEmpty()){
            this.setParam(this.biggerYearKey, bvphSearchCriteria.getDateEnd());
        }else{
            this.setBiggerYear(biggerYear);
        }
    }
    
    @Override
    public void setParams(Map<String, String> params){
        this._setParams(params);
    }
    
    public int getDefaultBiggerYear(){
        return biggerYear;
    }

    public int getDefaultSmallerYear(){
        return smallerYear;
    }

    public void setText(String criteria){
        this.setParam(textKey, criteria);
    }

    public void setBiggerYear(int bigger){
        if(this.getParam(biggerYearKey)==null){
            this.setParam(biggerYearKey, String.format("31/12/%d", bigger));
        }else if(bigger<this.getYearFromStringDate(this.getParam(biggerYearKey))){
            this.setParam(biggerYearKey, String.format("31/12/%d", bigger));
        }else{
            this.biggerYear = bigger;
        }
    }

    public void setSmallerYear(int smaller){
        if(this.getParam(smallerYearKey)==null){
            this.setParam(smallerYearKey, String.format("01/01/%d", smaller));
        }else if(smaller>this.getYearFromStringDate(this.getParam(smallerYearKey))){
            this.setParam(smallerYearKey, String.format("01/01/%d", smaller));
        }else{
            this.smallerYear = smaller;
        }
    }

    @Override
    public Element get() {
        if(super.getUrl()==null){
            super.setUrl(url);
        }
        return super.get();
    }
    
    
    private void _initDefaultParams(){
        this.setParam(paisKey, defaultPaisValue);
        this.setParam(ocrKey, defaultOcrValue);
    }
    
    private void _setParams(Map<String, String> p){
        Calendar cal = new GregorianCalendar();
        Map<String, String> params;
        params = new HashMap<>(p);
        if(params.containsKey(smallerYearKey)){
            smallerYear=getYearFromStringDate(params.get(smallerYearKey));
        }else{
            params.put(smallerYearKey, String.format("01/01/%d", smallerYear));
        }
        if(params.containsKey(biggerYearKey)){
            biggerYear=getYearFromStringDate(params.get(biggerYearKey));
        }else{
            params.put(biggerYearKey, String.format("31/12/%d", biggerYear));
        }
        if(params.containsKey(dateFormatKey)){
            dateFormat=params.get(dateFormatKey);
        }else{
            params.put(dateFormatKey, dateFormat);
        }
        super.setParams(params);
    }
    
    private int getYearFromStringDate(String date){
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat fr = new SimpleDateFormat("dd/mm/yyyy");
        try {
            cal.setTime(fr.parse(date));
        } catch (ParseException ex) {
            throw new ErrorParsingDate(ex);
        }
        return cal.get(Calendar.YEAR);
    }
}
