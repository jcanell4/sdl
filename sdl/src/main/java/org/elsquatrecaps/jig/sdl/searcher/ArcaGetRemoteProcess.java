/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.jsoup.nodes.Element;

@XmlRootElement
@XmlType()
public class ArcaGetRemoteProcess extends GetRemoteProcessWithUniqueKeys{
    @XmlElement
    private String searchtermKey = "searchterm";
    @XmlElement
    private String fieldKey = "field";
    @XmlElement
    private String textField = "all";
    @XmlElement
    private String exactTextField = "all";
    @XmlElement
    private String dateField = "date";
    @XmlElement
    private String modeKey = "mode";
    @XmlElement
    private String textMode = "all";
    @XmlElement
    private String exactTextMode = "exact";
    @XmlElement
    private String dateMode = "exact";
    @XmlElement
    private String connKey = "conn";
    @XmlElement
    private String textConnAnd = "and";
    @XmlElement
    private String exactTextConnAnd = "and";
    @XmlElement
    private String textConnOr = "or";
    @XmlElement
    private String exactTextConnOr = "or";
    @XmlElement
    private String dateConn = "and";
    @XmlElement
    private String orderKey = "order";
    @XmlElement
    private String orderDefaultValue = "date";
    @XmlElement
    private String separator = "!";
    @XmlElement
    private String url ="http://mdc2.cbuc.cat/cdm/search";

    @XmlTransient
    private List<ArcaParam> arcaParams=new ArrayList<>();

    public ArcaGetRemoteProcess(){   
        super.setUrl(url);
    }
    
    public ArcaGetRemoteProcess(ArcaSearchCriteria criteria){
        super.setUrl(url);
        this.setCriteria(criteria);
    }

    public ArcaGetRemoteProcess(Map<String, String> params){
        super.setUrl(url);
        this._setParams(params);
    }

    @Override
    public void setParam(String key, String value) {
        ArcaParam param;
        if(dateField.equalsIgnoreCase(key)){
            int ind=this.arcaParams.size()-1;
            while(ind>=0 && !(this.arcaParams.get(ind) instanceof ArcaDateParam)){
                ind--;
            }
            if(ind>=0 && !((ArcaDateParam) (this.arcaParams.get(ind))).isFull()){
                ((ArcaDateParam) (this.arcaParams.get(this.arcaParams.size()-1))).setDateEnd(value);
            }else{
                param = new ArcaDateParam(value);            
                this.arcaParams.add(param);
            }
        }else{
            if(value.startsWith("\"") && value.endsWith("\"")){
                
            }else{
                String[] words = value.split("\\s+");
                for(String word:words){
                    param = new ArcaTextParam(word);
                    this.arcaParams.add(param);
                }
            }
        }
    }

    
    public void setCriteria(SearchCriteria criteria){
        ArcaSearchCriteria arcaSearchCriteria = (ArcaSearchCriteria) criteria;
        this.setText(criteria.getText());
        if(arcaSearchCriteria.getDateStart()!=null
                && !arcaSearchCriteria.getDateStart().isEmpty()){
            this.setParam(dateField, arcaSearchCriteria.getDateStart());
//        }else{
//            this.setSmallerYear(smallerYear);
        }
        if(arcaSearchCriteria.getDateEnd()!=null
                && !arcaSearchCriteria.getDateEnd().isEmpty()){
            this.setParam(dateField, arcaSearchCriteria.getDateEnd());
//        }else{
//            this.setBiggerYear(biggerYear);
        }
    }
    
    @Override
    public void setParams(Map<String, String> params){
        this._setParams(params);
    }

    public void setParams(List<ArcaParam> params){
        this._setParams(params);
    }
    
    @XmlTransient
    public void setText(String criteria){
        this.setParam(textField, criteria);
    }

    public String _getText(){
        StringBuilder retSerchTerm = new StringBuilder();
        String sep="";
        
        for(int i=0; i<arcaParams.size(); i++ ){
            if(i==1){
               sep = "|";
            }
            retSerchTerm.append(sep);
            retSerchTerm.append(arcaParams.get(i).getSearchterm());
        }
        return retSerchTerm.toString();
    }

    @Override
    public Element get() {
        if(super.getUrl()==null){
            super.setUrl(url);
        }
        return super.get();
    }
    
