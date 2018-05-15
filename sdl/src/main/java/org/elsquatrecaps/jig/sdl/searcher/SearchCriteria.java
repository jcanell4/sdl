package org.elsquatrecaps.jig.sdl.searcher;

public class SearchCriteria {
    private String text;

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
}
