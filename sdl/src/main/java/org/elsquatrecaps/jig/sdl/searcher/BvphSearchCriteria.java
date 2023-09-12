package org.elsquatrecaps.jig.sdl.searcher;

public final class BvphSearchCriteria extends BvphTypeSearchCriteria{

    public BvphSearchCriteria() {
    }
    
    public BvphSearchCriteria(String text){
        super(text);
    }

    public BvphSearchCriteria(String text, String tit){
        super(text);
        this.setTitle(tit);
    }

    public BvphSearchCriteria(String text, String dateStart, String dateEnd){
        super(text);
        setDateStart(dateStart);
        setDateEnd(dateEnd);
    }
    
    public BvphSearchCriteria(String text, String dateStart, String dateEnd, String tit){
        super(text);
        setDateStart(dateStart);
        setDateEnd(dateEnd);
        setTitle(tit);
    }
    
    public BvphSearchCriteria(String text, Integer smaller, Integer bigger){
        super(text);
        setSmallerYear(smaller);
        setBiggerYear(bigger);
    }

    public BvphSearchCriteria(String text, Integer smaller, Integer bigger, String tit){
        super(text);
        setSmallerYear(smaller);
        setBiggerYear(bigger);
        setTitle(tit);
    }
}
