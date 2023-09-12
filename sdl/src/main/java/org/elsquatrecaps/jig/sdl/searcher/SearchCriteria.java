package org.elsquatrecaps.jig.sdl.searcher;

public class SearchCriteria {
    private String text;
    private String title=null;

    public SearchCriteria() {
    }

    public SearchCriteria(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean hasTitle(){
        return title!=null && !title.isEmpty();
    }
}