    public String getQueryPath(){
        StringBuilder ret = new StringBuilder();
        StringBuilder retSerchTerm = new StringBuilder();
        StringBuilder retField = new StringBuilder();
        StringBuilder retMode = new StringBuilder();
        StringBuilder retCon = new StringBuilder();
        String sep="";
        
        for(int i=0; i<arcaParams.size(); i++ ){
            if(i==1){
               sep = this.separator;
            }
            retSerchTerm.append(sep);
            retSerchTerm.append(arcaParams.get(i).getSearchterm());
            retField.append(sep);
            retField.append(arcaParams.get(i).getField());
            retMode.append(sep);
            retMode.append(arcaParams.get(i).getMode());
            retCon.append(sep);
            retCon.append(arcaParams.get(i).getConn());
        }
        ret.append("/");
        ret.append(this.searchtermKey);
        ret.append("/");
        ret.append(retSerchTerm.toString());
        ret.append("/");
        ret.append(this.fieldKey);
        ret.append("/");
        ret.append(retField.toString());
        ret.append("/");
        ret.append(this.modeKey);
        ret.append("/");
        ret.append(retMode.toString());
        ret.append("/");
        ret.append(this.connKey);
        ret.append("/");
        ret.append(retCon.toString());
        ret.append("/");
        ret.append(this.orderKey);
        ret.append("/");
        ret.append(this.orderDefaultValue);
        
        return ret.toString();
    }
//    
//    private void _initDefaultParams(){
//
//    }
    
    private void _setParams(List<ArcaParam> p){
        for (ArcaParam arcaParam : p) {
            this.arcaParams.add(arcaParam);
        }
    }
    
    private void _setParams(Map<String, String> p){
        Calendar cal = new GregorianCalendar();
        ArcaGetRemoteProcess self = this;
        p.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String key, String value) {
                self.setParam(key, value);
            }
        });
    }
    
    private class ArcaParam{
        private String searchterm;
        private String field;
        private String mode;
        private String conn;

        public ArcaParam(String searchterm, String field, String mode, String conn) {
            this.searchterm = searchterm;
            this.field = field;
            this.mode = mode;
            this.conn = conn;
        }

        public String getSearchterm() {
            return searchterm;
        }

        public String getField() {
            return field;
        }

        public String getMode() {
            return mode;
        }

        public String getConn() {
            return conn;
        }
        
        protected void setSearchTerm(String term){
            this.searchterm = term;
        }
    }
    
    private class ArcaExactTextParam extends ArcaParam{

        public ArcaExactTextParam(String text) {
            super(text, exactTextField, exactTextMode, exactTextConnAnd);
        }
    }

    private class ArcaTextParam extends ArcaParam{

        public ArcaTextParam(String text) {
            super(text, textField, textMode, textConnAnd);
        }
    }

    private class ArcaDateParam extends ArcaParam{
        private boolean full = false;
        
        public ArcaDateParam(String dateIni, String dateEnd) {
            super("", dateField, dateMode, dateConn);
            String pDate;
            String[] adate = dateIni.split("/");
            pDate = adate[2].concat(adate[0]).concat(adate[1]);
            
            adate = dateEnd.split("/");
            pDate = pDate.concat("-").concat(adate[2].concat(adate[0]).concat(adate[1]));
            full = true;
            this.setSearchTerm(pDate);            
        }
        
        public ArcaDateParam(String date) {
            super("", dateField, dateMode, dateConn);
            String pDate;
            if(date.indexOf("-")==-1){
                String[] adate = date.split("/");
                pDate = adate[2].concat(adate[0]).concat(adate[1]);
            }else{
                full = true;
                String[] period = date.split("-");
                String[] adate = period[0].split("/");
                pDate = adate[2].concat(adate[0]).concat(adate[1]);
                
                adate = period[1].split("/");
                pDate = pDate.concat("-").concat(adate[2].concat(adate[0]).concat(adate[1]));
            }
            this.setSearchTerm(pDate);
        }

        public boolean isFull() {
            return full;
        }
        
        public void setDateEnd(String date){
            String pDate;
            String[] adate = date.split("/");
            pDate = this.getSearchterm().concat("-").concat(adate[2].concat(adate[0]).concat(adate[1]));
            this.setSearchTerm(pDate);
            full = true;
        }
    }
}
