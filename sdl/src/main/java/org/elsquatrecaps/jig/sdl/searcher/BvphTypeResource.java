/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.elsquatrecaps.jig.sdl.exception.ErrorGettingRemoteResource;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author josep
 */
public abstract class BvphTypeResource extends SearcherResource {
    protected String jpgTemporalUrlBase = "../catalogo_imagenes/imagen_id.do";
    protected String fragmentsFilter;
    protected String actionsFilter;
    protected String titleFilter;
    protected String editionDateBlocFilter;
    protected String altoXmlUrl;
    protected String ocrtextUrl;
    protected String jpgTemporalUrl;
    protected String patterToExtractDateFromTitle;

    public BvphTypeResource() {
    }

    protected String getDateFromDbiOrTitle(Element dateElement) {
        String ret = null;
        if (dateElement == null) {
            ret = getDateFromTitle();
        } else {
            ret = getDateFromDbi(dateElement.text());
        }
        return ret;
    }

    protected String getDateFromTitle() {
        String ret = Utils.getDateFromText(this.getTitle(), "/");
        if (ret.endsWith("0000")) {
            Pattern pattern = Pattern.compile(patterToExtractDateFromTitle);
            Matcher matcher = pattern.matcher(this.getTitle());
            if (matcher.find()) {
                ret = matcher.group(1);
            }
        }
        return ret;
    }

    protected String getDateFromDbi(String bdi) {
        String ret = "00/00/0000";
        Pattern pattern = Pattern.compile(".*(\\d{2}[/-]\\d{2}[/-]\\d{4}).*");
        Matcher matcher = pattern.matcher(bdi);
        if (matcher.find()) {
            ret = matcher.group(1);
        } else {
            ret = Utils.getDateFromText(bdi, "/");
            if (ret.startsWith("00") || ret.substring(3, 5) == "00" || ret.endsWith("0000")) {
                String dateFromTitle = Utils.getDateFromText(this.getTitle(), "/");
                if (ret.startsWith("00") && !dateFromTitle.startsWith("00")) {
                    ret = dateFromTitle.substring(0, 2).concat(ret.substring(2));
                }
                if (ret.substring(3, 5) == "00" && dateFromTitle.substring(3, 5) != "00") {
                    ret = ret.substring(0, 2).concat(dateFromTitle.substring(2, 6)).concat(ret.substring(6));
                }
                if (ret.endsWith("0000") && !dateFromTitle.endsWith("0000")) {
                    ret = ret.substring(0, 6).concat(dateFromTitle.substring(6));
                }
            }
        }
        return ret;
    }

    protected Element getToDownloading(String url) {
        AbstractGetRemoteProcess grp = new GetRemoteProcessWithUniqueKeys(url);
        grp.setParam("aceptar", "Aceptar");
        Element toDonwloading = grp.get();
        return toDonwloading;
    }

    @Override
    public FormatedFile getFormatedFile() {
        return getStrictFormatedFile("jpg");
    }

    @Override
    protected FormatedFile getStrictFormatedFile(String format) {
        String urlFile = null;
        /*http://prensahistorica.mcu.es/es/catalogo_imagenes/iniciar_descarga.cmd?path=4033223&posicion=104&numTotalPags=480*/
        switch (format) {
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

    //    protected FormatedFile getFormatedFileInstance(String url, String format){
    //        FormatedFile ret;
    //        String name = getFileName();
    //        if(format.equals("jpg")){
    //            ret = new BvphJpgFile(url, name, name.concat(".").concat(format));
    //        }else{
    //            ret = super.getFormatedFileInstance(url, format);
    //        }
    //        return ret;
    //    }
}
