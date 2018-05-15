package org.elsquatrecaps.jig.sdl.searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BvphResource extends Resource{
    private static final String[] SUPPORTED_FORMATS={"jpg", "txt", "xml"};
    private String fragmentsFilter;
    private String actionsFilter;
    private String saveJpgFilter;
    private String titleFilter;
    private String pageFilter;
    private String altoXmlUrl;
    private String ocrtextUrl;
    private String jpgTemporalUrl;

    public BvphResource(String fragmentsFilter, String actionsFilter, String saveJpgFilter, String titleFilter, String pageFilter) {
        this.fragmentsFilter = fragmentsFilter;
        this.actionsFilter = actionsFilter;
        this.saveJpgFilter = saveJpgFilter;
        this.titleFilter = titleFilter;
        this.pageFilter = pageFilter;
    }
    
    public void updateFromElement(Element elem, String context, Map<String, String> cookies){
        Element dlElement;
        List<Element> frags;
        
        dlElement = elem.parents().get(2);
        setPublicationId(dlElement.attr("id"));
        setTitle(dlElement.selectFirst(titleFilter).text());
        setPageId(elem.child(0).attr("id"));
        setPage(elem.selectFirst(pageFilter).text());
        frags = new ArrayList<>(elem.select(fragmentsFilter));
        for(int i=0; i<frags.size(); i++){
            addFragment(frags.get(i).text());
        }
        String relativeUrl = elem.child(0).attr("href");
        String url = GetRemoteProcess.relativeToAbsoluteUrl(context, relativeUrl);
        Element toDonwloading = getToDownloading(url);
        Elements actions = toDonwloading.select(actionsFilter);
        ocrtextUrl = actions.get(0).child(0).attr("href");
        altoXmlUrl = actions.get(1).child(0).attr("href");
        jpgTemporalUrl = toDonwloading.select(saveJpgFilter).last().attr("href");
        
        ocrtextUrl = GetRemoteProcess.relativeToAbsoluteUrl(context, ocrtextUrl+"&aceptar=Aceptar");
        altoXmlUrl = GetRemoteProcess.relativeToAbsoluteUrl(context, altoXmlUrl+"&aceptar=Aceptar");
        jpgTemporalUrl = GetRemoteProcess.relativeToAbsoluteUrl(context, jpgTemporalUrl+"&aceptar=Aceptar");
    }
    
    private Element getToDownloading(String url){
        GetRemoteProcess grp = new GetRemoteProcess(url);
        grp.setParam("aceptar", "Aceptar");
        Element toDonwloading = grp.get();
        return toDonwloading;
    }
            
            
    @Override
    public FormatedFile getFormatedFile() {
        return getStrictFormatedFile("jpg");
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
        String urlFile = null;
        /*http://prensahistorica.mcu.es/es/catalogo_imagenes/iniciar_descarga.cmd?path=4033223&posicion=104&numTotalPags=480*/
        switch(format){
            case "xml":
                urlFile = altoXmlUrl;
                break;
            case "txt":
                urlFile = ocrtextUrl;
                break;
            case "jpg":
                urlFile = jpgTemporalUrl;
                break;
        }
        return new BvphFormatedFile(urlFile, format, getPublicationId() + "_" + getPageId(), getPublicationId() + "_" + getPageId() + "." +format);
    }

    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }
}
