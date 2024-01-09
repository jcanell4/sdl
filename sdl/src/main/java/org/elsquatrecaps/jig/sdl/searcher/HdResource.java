package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import static org.elsquatrecaps.jig.sdl.searcher.SearcherResource.logger;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;

public class HdResource extends SearcherResource{
    private static final String[] SUPPORTED_FORMATS={"pdf", "jpg", "txt"};
    private String idFilter;
    private String basicInfoNewsPaperListFilter;
    private String titleFilter;
    private String pageNumFilter;
    private String editionDateFilter;
    private String pdfUrl;
    private String fragmentsFilter;
    private String urlDownloadPdfFile;
    private String urlDownloadJpgFile;
    private String urlDownloadTxtFile;
//    private String savePdfFilter;
//    private Integer iPageNumber;
//    private String textKey;


    public HdResource() {
    }
    
    public HdResource(String idFilter, String basicInfoNewsPaperListFilter, String titleFilter, String editionDateFilter, 
            String pageNumFilter, String fragmentsFilter, String urlDownloadPdfFile, String urlDownloadJpgFile, String urlDownloadTxtFile) {
        this.idFilter = idFilter;
        this.basicInfoNewsPaperListFilter = basicInfoNewsPaperListFilter;
        this.titleFilter = titleFilter;
        this.editionDateFilter = editionDateFilter;
        this.pageNumFilter = pageNumFilter;
        this.fragmentsFilter = fragmentsFilter;
        this.urlDownloadPdfFile = urlDownloadPdfFile;
        this.urlDownloadJpgFile = urlDownloadJpgFile;
        this.urlDownloadTxtFile = urlDownloadTxtFile;
//        this.savePdfFilter = savePdfFilter;
//        this.noPdfUrl = noPdfFileUrl;
//        this.textKey = textKey.replaceAll("\"", "").replaceAll("( )+", "|");        
    }
    
    public void updateFromElement(Element contentDocum, String context, Map<String, String> cookies){
        String idhref = contentDocum.selectFirst(idFilter).attr("href");
        String id = idhref.substring(17, idhref.indexOf("&page"));
        try{
            Element basicInfoElem = contentDocum.selectFirst(basicInfoNewsPaperListFilter);
            String title = basicInfoElem.selectFirst(titleFilter).text().trim();
            String editionDate = basicInfoElem.selectFirst(editionDateFilter).text().trim();
            String strPageNumber = basicInfoElem.selectFirst(pageNumFilter).text().trim();
            Integer iPageNumber = Integer.valueOf(strPageNumber.replaceAll("[^0-9]", ""));
            String fragmentsText = contentDocum.selectFirst(fragmentsFilter).text().trim();            
            setPublicationId(Utils.getNormalizedText(id));
            setTitle(title);
            setPage(strPageNumber);
            setPageId(Utils.getNormalizedText("P".concat(iPageNumber.toString())));
            setEditionDate(getDateFromDateEditonOrTitle(editionDate));
            saveFragments(fragmentsText.split("\\.\\.\\."));
            
            
            
            
            
            
            
            
            
            
            
            
//            //ALERTA DE VEGADES EL FORMAT ÉS JPG!
//            Element pdfElement = contentDocum.selectFirst(savePdfFilter);
//            if(pdfElement==null){
//                throw new ErrorGettingRemoteData("Error. Nonexistent pdf for: ".concat(this.getTitle()));
//            }else{
//                pdfUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, pdfElement.attr("src"));
//            }
        }catch(Exception ex){
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
        }
        
    }
    
    private String getDateFromDateEditonOrTitle(String dateElement){
        String ret = null;
        if(dateElement==null){
            ret = getDateFromTitle("0000");
        }else{
            ret = getDateFromDateEdition(dateElement.trim());
            if(ret.equals("00/00/0000")){
                ret = getDateFromTitle(dateElement.trim());
            }
        }
        return Utils.getNormalizedData(ret);
    }    

    private String getDateFromDateEdition(String date){
        String ret="00/00/0000";
        Pattern pattern = Pattern.compile(".*(\\d{2}[/-]\\d{1,2}[/-]\\d{4})|(\\d{1}[/-]\\d{1,2}[/-]\\d{4})|(\\d{2}[/-]\\d{4})|(\\d{1}[/-]\\d{4}).*");
        Matcher matcher = pattern.matcher(date);
        if(matcher.find()){
            if(matcher.group(1)!=null){
                ret = matcher.group(1);
            }else if(matcher.group(2)!=null){
                ret = "0".concat(matcher.group(2));
            }else if(matcher.group(3)!=null){
                ret = "00/".concat(matcher.group(3));
            }else{
                ret = "00/0".concat(matcher.group(4));
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
        String urlFile=null;
        switch (format) {
            case "pdf":
//                urlFile = urlDownloadPdfFile.concat("?id=").concat(getPublicationId()).concat("&page=")
//                        .concat(iPageNumber.toString()).concat("&attachment=").concat(getFileName(format)).concat(".pdf");
                urlFile = urlDownloadPdfFile.concat("?id=").concat(getPublicationId()).concat("&page=")
                        .concat(getPageId().substring(1)).concat("&attachment=").concat(getFileName(format)).concat(".pdf");
                break;
            case "txt":
//                urlFile = urlDownloadTxtFile.concat("?id=").concat(getPublicationId()).concat("&page=")
//                        .concat(iPageNumber.toString()).concat("&attachment=").concat(getFileName(format)).concat(".txt");
                urlFile = urlDownloadTxtFile.concat("?id=").concat(getPublicationId()).concat("&page=")
                        .concat(getPageId().substring(1)).concat("&attachment=").concat(getFileName(format)).concat(".txt");
                break;
            case "jpg":
//                urlFile = urlDownloadJpgFile.concat("&id=").concat(getPublicationId()).concat("&page=")
//                        .concat(iPageNumber.toString()).concat("&attachment=").concat(getFileName(format)).concat(".jpg");
                urlFile = urlDownloadJpgFile.concat("&id=").concat(getPublicationId()).concat("&page=")
                        .concat(getPageId().substring(1)).concat("&attachment=").concat(getFileName(format)).concat(".jpg");
                break;
        }        
        return getFormatedFileInstance(urlFile, format);
    }
    
    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }

    private void saveFragments(String[] text){
        for(int id=0; id < text.length; id++){
            if(text[id].length()<500){
                addFragment(text[id]);
            }else{
                addFragment(text[id].substring(0, 500));
            }
        }
    }    

    @Override
    public String getContentTypeFormat(String format) {
        return "P";
    }
}
