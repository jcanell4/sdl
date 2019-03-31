/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.util.Utils;
import org.jsoup.nodes.Element;

public class BvphSearchIterator extends BvphTypeSearchIterator<BvphResource>{

    
    public BvphSearchIterator(Element element,  BvphGetRemoteProcess getRemoteProcess){
        this._initAttr();
        this._init(element, getRemoteProcess);
    }
    
    public BvphSearchIterator(BvphGetRemoteProcess getRemoteProcess){
        this._initAttr();
        this._init(getRemoteProcess);
    }

    public BvphSearchIterator(){
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
        morePubYearFilter = "#contenidos_anyopublicacion ul li.enlacemas a";
        mainPublicationYearsFilter = "dd#contenidos_anyopublicacion ul li";
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
    
    protected BvphResource getResource(Element a) {       
        BvphResource ret;
        String urlToDownloadJpg = downloadPdfJpg.concat("&").concat(Utils.urlQueryPath(a.child(0).attr("href")));
        ret = new BvphResource(fragmentsFilter, actionsFilter, urlToDownloadJpg, titleFilter, editionDateBloc, patterToExtractDateFromTitle);
        ret.updateFromElement(a, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
        return ret;
    }
    
    
/////// ------ CLASS BvphBlockSearhIterator  ---------------------//
//
//    private class BvphBlockSearhIterator implements Iterator<BvphResource>{
//        Element elementToNextPage;
//        Elements blocElements;
//        int elementsToProcess=0;
//                
//        public BvphBlockSearhIterator() {
//            updateValues();
//            
//            nextBlockIsNeeded = false;
//            initilized = true;
//        }
//        
//        
//
//        @Override
//        public boolean hasNext() {
//            logger.debug(String.format("Elements actuals: %d", elementsToProcess));
//            logger.debug(String.format("Hi hamés pàgines: %s", elementToNextPage!=null?"SI":"NO"));
//            return elementToNextPage!=null || elementsToProcess>0;
//        }
//
//        @Override
//        public BvphResource next() {
//            BvphResource ret;
//            Element a;
//            if(elementsToProcess==0){
//                logger.debug("S'ha acabta la pàgina actual, s'inicia la càrrega d'una nova pàgina (LoadingNextPage)");
//                loadNextPage();
//                updateValues();          
//                logger.debug("nova pàgina carregada");
//            }
//            a = blocElements.get(blocElements.size()-elementsToProcess);
//            --elementsToProcess;
//            logger.debug("S'ha localitzat el registre. Es passa a extreure'n la informació");
//            ret = getResource(a);
//            logger.debug("Informació extreta");
//            return ret;
//        }
//        
//        private void loadNextPage(){
//            if(elementToNextPage!=null){
//                String url =  url = relativeToAbsoluteUrl(elementToNextPage.attr("href"));
//                getRemoteProcessAux.setUrl(url);
//                getRemoteProcessAux.setDefaultCookies(getRemoteProcess.getCookies());
////                if(getRemoteProcessAux.getCookies()==null || getRemoteProcessAux.getCookies().size()==0){
////                    getRemoteProcessAux.setCookies(getRemoteProcess.getCookies());
////                }
//                sourceElement = getRemoteProcessAux.get();
//            }
//        }
//        
//        private void updateValues(){
//            elementToNextPage = sourceElement.selectFirst(navPagesNextFilter);
//            blocElements = sourceElement.select(newsPaperEditionListFilter);
//            elementsToProcess = blocElements.size();
//        }
//
//        private BvphResource getResource(Element a) {       
//            BvphResource ret;
//            String urlToDownloadJpg = downloadPdfJpg.concat("&").concat(Utils.urlQueryPath(a.child(0).attr("href")));
//            ret = new BvphResource(fragmentsFilter, actionsFilter, urlToDownloadJpg, titleFilter, editionDateBloc, patterToExtractDateFromTitle);
//            ret.updateFromElement(a, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
//            return ret;
//        }
//    }
}
