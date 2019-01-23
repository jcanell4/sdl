/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.jsoup.nodes.Element;

@XmlRootElement
@XmlType()
public class HdGetRemoteProcess extends GetRemoteProcessWithRepeatedKeys{
    @XmlTransient
    private String textField = "text";
    @XmlTransient
    private String fromDateField = "from_date";
    @XmlTransient
    private String toDateField = "to_date";
    @XmlElement
    private String defaultFromDate = "01/01/1600";
    @XmlElement
    private String url ="http://hemerotecadigital.bne.es/results.vm";

    @XmlTransient
    private HdParam hdParams=new HdParam();
    @XmlTransient
    private boolean parsedParams=false;

    public HdGetRemoteProcess(){   
        super.setUrl(url);
    }
    
    public HdGetRemoteProcess(HdSearchCriteria criteria){
        super.setUrl(url);
        this.setCriteria(criteria);
    }

    public HdGetRemoteProcess(List<String> params){
        super.setUrl(url);
        this._setParams(params);
    }

    @Override
    public void setParam(String key, String value) {
        if(fromDateField.equalsIgnoreCase(key)){
            hdParams.setFromDate(value);
        }else if(toDateField.equalsIgnoreCase(key)){
            hdParams.setToDate(value);
        }else{
//            value = value.replaceAll(" ", "+");
            hdParams.setText(value);
        }
        parsedParams = false;
    }

    public void setCriteria(SearchCriteria criteria){
        HdSearchCriteria hdSearchCriteria = (HdSearchCriteria) criteria;
        this.hdParams = new HdParam();
        this.setText(criteria.getText());
        if(hdSearchCriteria.getDateStart()!=null
                && !hdSearchCriteria.getDateStart().isEmpty()){
            this.setParam(fromDateField, hdSearchCriteria.getDateStart());
        }
        if(hdSearchCriteria.getDateEnd()!=null
                && !hdSearchCriteria.getDateEnd().isEmpty()){
            this.setParam(toDateField, hdSearchCriteria.getDateEnd());
        }
    }
    
    public void setParams(List<String> params){
        this._setParams(params);
    }

    @XmlTransient
    public void setText(String criteria){
        this.setParam(textField, criteria);
    }

    @Override
    public Element get() {
        if(super.getUrl()==null){
            super.setUrl(url);
        }
        return super.get();
    }
    
    @Override
    public List<String> getParams(){
        if(!parsedParams){
            super.setParams(this.hdParams.getParams());
        }
        return super.getParams();
    }
    
    private void _setParams(List<String> p){
        for(int i=0; i<p.size(); i+=2){
            super.setParam(p.get(i), p.get(i+1));
        }
    }

    private class HdParam{
        private String text;
        private String fromDate=null;
        private String toDate=null;

        public HdParam() {
        }

        public HdParam(String text, String fromDate, String toDate) {
            this.text = text;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public HdParam(HdSearchCriteria sc) {
            this.text = sc.getText();
            this.fromDate = sc.getDateStart();
            this.toDate = sc.getDateEnd();
        }

        public String getText() {
            return text;
        }

        public List<String> getDate() {
            String date;
            List<String> list = new ArrayList<>();
            list.add("d");
            list.add("creation");
            date = fromDate;
            if(date==null){
                date = defaultFromDate;
            }
            list.addAll(this._getDate(date));
            date = toDate;
            if(date==null){
                date = "31/12/".concat(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            }
            list.addAll(this._getDate(date));
            return list;
        }

        private List<String> _getDate(String date) {
            List<String> list = new ArrayList<>();
            String[] adate = date.split("/");
            for(int i=2; i>=0; i--){
                list.add("d");
                list.add(adate[i]);
            }
            return list;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public List<String> getParams(){
            List<String> ret = new ArrayList();
            
            ret.add("o");
            ret.add("");
            ret.add("w");            
            ret.add(this.getText());
            ret.add("f");
            ret.add("text");
            ret.addAll(this.getDate());
            ret.add("b");
            ret.add("search-visible");
            ret.add("t");
            ret.add("+creation");
            ret.add("l");
            ret.add("600");
            ret.add("l");
            ret.add("700");
            ret.add("s");
            ret.add("0");
            ret.add("lang");
            ret.add("ca");
            return ret;
        }
    }
}
