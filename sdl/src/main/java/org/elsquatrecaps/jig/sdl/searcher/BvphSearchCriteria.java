package org.elsquatrecaps.jig.sdl.searcher;

public final class BvphSearchCriteria extends SearchCriteria{
    private Integer smallerYear=null;
    private Integer biggerYear=null;
    private String dateStart=null;
    private String dateEnd=null;

    public BvphSearchCriteria() {
    }
    
    public BvphSearchCriteria(String text){
        super(text);
    }

    public BvphSearchCriteria(String text, String dateStart, String dateEnd){
        super(text);
        setDateStart(dateStart);
        setDateEnd(dateEnd);
    }
    
    public BvphSearchCriteria(String text, Integer smaller, Integer bigger){
        super(text);
        setSmallerYear(smaller);
        setBiggerYear(bigger);
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
