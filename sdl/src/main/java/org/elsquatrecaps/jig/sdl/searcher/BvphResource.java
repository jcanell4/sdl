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

public class BvphResource extends SearcherResource{
    private static final String[] SUPPORTED_FORMATS={"jpg", "txt", "xml"};
    private String fragmentsFilter;
    private String actionsFilter;
    private String saveJpgFilter;
    private String titleFilter;
    private String pageFilter;
    private String editionDateBlocFilter;
    private String altoXmlUrl;
    private String ocrtextUrl;
    private String jpgTemporalUrl;
    private String patterToExtractDateFromTitle;

    public BvphResource() {
    }
    
    public BvphResource(String fragmentsFilter, String actionsFilter, String saveJpgFilter, String titleFilter, String pageFilter, String editionDateBloc, String patterToExtractDateFromTitle) {
        this.fragmentsFilter = fragmentsFilter;
        this.actionsFilter = actionsFilter;
        this.saveJpgFilter = saveJpgFilter;
        this.titleFilter = titleFilter;
        this.pageFilter = pageFilter;
        this.editionDateBlocFilter = editionDateBloc;
        this.patterToExtractDateFromTitle = patterToExtractDateFromTitle;
    }
    
    public void updateFromElement(Element elem, String context, Map<String, String> cookies){
        Element dlElement;
        List<Element> frags;
        try{
            dlElement = elem.parents().get(2);
            setPublicationId(dlElement.attr("id"));
            setTitle(dlElement.selectFirst(titleFilter).text());
            setPageId(elem.child(0).attr("id"));
            setPage(elem.selectFirst(pageFilter).text());
            setEditionDate(getDateFromDbiOrTitle(dlElement.selectFirst(editionDateBlocFilter)));
            frags = new ArrayList<>(elem.select(fragmentsFilter));
            for(int i=0; i<frags.size(); i++){
                addFragment(frags.get(i).text());
            }
            String relativeUrl = elem.child(0).attr("href");
            String url = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, relativeUrl);
            Element toDonwloading = getToDownloading(url);
            Elements actions = toDonwloading.select(actionsFilter);
            ocrtextUrl = actions.get(0).child(0).attr("href");
            altoXmlUrl = actions.get(1).child(0).attr("href");
            jpgTemporalUrl = toDonwloading.select(saveJpgFilter).last().attr("href");

            ocrtextUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, ocrtextUrl+"&aceptar=Aceptar");
            altoXmlUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, altoXmlUrl+"&aceptar=Aceptar");
            jpgTemporalUrl = AbstractGetRemoteProcess.relativeToAbsoluteUrl(context, jpgTemporalUrl+"&aceptar=Aceptar");
        }catch(Exception ex){
            logger.error("Error carregant un registre: ".concat(ex.getMessage()));
            throw new ErrorGettingRemoteResource(ex);
        }
    }
    
    private String getDateFromDbiOrTitle(Element dateElement){
        String ret = null;
        if(dateElement==null){
            ret = getDateFromTitle();
        }else{
            ret = getDateFromDbi(dateElement.text());
        }
        return ret;
    }
    
    private String getDateFromTitle(){
        String ret = Utils.getDateFromText(this.getTitle(), "/");
        if(ret.endsWith("0000")){
            Pattern pattern = Pattern.compile(patterToExtractDateFromTitle);
            Matcher matcher = pattern.matcher(this.getTitle());
            if(matcher.find()){
              ret = matcher.group(1);
            }
        }
        return ret;        
    }
    
    private String getDateFromDbi(String bdi){
        String ret="00/00/0000";
        Pattern pattern = Pattern.compile(".*(\\d{2}[/-]\\d{2}[/-]\\d{4}).*");
        Matcher matcher = pattern.matcher(bdi);
        if(matcher.find()){
            ret = matcher.group(1);
        }
        return ret;
    }
    
    private Element getToDownloading(String url){
        AbstractGetRemoteProcess grp = new GetRemoteProcessWithoutParams(url);
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
        return getFormatedFileInstance(urlFile, format);
    }
    
    protected FormatedFile getFormatedFileInstance(String url, String format){
        FormatedFile ret;
        String name = getFileName();
        if(format.equals("jpg")){
            ret = new BvphJpgFile(url, name, name.concat(".").concat(format));
        }else{
            ret = super.getFormatedFileInstance(url, format);
        }
        return ret;
    }

    @Override
    public String[] getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }
}
