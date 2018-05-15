package org.elsquatrecaps.jig.sdl.searcher;

public class BvphSearchCriteria extends SearchCriteria{
    private Integer smallerYear=null;
    private Integer biggerYear=null;

    public BvphSearchCriteria() {
    }
    
    public BvphSearchCriteria(String text){
        super(text);
    }

    public BvphSearchCriteria(String text, Integer smaller, Integer bigger){
        super(text);
        setSmallerYear(smallerYear);
        setBiggerYear(biggerYear);
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
}
