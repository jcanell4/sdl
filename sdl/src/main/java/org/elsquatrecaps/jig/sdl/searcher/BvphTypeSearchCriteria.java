/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

/**
 *
 * @author josep
 */
public class BvphTypeSearchCriteria extends SearchCriteria {
    protected Integer smallerYear = null;
    protected Integer biggerYear = null;
    protected String dateStart = null;
    protected String dateEnd = null;

    public BvphTypeSearchCriteria() {
    }

    public BvphTypeSearchCriteria(String text) {
        super(text);
    }

    public Integer getSmallerYear() {
        return smallerYear;
    }

    public void setSmallerYear(Integer smallerYear) {
        this.smallerYear = smallerYear;
    }

    public Integer getBiggerYear() {
        return biggerYear;
    }

    public void setBiggerYear(Integer biggerYear) {
        this.biggerYear = biggerYear;
    }

    /**
     * @return the dateStart
     */
    public String getDateStart() {
        return dateStart;
    }

    /**
     * @param dateStart the dateStart to set
     */
    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * @return the dateEnd
     */
    public String getDateEnd() {
        return dateEnd;
    }

    /**
     * @param dateEnd the dateEnd to set
     */
    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }
    
}
