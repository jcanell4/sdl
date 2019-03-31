package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteData;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArcaResource extends BvphTypeResource{
//    protected static final String[] ALL_SUPPORTED_FORMATS = {"pdf","jpg", "txt", "xml"};
    protected ArrayList<String> supportedFormats = new ArrayList<>();
    private String pdfTemporalUrl;

    public ArcaResource() {
    }
    
    public ArcaResource(String fragmentsFilter, String actionsFilter, String downloadJpg, String downloadPdf, String titleFilter, String editionDateBloc, String patterToExtractDateFromTitle) {
        this.fragmentsFilter = fragmentsFilter;
        this.actionsFilter = actionsFilter;
        this.jpgTemporalUrl = downloadJpg;
        this.pdfTemporalUrl = downloadPdf;
        this.titleFilter = titleFilter;
        this.editionDateBlocFilter = editionDateBloc;
        this.patterToExtractDateFromTitle = patterToExtractDateFromTitle;
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
            setPageId(elem.child(1).attr("id"));
            setPage(elem.child(1).text());
            setEditionDate(getDateFromDbiOrTitle(dlElement.selectFirst(editionDateBlocFilter)));
            frags = new ArrayList<>(elem.select(fragmentsFilter));
            for (int i = 0; i < frags.size(); i++) {
                addFragment(frags.get(i).text());
            }
            String relativeUrl = elem.child(0).attr("href");
            String url = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, relativeUrl);
            Element toDonwloading = getToDownloading(url);
            Elements actions = toDonwloading.select(actionsFilter);
            ocrtextUrl = actions.get(0).child(0).attr("href");
            altoXmlUrl = actions.get(1).child(0).attr("href");
            //            jpgTemporalUrl = toDonwloading.select(saveJpgFilter).last().attr("href");
            ocrtextUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, ocrtextUrl + "&aceptar=Aceptar");
            altoXmlUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, altoXmlUrl + "&aceptar=Aceptar");
            jpgTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, jpgTemporalUrl);
            pdfTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, pdfTemporalUrl);
            this.updateSupportedFormats();
            
        } catch (Exception ex) {
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
        }
    }

    @Override
    protected boolean isFormatSupported(String format) {
        boolean ret = false;
        for (int i = 0; !ret && i < supportedFormats.size(); i++) {
            ret = supportedFormats.get(i).equalsIgnoreCase(format);
        }
        return ret;
    }

    @Override
    public String[] getSupportedFormats() {
        String[] ret = new String[supportedFormats.size()];
        return supportedFormats.toArray(ret);
    }
    
    private void updateSupportedFormats(){
        this.supportedFormats.add("txt");
        this.supportedFormats.add("xml");
        if(Utils.isCorrectContentType(jpgTemporalUrl, "jpeg")){
            this.supportedFormats.add("jpg");
        }
        if(Utils.isCorrectContentType(pdfTemporalUrl, "pdf")){
            this.supportedFormats.add("pdf");
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
}
