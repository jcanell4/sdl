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

public class HdResource extends SearcherResource{
    private static final String[] SUPPORTED_FORMATS={"pdf"};
    private String titleFilter;
    private String pageNumFilter;
    private String editionDateFilter;
    private String pdfUrl;
    private String fragmentsFilter;
    private String savePdfFilter;
    private String noPdfUrl;

    public HdResource() {
    }
    
    public HdResource(String titleFilter, String editionDateFilter, String pageNumFilter, String fragmentsFilter, String savePdfFilter, String noPdfFileUrl) {
        this.titleFilter = titleFilter;
        this.editionDateFilter = editionDateFilter;
        this.pageNumFilter = pageNumFilter;
        this.fragmentsFilter = fragmentsFilter;
        this.savePdfFilter = savePdfFilter;
        this.noPdfUrl = noPdfFileUrl;
    }
    
    public void updateFromElement(Element contentDocum, String id, String context, Map<String, String> cookies){
        try{
            setPublicationId(Utils.getNormalizedText(id));
            setTitle(contentDocum.selectFirst(titleFilter).text().trim());
            setPage(contentDocum.selectFirst(pageNumFilter).text().trim());
            setPageId(Utils.getNormalizedText(getPage()));
            setEditionDate(getDateFromDateEditonOrTitle(contentDocum.selectFirst(editionDateFilter)));
            Element elementText = contentDocum.selectFirst(this.fragmentsFilter);
            if(elementText!=null){
                addFragment(elementText.val());
            }
            //ALERTA DE VEGADES EL FORMAT ÉS JPG!
            Element pdfElement = contentDocum.selectFirst(savePdfFilter);
            if(pdfElement==null){
                throw new ErrorGettingRemoteData("Error. Nonexistent pdf for: ".concat(this.getTitle()));
            }else{
                pdfUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, pdfElement.attr("src"));
            }
        }catch(Exception ex){
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
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
        return Utils.getNormalizedData(ret);
    }    

    private String getDateFromTitle(String date){
        String ret = Utils.getDateFromText(this.getTitle(), "/");
        if(ret.endsWith("0000")){
            ret = "01/01/".concat(date);
        }
        return ret;        
    }
    
    private String getDateFromDateEdition(String date){
        String ret="01/01/0001";
        Pattern pattern = Pattern.compile(".*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{4})|(\\d{1,2}[/-]\\d{4}).*");
        Matcher matcher = pattern.matcher(date);
        if(matcher.find()){
            if(matcher.group(2)==null){
                ret = matcher.group(1);
            }else{
                ret = "01/".concat(matcher.group(2));
            }
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
