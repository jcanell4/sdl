package org.elsquatrecaps.jig.sdl.searcher;

public final class ArcaSearchCriteria extends BvphTypeSearchCriteria{

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
    
    public ArcaSearchCriteria(String text, Integer smaller, Integer bigger){
        super(text);
        setSmallerYear(smaller);
        setBiggerYear(bigger);
    }
}
