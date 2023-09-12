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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.elsquatrecaps.jig.sdl.exception.ErrorParsingDate;

/**
 *
 * @author josep
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BvphTypeGetRemoteProcess extends GetRemoteProcessWithUniqueKeys {
    @XmlTransient
    protected String textKey = "busq_general";
    @XmlTransient
    protected String paisKey = "descrip_idlistpais";
    @XmlTransient
    protected String bPaisKey = "busq_idlistpais";
    @XmlTransient
    protected String defaultPaisValue = "España";
    @XmlTransient
    protected String defaultBPaisValue = "DGB29840";
    @XmlTransient
    protected String ocrKey = "general_ocr";
    @XmlTransient
    protected String defaultOcrValue = "on";
    @XmlTransient
    protected String smallerYearKey = "busq_rango0_fechapubinicial__fechapubfinal";
    @XmlTransient
    protected String biggerYearKey = "busq_rango1_fechapubinicial__fechapubfinal";
    @XmlTransient
    protected String dateFormatKey = "formato_fechapublicacion";
    @XmlTransient
    protected String dateFormat = "dd/MM/yyyy";
    @XmlTransient
    protected int smallerYear = 1500;
    @XmlTransient
    protected int biggerYear = Calendar.getInstance().get(Calendar.YEAR);
    @XmlTransient
    protected String titleKey = "busq_titulo";

    public BvphTypeGetRemoteProcess() {
    }

    @Override
    public void setParam(String key, String value) {
        if (smallerYearKey.equals(key)) {
            smallerYear = getYearFromStringDate(value);
            if (getParam(dateFormatKey) == null) {
                setParam(dateFormatKey, dateFormat);
            }
        }
        if (biggerYearKey.equals(key)) {
            biggerYear = getYearFromStringDate(value);
            if (getParam(dateFormatKey) == null) {
                setParam(dateFormatKey, dateFormat);
            }
        }
        super.setParam(key, value);
    }

    public void setCriteria(SearchCriteria criteria) {
        BvphTypeSearchCriteria searchCriteria = (BvphTypeSearchCriteria) criteria;
        this.setText(criteria.getText());
        if (searchCriteria.getSmallerYear() != null && searchCriteria.getSmallerYear() > 0) {
            this.setSmallerYear(searchCriteria.getSmallerYear());
        } else if (searchCriteria.getDateStart() != null && !searchCriteria.getDateStart().isEmpty()) {
            this.setParam(this.smallerYearKey, searchCriteria.getDateStart());
        } else {
            this.setSmallerYear(smallerYear);
        }
        if (searchCriteria.getBiggerYear() != null && searchCriteria.getBiggerYear() > 0) {
            this.setBiggerYear(searchCriteria.getBiggerYear());
        } else if (searchCriteria.getDateEnd() != null && !searchCriteria.getDateEnd().isEmpty()) {
            this.setParam(this.biggerYearKey, searchCriteria.getDateEnd());
        } else {
            this.setBiggerYear(biggerYear);
        }
        if(criteria.hasTitle()){
            this.setTitle(criteria.getTitle());
        }
    }

    @Override
    public void setParams(Map<String, String> params) {
        this._setParams(params);
    }

    public int getDefaultBiggerYear() {
        return biggerYear;
    }

    public int getDefaultSmallerYear() {
        return smallerYear;
    }

    public void setText(String criteria) {
        this.setParam(textKey, criteria);
    }

    public void setTitle(String criteria) {
        this.setParam(titleKey, criteria);
    }

    public void setBiggerYear(int bigger) {
        if (this.getParam(biggerYearKey) == null) {
            this.setParam(biggerYearKey, String.format("31/12/%d", bigger));
        } else if (bigger < this.getYearFromStringDate(this.getParam(biggerYearKey))) {
            this.setParam(biggerYearKey, String.format("31/12/%d", bigger));
        } else {
            this.biggerYear = bigger;
        }
    }

    public void setBiggerData(String bigger) {
        if (this.getParam(biggerYearKey) == null) {
            this.setParam(biggerYearKey, bigger);
        } else if (this.compareDates(bigger, this.getParam(biggerYearKey)) < 0) {
            this.setParam(biggerYearKey, bigger);
        }
    }

    public void setSmallerYear(int smaller) {
        if (this.getParam(smallerYearKey) == null) {
            this.setParam(smallerYearKey, String.format("01/01/%d", smaller));
        } else if (smaller > this.getYearFromStringDate(this.getParam(smallerYearKey))) {
            this.setParam(smallerYearKey, String.format("01/01/%d", smaller));
        } else {
            this.smallerYear = smaller;
        }
    }

    public void setSmallerData(String smaller) {
        if (this.getParam(smallerYearKey) == null) {
            this.setParam(smallerYearKey, smaller);
        } else if (this.compareDates(smaller, this.getParam(smallerYearKey)) > 0) {
            this.setParam(smallerYearKey, smaller);
        }
    }

    protected void _initDefaultParams() {
        this.setParam(paisKey, defaultPaisValue);
        this.setParam(bPaisKey, defaultBPaisValue);
        this.setParam(ocrKey, defaultOcrValue);
    }

    protected void _setParams(Map<String, String> p) {
        Calendar cal = new GregorianCalendar();
        Map<String, String> params;
        params = new HashMap<>(p);
        if (params.containsKey(smallerYearKey)) {
            smallerYear = getYearFromStringDate(params.get(smallerYearKey));
        } else {
            params.put(smallerYearKey, String.format("01/01/%d", smallerYear));
        }
        if (params.containsKey(biggerYearKey)) {
            biggerYear = getYearFromStringDate(params.get(biggerYearKey));
        } else {
            params.put(biggerYearKey, String.format("31/12/%d", biggerYear));
        }
        if (params.containsKey(dateFormatKey)) {
            dateFormat = params.get(dateFormatKey);
        } else {
            params.put(dateFormatKey, dateFormat);
        }
        super.setParams(params);
    }

    protected int compareDates(String date1, String date2) {
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();
        SimpleDateFormat fr = new SimpleDateFormat("dd/mm/yyyy");
        try {
            cal1.setTime(fr.parse(date1));
            cal2.setTime(fr.parse(date2));
        } catch (ParseException ex) {
            throw new ErrorParsingDate(ex);
        }
        return cal1.compareTo(cal2);
    }

    protected int getYearFromStringDate(String date) {
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
