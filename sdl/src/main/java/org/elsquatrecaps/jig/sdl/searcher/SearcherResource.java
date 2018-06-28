package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public abstract class SearcherResource{
    private String title;
    private String page;
    private String publicationId;
    private String pageId;
    private String editionDate;
    private ArrayList<String> fragments= new ArrayList<String>();

  
    public String getFileName(){
        StringBuilder strBuffer = new StringBuilder();
        String locTitle;
        Pattern pattern1 = Pattern.compile(
                              "[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
        Pattern pattern2 = Pattern.compile(
                              "[+-.:,;<>\\{\\}\\[\\]\\*\\^\\¿\\?\\=\\)\\(\\/\\&\\%ºº$\\·\\#\\@\\|\\\\!\"]+");
        if(editionDate!=null && !editionDate.isEmpty() && editionDate.matches("[0-9]{2}\\/[0-9]{2}\\/[0-9]{2,4}")){            
            String[] aDate = editionDate.split("\\/");
            strBuffer.append(aDate[2]);
            strBuffer.append("_");
            strBuffer.append(aDate[1]);
            strBuffer.append("_");
            strBuffer.append(aDate[0]);
        }else if(editionDate!=null && !editionDate.isEmpty() && editionDate.matches("[0-9]{4}.+?[0-9]{2}")){            
            strBuffer.append(editionDate.substring(0, 4));
            strBuffer.append("_00_00");
        }else{
            strBuffer.append("0000_00_00");
        }
        strBuffer.append(publicationId);
        strBuffer.append("_");
        strBuffer.append(pageId);
        locTitle = Normalizer.normalize(this.title, Normalizer.Form.NFD);
        locTitle = pattern1.matcher(locTitle).replaceAll("");
        locTitle = pattern2.matcher(locTitle).replaceAll("");
        String[] words = locTitle.split(" ");
        if(words.length>0){
            strBuffer.append("_");
        }
        for (String word : words){
            if(word.length()>0){
                strBuffer.append(word.substring(0, 1).toUpperCase());
                strBuffer.append(word.substring(1).toLowerCase());
            }
        }
        return strBuffer.toString().substring(0,60);
    }
    
    
    public String getPublicationId() {
        return publicationId;
    }

    public String getTitle() {
        return title;
    }

    public String getPage() {    

        return page;
    }

    public String getPageId() {
        return pageId;
    }

    public String[] getFragments() {
        String[] ret = new String[fragments.size()];
        return fragments.toArray(ret);
    }

    public String getFragment(int idx) {
        return fragments.get(idx);
    }

    protected void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    protected void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void addFragment(String fragment) {
        this.fragments.add(fragment);
    }

    public void removeAllFragments() {
        this.fragments.clear();
    }
    
    public void addAllFragments(String[] fragment) {
        for(String frg: fragment){
            this.fragments.add(frg);
        }
    }

    public void setEditionDate(String editionDate) {
        this.editionDate = editionDate;
    }
    
    public String getEditionDate() {
        return this.editionDate;
    }    
    
    protected String[] getAlternativeFormats(String format) {
        return FormatedResourceUtils.getAlternativeFormats(format);
    }

    protected boolean isFormatSupported(String format) {
        return FormatedResourceUtils.isFormatSupported(format, getSupportedFormats());
    }

    public abstract String[] getSupportedFormats();

    public abstract FormatedFile getFormatedFile();

    public FormatedFile getFormatedFile(String format) {
        String formatDef = FormatedResourceUtils.getFormat(format, getSupportedFormats());
        return getStrictFormatedFile(formatDef);
    }

    protected abstract FormatedFile getStrictFormatedFile(String format);    
}
