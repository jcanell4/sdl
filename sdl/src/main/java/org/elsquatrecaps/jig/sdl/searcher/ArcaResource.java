package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

public class ArcaResource extends SearcherResource{
    private static final String[] SUPPORTED_FORMATS={"pdf"};
    private String editionDateFilter;
    private String pdfUrl;
    private String fragmentsFilter;
    private String textKey;
    private String savePdfFilter;

    public ArcaResource() {
    }
    
    public ArcaResource(String editionDateFilter, String fragmentsFilter, String textKey, String savePdfFilter) {
        this.editionDateFilter = editionDateFilter;
        this.fragmentsFilter = fragmentsFilter;
        this.textKey = textKey.replaceAll("\"", "").replaceAll("( )+", "|");
        this.savePdfFilter = savePdfFilter;
    }
    
    public void updateFromElement(Element basicInfoElem, Element contentDocum, String context, Map<String, String> cookies){
        setPublicationId(basicInfoElem.attr("itemcoll"));
        setTitle(basicInfoElem.text().trim());
        setPageId(basicInfoElem.attr("item_id"));
        setPage("document complet");
        setEditionDate(contentDocum.selectFirst(this.editionDateFilter).text().trim());
        Element elementText = contentDocum.selectFirst(this.fragmentsFilter);
        saveFragments(elementText.text());
        pdfUrl = GetRemoteProcess.relativeToAbsoluteUrl(context, contentDocum.selectFirst(savePdfFilter).attr("href"));
    }
    
    private void saveFragments(String text){
        Pattern p = Pattern.compile(".{0,100}(".concat(textKey).concat(").{0,100}"), Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
        Matcher m = p.matcher(text);
        while(m.find()){
            addFragment(text.substring(m.start(), m.end()));
        }
    }
    
//    private String getDateFromDbiOrTitle(Element dateElement){
//        String ret = null;
//        if(dateElement==null){
//            ret = getDateFromTitle();
//        }else{
//            ret = getDateFromDbi(dateElement.text());
//        }
//        return ret;
//    }
//    
//    private String getDateFromTitle(){
//        String ret="00/00/0000";
////        Pattern pattern = Pattern.compile(patterToExtractDateFromTitle);
////        Matcher matcher = pattern.matcher(this.getTitle());
////        if(matcher.find()){
////            ret = matcher.group(1);
////        }
//        return ret;
//    }
//    
//    private String getDateFromDbi(String bdi){
//        String ret="00/00/0000";
//        Pattern pattern = Pattern.compile(".*(\\d{2}[/-]\\d{2}[/-]\\d{4}).*");
//        Matcher matcher = pattern.matcher(bdi);
//        if(matcher.find()){
//            ret = matcher.group(1);
//        }
//        return ret;
//    }
//    
//    private Element getToDownloading(String url){
//        GetRemoteProcess grp = new GetRemoteProcess(url);
//        grp.setParam("aceptar", "Aceptar");
//        Element toDonwloading = grp.get();
//        return toDonwloading;
//    }
//            
//            
    @Override
    public FormatedFile getFormatedFile() {
        return getStrictFormatedFile("pdf");
    }

    @Override
    protected boolean isFormatSupported(String format) {
        boolean ret=false;
        for(int i=0; !ret && i<SUPPORTED_FORMATS.length; i++){
            ret = SUPPORTED_FORMATS[i].equalsIgnoreCase(format);
        }
        return ret;
    }

    @Override
    protected FormatedFile getStrictFormatedFile(String format) {
        String urlFile = format.equals("pdf")?pdfUrl:null;
        return getFormatedFileInstance(urlFile, format, getPublicationId() + "_" + getPageId(), getPublicationId() + "_" + getPageId() + "." +format);
    }
    
    protected FormatedFile getFormatedFileInstance(String url, String format, String name, String fileName){
        FormatedFile ret;
        ret = new BasicSearcherFormatedFile(url, format, getPublicationId() + "_" + getPageId(), getPublicationId() + "_" + getPageId() + "." +format);
        return ret;
    }

    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }
}
