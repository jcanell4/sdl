package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteData;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import static org.elsquatrecaps.jig.sdl.searcher.SearcherResource.logger;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;

public class ArcaResource extends SearcherResource{
    private static final String[] SUPPORTED_FORMATS={"pdf"};
    private String editionDateFilter;
    private String pdfUrl;
    private String fragmentsFilter;
    private String textKey;
    private String savePdfFilter;
    private String noPdfUrl;

    public ArcaResource() {
    }
    
    public ArcaResource(String editionDateFilter, String fragmentsFilter, String textKey, String savePdfFilter, String noPdfFileUrl) {
        this.editionDateFilter = editionDateFilter;
        this.fragmentsFilter = fragmentsFilter;
        this.textKey = textKey.replaceAll("\"", "").replaceAll("( )+", "|");
        this.savePdfFilter = savePdfFilter;
        this.noPdfUrl = noPdfFileUrl;
    }
    
    public void updateFromElement(Element basicInfoElem, Element contentDocum, String context, Map<String, String> cookies){
        try{
            setPublicationId(Utils.buildNormalizedFilename(basicInfoElem.attr("itemcoll")));
            setTitle(basicInfoElem.text().trim());
            setPageId(basicInfoElem.attr("item_id"));
            setPage("document complet");
            setEditionDate(getDateFromDateEditonOrTitle(contentDocum.selectFirst(this.editionDateFilter)));
            Element elementText = contentDocum.selectFirst(this.fragmentsFilter);
            if(elementText!=null){
                saveFragments(elementText.text());
            }
            //ALERTA DE VEGADES EL FORMAT ÉS JPG!
            Element pdfElement = contentDocum.selectFirst(savePdfFilter);
            if(pdfElement==null){
                throw new ErrorGettingRemoteData("Error. Nonexistent pdf for: ".concat(this.getTitle()));
            }else{
                pdfUrl = GetRemoteProcess.relativeToAbsoluteUrl(context, pdfElement.attr("src"));
            }
        }catch(Exception ex){
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
        }
        
    }
    
    private void saveFragments(String text){
        Pattern p = Pattern.compile(".{0,100}(".concat(textKey).concat(").{0,100}"), Pattern.CASE_INSENSITIVE+Pattern.DOTALL);
        Matcher m = p.matcher(text);
        while(m.find()){
            addFragment(text.substring(m.start(), m.end()));
        }
    }
    
    private String getDateFromDateEditonOrTitle(Element dateElement){
        String ret = null;
        if(dateElement==null){
            ret = ret = getDateFromTitle("0000");
        }else{
            ret = getDateFromDateEdition(dateElement.text().trim());
            if(ret.equals("00/00/0000")){
                ret = getDateFromTitle(dateElement.text().trim());
            }
        }
        return ret;
    }    

    private String getDateFromTitle(String date){
        String ret = Utils.getDateFromText(this.getTitle(), "/");
        if(ret.endsWith("0000")){
            ret = "01/01/".concat(date);
        }
        return ret;        
    }
    
    private String getDateFromDateEdition(String date){
        String ret="00/00/0000";
        Pattern pattern = Pattern.compile(".*(\\d{2}[/-]\\d{2}[/-]\\d{4}).*");
        Matcher matcher = pattern.matcher(date);
        if(matcher.find()){
            ret = matcher.group(1);
        }
        return ret;
    }
    
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
        return getFormatedFileInstance(urlFile, format);
    }
    
    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }
}
