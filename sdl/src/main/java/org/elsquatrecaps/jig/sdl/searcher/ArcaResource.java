package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArcaResource extends BvphTypeResource{
//    protected static final String[] ALL_SUPPORTED_FORMATS = {"pdf","jpg", "txt", "xml"};
//    protected ArrayList<String> supportedFormats = new ArrayList<>();
    protected HashMap<String, String> contentTypeAndSupportedFormats = new HashMap();    
    private String pdfTemporalUrl;
    private Element toDonwloading;
    private String previousPageFilter;
    private String nextPageFilter;
    private String downloadJpgFromPagesMenu;
    private String imageID;
    private String pageIdFilter;
    
    public ArcaResource() {
    }
    
    public ArcaResource(String fragmentsFilter, String actionsFilter, String downloadJpg, 
            String downloadPdf, String titleFilter, String editionDateBloc, String pageIdFilter,
            String patterToExtractDateFromTitle, String downloadJpgFromPagesMenu,
            String previousPageFilter, String nextPageFilter) {
        this.fragmentsFilter = fragmentsFilter;
        this.actionsFilter = actionsFilter;
        this.jpgTemporalUrl = downloadJpg;
        this.pdfTemporalUrl = downloadPdf;
        this.titleFilter = titleFilter;
        this.editionDateBlocFilter = editionDateBloc;
        this.patterToExtractDateFromTitle = patterToExtractDateFromTitle;
        this.downloadJpgFromPagesMenu = downloadJpgFromPagesMenu;
        this.previousPageFilter = previousPageFilter;
        this.nextPageFilter = nextPageFilter;
        this.pageIdFilter = pageIdFilter;                
    }
    
    public ArcaResource(ArcaResource sibling){
        this.setEditionDate(sibling.getEditionDate());
        this.setProcessDateResult(sibling.getProcessDateResult());
        this.setPublicationId(sibling.getPublicationId());
        this.setTitle(sibling.getTitle());
        this.pdfTemporalUrl = sibling.pdfTemporalUrl;
        this.previousPageFilter = sibling.previousPageFilter;
        this.nextPageFilter = sibling.nextPageFilter;
        this.downloadJpgFromPagesMenu = sibling.downloadJpgFromPagesMenu;
        this.actionsFilter = sibling.actionsFilter;
        this.pageIdFilter = sibling.pageIdFilter;
    }
    
    @Override
    public boolean hasNextPage(){
        return toDonwloading.selectFirst(nextPageFilter)!=null;
    }
    
    @Override
    public boolean hasPrevioiusPage(){
        return toDonwloading.selectFirst(previousPageFilter)!=null;
    }
    
    public boolean updateFromPagesMenuSiblingElement(ArcaResource res, int prevOrNext, String context, Map<String, String> cookies){
        boolean ret;
        String href=null;
        if(prevOrNext==PREVIOUS_SIBLING){
            Element aPrevious = res.toDonwloading.selectFirst(previousPageFilter);
            if(aPrevious!=null){
                href = Utils.urlQueryPath(aPrevious.attr("href"));
                GetRemoteProcessWithoutParams rp = new GetRemoteProcessWithoutParams(
                        Utils.relativeToAbsoluteUrl(context, aPrevious.attr("href")), cookies);
                toDonwloading = rp.get();
            }
        }else{
            Element aNext = res.toDonwloading.selectFirst(nextPageFilter);
            if(aNext!=null){
                href = Utils.urlQueryPath(aNext.attr("href"));
                GetRemoteProcessWithoutParams rp = new GetRemoteProcessWithoutParams(
                        Utils.relativeToAbsoluteUrl(context, aNext.attr("href")), cookies);
                toDonwloading = rp.get();
            }
        }
        ret = toDonwloading!=null;
        if(ret){
            setPageId("P".concat(toDonwloading.selectFirst(pageIdFilter).attr("value")));
            Elements actions = toDonwloading.select(actionsFilter);
            if(!actions.isEmpty()){
                ocrtextUrl = actions.get(0).child(0).attr("href");
                ocrtextUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, ocrtextUrl + "&aceptar=Aceptar");
            }else{
                ocrtextUrl=null;
            }
            if(actions.size()>1){
                altoXmlUrl = actions.get(1).child(0).attr("href");
                altoXmlUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, altoXmlUrl + "&aceptar=Aceptar");
            }else{
                altoXmlUrl=null;
            }
            jpgTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, downloadJpgFromPagesMenu.concat(href));
            this.updateSupportedFormats();
        }
        return ret;
    }
    
    public void updateFromElement(Element elem, String context, Map<String, String> cookies) {
        Element dlElement;
        List<Element> frags;
        try {
            dlElement = elem.parents().get(2);
            setPublicationId(dlElement.attr("id"));
            setTitle(dlElement.selectFirst(titleFilter).text());
            //            setPageId(elem.child(0).attr("id"));
            //            setPage(elem.selectFirst(pageFilter).text());
//            setPageId(elem.child(1).attr("id"));
//            String strPageNumber = elem.child(1).text();
//            Integer iPageNumber = Integer.valueOf(strPageNumber.replaceAll("[^0-9]", ""));
//            setPageId(Utils.getNormalizedText("P".concat(iPageNumber.toString())));
            setPage(elem.child(1).text());
            setEditionDate(getDateFromDbiOrTitle(dlElement.selectFirst(editionDateBlocFilter)));
            setImageID(elem.child(1).attr("id"));
            frags = new ArrayList<>(elem.select(fragmentsFilter));
            for (int i = 0; i < frags.size(); i++) {
                addFragment(frags.get(i).text());
            }
            String relativeUrl = elem.child(0).attr("href");
            String url = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, relativeUrl);
            toDonwloading = getToDownloading(url, cookies);
            Element elementPageId = toDonwloading.selectFirst(pageIdFilter);
            if(elementPageId!=null){
                setPageId("P".concat(elementPageId.attr("value")));
            }else{
                setPageId("");
            }
            Elements actions = toDonwloading.select(actionsFilter);
            if(actions.size()>0){
                ocrtextUrl = actions.get(0).child(0).attr("href");
                ocrtextUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, ocrtextUrl + "&aceptar=Aceptar");
            }else{
                ocrtextUrl=null;
            }
            if(actions.size()>1){
                altoXmlUrl = actions.get(1).child(0).attr("href");
                altoXmlUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, altoXmlUrl + "&aceptar=Aceptar");
            }else{
                altoXmlUrl=null;
            }
            //            jpgTemporalUrl = toDonwloading.select(saveJpgFilter).last().attr("href");
            if(jpgTemporalUrl!= null){
                jpgTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, jpgTemporalUrl);
            }
            if(pdfTemporalUrl!=null){
                pdfTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, pdfTemporalUrl);
            }
            this.updateSupportedFormats();
            
        } catch (Exception ex) {
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
        }
    }

