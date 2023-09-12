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

public class BvphResource extends BvphTypeResource{
    protected static final String[] SUPPORTED_FORMATS = {"jpg", "txt", "xml"};

    public BvphResource() {
    }
    
    public BvphResource(String fragmentsFilter, String actionsFilter, String downloadPdfJpg, String titleFilter, String editionDateBloc, String patterToExtractDateFromTitle) {
        this.fragmentsFilter = fragmentsFilter;
        this.actionsFilter = actionsFilter;
        this.jpgTemporalUrl = downloadPdfJpg;
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
            Element toDonwloading = getToDownloading(url, cookies);
            Elements actions = toDonwloading.select(actionsFilter);
            ocrtextUrl = actions.get(0).child(0).attr("href");
            altoXmlUrl = actions.get(1).child(0).attr("href");
            //            jpgTemporalUrl = toDonwloading.select(saveJpgFilter).last().attr("href");
            ocrtextUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, ocrtextUrl + "&aceptar=Aceptar");
            altoXmlUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, altoXmlUrl + "&aceptar=Aceptar");
            jpgTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, jpgTemporalUrl);
        } catch (Exception ex) {
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
        }
    }

    @Override
    protected boolean isFormatSupported(String format) {
        boolean ret = false;
        for (int i = 0; !ret && i < SUPPORTED_FORMATS.length; i++) {
            ret = SUPPORTED_FORMATS[i].equalsIgnoreCase(format);
        }
        return ret;
    }

    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }

    @Override
    public String getContentTypeFormat(String format) {
        return "P";
    }
}
