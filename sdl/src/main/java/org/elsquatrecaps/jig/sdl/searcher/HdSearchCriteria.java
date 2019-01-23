package org.elsquatrecaps.jig.sdl.searcher;

public final class HdSearchCriteria extends SearchCriteria{
    private String dateStart=null;
    private String dateEnd=null;

    public HdSearchCriteria() {
    }
    
    public HdSearchCriteria(String text){
        super(text);
    }

    public HdSearchCriteria(String text, String dateStart, String dateEnd){
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

//http://hemerotecadigital.bne.es/results.vm?o=&w=marinero+capitan&f=text&t=%2Bcreation&l=600&l=700&s=0&lang=ca