//    @Override
//    protected boolean isFormatSupported(String format) {
//        return this.contentTypeAndSupportedFormats.containsKey(format);
////        boolean ret = false;
////        for (int i = 0; !ret && i < supportedFormats.size(); i++) {
////            ret = supportedFormats.get(i).equalsIgnoreCase(format);
////        }
////        return ret;    
//    }

    
    @Override
    public String[] getSupportedFormats() {
        String[] ret = new String[contentTypeAndSupportedFormats.size()];
        int i=0;
        for(Entry<String, String> entry: contentTypeAndSupportedFormats.entrySet()){
            ret[i++] = entry.getKey();
        }
        return ret;
    }
    
    public String getContentTypeFormat(String format){
        return this.contentTypeAndSupportedFormats.get(format);
    }
    
    private void updateSupportedFormats(){
        if(jpgTemporalUrl!=null && Utils.isCorrectContentType(jpgTemporalUrl, "jpeg")){
            if(ocrtextUrl!=null){
                this.contentTypeAndSupportedFormats.put("txt", "P");
            }
            if(altoXmlUrl!=null){
                this.contentTypeAndSupportedFormats.put("xml", "P");
            }
            this.contentTypeAndSupportedFormats.put("jpg", "P");
        }
        if(pdfTemporalUrl!= null && Utils.isCorrectContentType(pdfTemporalUrl, "pdf")){
            this.contentTypeAndSupportedFormats.put("pdf", "D");
            if(this.contentTypeAndSupportedFormats.size()==1){
                this.setPageId("");
            }
        }
    }
    
    @Override
    protected FormatedFile getStrictFormatedFile(String format) {
        FormatedFile ret;
        if(format.equals("pdf")){
            ret = getFormatedFileInstance(pdfTemporalUrl, format);
        }else{
            ret = super.getStrictFormatedFile(format);
        }
        return ret;        
    }
    
//    public String getFileName(){
//        String fileName;
//        if(isFormatSupported("pdf")){
//            fileName = _getPdfFileName();
//        }else{
//            fileName = super.getFileName();
//        }
//        return fileName;
//        
//    }
//
//    private String _getPdfFileName(){
//        StringBuilder strBuffer = new StringBuilder();
//        if(getEditionDate()!=null && !getEditionDate().isEmpty() && getEditionDate().matches("[0-9]{2}\\/[0-9]{2}\\/[0-9]{2,4}")){            
//            String[] aDate = getEditionDate().split("\\/");
//            strBuffer.append(aDate[2]);
//            strBuffer.append("_");
//            strBuffer.append(aDate[1]);
//            strBuffer.append("_");
//            strBuffer.append(aDate[0]);
//        }else if(getEditionDate()!=null && !getEditionDate().isEmpty() && getEditionDate().matches("[0-9]{4}.+?[0-9]{1,2}")){            
//            strBuffer.append(getEditionDate().substring(0, 4));
//            strBuffer.append("_00_00");
//        }else if(getEditionDate()!=null && !getEditionDate().isEmpty() && getEditionDate().matches("[0-9]{2}.+?[0-9]{4}")){            
//            strBuffer.append(getEditionDate().substring(getEditionDate().length()-4, getEditionDate().length()));
//            strBuffer.append("_00_00");
//        }else if(getEditionDate()!=null && !getEditionDate().isEmpty() && getEditionDate().matches("[0-9]{1}.+?[0-9]{4}")){            
//            strBuffer.append(getEditionDate().substring(getEditionDate().length()-4, getEditionDate().length()));
//            strBuffer.append("_00_00");
//        }else{
//            strBuffer.append("0000_00_00");
//        }
//        strBuffer.append("_");
//        strBuffer.append(this.getProcessDateResult());
//        strBuffer.append("_");
//        strBuffer.append(this.getPublicationId());
//        strBuffer.append(Utils.buildNormalizedFilename(this.getTitle()));
//        return strBuffer.toString().substring(0,Math.min(60, strBuffer.length()));        
//    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
    
    @Override
    public String getOldId(){
        return this.getPublicationId().concat("_").concat(imageID);
    }
    
    @Override
    public boolean isIdRewritten() {
        return !(this.imageID==null || this.imageID.isEmpty());
    }
}
