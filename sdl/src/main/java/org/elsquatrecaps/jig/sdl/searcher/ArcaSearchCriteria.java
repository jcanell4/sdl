package org.elsquatrecaps.jig.sdl.searcher;

public final class ArcaSearchCriteria extends SearchCriteria{
    private String dateStart=null;
    private String dateEnd=null;

    public ArcaSearchCriteria() {
    }
    
    public ArcaSearchCriteria(String text){
        super(text);
    }

    public ArcaSearchCriteria(String text, String dateStart, String dateEnd){
        super(text);
        setDateStart(dateStart);
        setDateEnd(dateEnd);
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
