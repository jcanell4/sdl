/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;

public class ArcaSearchIterator extends BvphTypeSearchIterator<ArcaResource>{
    String pdfUrl = null;
    String jpgUrl = null;
    
    public ArcaSearchIterator(Element element,  BvphGetRemoteProcess getRemoteProcess){
        this._initAttr();
        this._init(element, getRemoteProcess);
    }
    
    public ArcaSearchIterator(BvphGetRemoteProcess getRemoteProcess){
        this._initAttr();
        this._init(getRemoteProcess);
    }

    public ArcaSearchIterator(){
        this._initAttr();
    }
    
    private void _initAttr(){
        numResourcesThreshold = 10000;
        thereIsTooMuchFilter = "div#consulta_resultados_sumario div.resultados_opciones p.warning span.texto_warning strong";
        ascendingOrderFilter = "#consulta_resultados_sumario div.resultados_opciones p.resultados_orden span.valor span.campo_fechapublicacionorden span.links_orden span.enlace_cambio span.boton_orden_ascendente a";
        ascendingOrderOkFilter = "#consulta_resultados_sumario div.resultados_opciones p.resultados_orden span.valor span.campo_fechapublicacionorden span.links_orden span.recurso_orden_ascendente_ok";
        navPagesFilter = "#navegacion_resultados div.nav_marco div.nav_paginas span.nav_descrip";
        navPagesNextFilter = "#navegacion_resultados div.nav_marco div.nav_paginas span.nav_alante a#boton_siguiente";
        newsPaperEditionListFilter = "#navegacion_resultados div.nav_marco ol#nav_registros li ul>li.unidad_textual";
        pageNewsPaperListFilter = "ul>li.unidad_textual"; //.child[0].attr("href")   //relative to its father, a tag li got aplying  newsPaperEditionListFilter filter
        pdfUrl = "div.grupo_imagenes p.gruposimagenes a[data-analytics-group$='PDF']"; //a.parents().get(2).select(pdfUrl).get(1).attr("href");
        morePubYearFilter = "#contenidos_periodo ul li.enlacemas a";    
        mainPublicationYearsFilter = "dd#contenidos_periodo ul li";    
        pubYearPaginatedRegistersFilter = "ol#nav_registros.nav_registros li";
        pubYearPaginatedRegistersNextPageFilter = "a#boton_siguiente";
        fragmentsFilter = "ul.texto_ocurrencias li";
        actionsFilter = "div#tab_acciones ul li";
        saveJpgFilter = "ol#nav_registros div.visualizador_menu span#grupo_1 a";
        titleFilter = "dt span span.titulo a bdi";
        pageFilter = "p strong a";
        editionDateBloc = "dt span span.datos_publicacion bdi";
        downloadPdfJpg = "../catalogo_imagenes/imagen_id.do?formato=jpg&registrardownload=0";
    }
    
    protected ArcaResource getResource(Element a) {       
//  https://arca.bnc.cat/arcabib_pro/ca/catalogo_imagenes/iniciar_descarga.do?interno=S&posicion=3&path=1008850&formato=Imagen%20JPG        
        ArcaResource ret = null;
        String urlToDownloadPdf = a.parents().get(1).select(pdfUrl).get(0).attr("href");
        String urlToDownloadJpg = downloadPdfJpg.concat("&").concat(Utils.urlQueryPath(a.child(0).attr("href")));
        ret = new ArcaResource(fragmentsFilter, actionsFilter, urlToDownloadJpg, urlToDownloadPdf, titleFilter, editionDateBloc, patterToExtractDateFromTitle);
        ret.updateFromElement(a, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
        return ret;
    }
}
