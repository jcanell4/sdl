/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.Iterator;
import org.elsquatrecaps.jig.sdl.exception.PreventiveException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author josep
 * @param <T>
 */
public abstract class BvphTypeSearchIterator<T extends SearcherResource> extends SearchIterator<T> {
    
    protected static final Logger logger = LoggerFactory.getLogger(BvphTypeSearchIterator.class);
    int cnt = 0;
    protected int numResourcesThreshold = 10000;
    protected String thereIsTooMuchFilter = "div#consulta_resultados_sumario div.resultados_opciones p.warning span.texto_warning strong";
    protected String ascendingOrderFilter = "#consulta_resultados_sumario div.resultados_opciones p.resultados_orden span.valor span.campo_fechapublicacionorden span.links_orden span.enlace_cambio span.boton_orden_ascendente a";
    protected String ascendingOrderOkFilter = "#consulta_resultados_sumario div.resultados_opciones p.resultados_orden span.valor span.campo_fechapublicacionorden span.links_orden span.recurso_orden_ascendente_ok";
    protected String navPagesFilter = "#navegacion_resultados div.nav_marco div.nav_paginas span.nav_descrip";
    protected String navPagesNextFilter = "#navegacion_resultados div.nav_marco div.nav_paginas span.nav_alante a#boton_siguiente";
    protected String newsPaperEditionListFilter = "#navegacion_resultados div.nav_marco ol#nav_registros li ul>li.unidad_textual";
    protected String pageNewsPaperListFilter = "ul>li.unidad_textual"; //.child[0].attr("href")   //relative to its father, a tag li got aplying  newsPaperEditionListFilter filter
    protected String morePubYearFilter = "#contenidos_anyopublicacion ul li.enlacemas a";
    protected String mainPublicationYearsFilter = "dd#contenidos_anyopublicacion ul li";
    protected String pubYearPaginatedRegistersFilter = "ol#nav_registros.nav_registros li";
    protected String pubYearPaginatedRegistersNextPageFilter = "a#boton_siguiente";
    protected String fragmentsFilter = "ul.texto_ocurrencias li";
    protected String actionsFilter = "div#tab_acciones ul li";
    protected String previousPageFilter = "a#boton_anterior";
    protected String nextPageFilter = "a#boton_siguiente";
    protected String saveJpgFilter = "ol#nav_registros div.visualizador_menu span#grupo_1 a";
    protected String titleFilter = "dt span span.titulo a bdi";
    protected String pageFilter = "p strong a";
    protected String editionDateBloc = "dt span span.datos_publicacion bdi";
    //    @XmlElement
    //    private String downloadPdfJpg = "../catalogo_imagenes/iniciar_descarga.cmd";
    protected String downloadPdfJpg = "../catalogo_imagenes/imagen_id.do?formato=jpg&registrardownload=0";
    protected String patterToExtractDateFromTitle = ".*(\\d{4}\\s+([Ee]nero|[Ff]ebrero|[Mm]arzo|[Aa]bril|[Mm]ayo|[Jj]unio|[Jj]ulio|[Aa]gosto|[Ss]eptiembre|[Oo]ctubre|[Nn]oviembre|[Dd]iciembre)\\s+\\d{2}).*";
    //    @XmlElement
    //    private String noResourcesText = "No hay resultados";
    //    @XmlElement
    //    private String noResourcesFilter = "div#consulta_resultados_sumario div.navegacion_resultados p";
    //    @XmlElement
    //    private String unfulfilledContitionsText = "No hay ningún registro que cumpla las condiciones de búsqueda.";
    protected Element sourceElement;
    protected boolean initilized = false;
    protected BvphBlockSearhIterator currentBlockIterator;
    protected boolean nextBlockIsNeeded;
    protected BvphTypeGetRemoteProcess getRemoteProcess;
    protected AbstractGetRemoteProcess getRemoteProcessAux = new GetRemoteProcessWithoutParams();
    protected int currentBiggerYear = -1;
    protected int currentSmallerYear = -1;
    protected String lastDataProcessed = "01/01/1000";
    protected int pagesBefore = 0;
    protected int pagesAfeter = 0;

    public BvphTypeSearchIterator() {
    }

    public void init(AbstractGetRemoteProcess getRemoteProcess) {
        this._init((BvphTypeGetRemoteProcess) getRemoteProcess);
    }

    public void init(Element element, BvphTypeGetRemoteProcess getRemoteProcess) {
        this._init(element, getRemoteProcess);
    }

    protected void _init(BvphTypeGetRemoteProcess getRemoteProcess) {
        this.getRemoteProcess = getRemoteProcess;
        //        currentBiggerYear = getRemoteProcess.getDefaultBiggerYear();
        currentSmallerYear = getRemoteProcess.getDefaultSmallerYear();
    }

    protected void _init(Element element, BvphTypeGetRemoteProcess getRemoteProcess) {
        this._init(getRemoteProcess);
        this.sourceElement = element;
        //        currentBiggerYear = getRemoteProcess.getDefaultBiggerYear();
        currentSmallerYear = getRemoteProcess.getDefaultSmallerYear();
    }

    @Override
    public boolean hasNext() {
        boolean ret;
        checkNewBlock();
        ret = !this.noResources();
        ret = ret && hasNextInCurrentBlock();
        logger.debug(String.format("hasNext: %s", ret ? "si" : "no"));
        return ret;
    }

    @Override
    public T next() {
        T ret = null;
        ret = currentBlockIterator.next();
        if (ret.getProcessDateResult() == BvphResource.PR_DATE_RES_FIABLE) {
            lastDataProcessed = ret.getEditionDate();
        }
        logger.debug(String.format("next: %s", ret.getTitle()));
        return ret;
    }

    protected int getCurrentBiggerYear() {
        return currentBiggerYear;
    }

    protected int getCurrentSmallerYear() {
        return currentSmallerYear;
    }

    protected void checkNewBlock() {
        if (!initilized) {
            if (sourceElement == null) {
                updateOriginalSource();
            }
            if (!noResources()) {
                currentBlockIterator = new BvphBlockSearhIterator();
            }
        } else {
            nextBlockIsNeeded = !hasNextInCurrentBlock() && thereIsTooMuchResources();
        }
        if (nextBlockIsNeeded) {
            updateOriginalSource(getCurrentSmallerYear());
            currentBlockIterator = new BvphBlockSearhIterator();
        }
    }

    protected boolean noResources() {
        boolean ret;
        Element elem;
        String message = "";
        ret = sourceElement.select(navPagesFilter).size() == 0;
        return ret;
    }

    protected boolean thereIsTooMuchResources(Element e) {
        Element elem;
        int moreThanThreshold = 0;
        elem = e.selectFirst(thereIsTooMuchFilter);
        if (elem != null) {
            moreThanThreshold = Integer.valueOf(elem.text());
        }
        return moreThanThreshold > numResourcesThreshold;
    }

    protected boolean thereIsTooMuchResources() {
        return thereIsTooMuchResources(sourceElement);
    }

    protected boolean hasNextInCurrentBlock() {
        boolean ret = false;
        if (currentBlockIterator != null) {
            ret = currentBlockIterator.hasNext();
        }
        return ret;
    }

    protected void updateOriginalSource() {
        updateOriginalSource(currentSmallerYear);
    }

    protected void updateOriginalSource(int currentSmallerYear) {
        Element aElement;
        getRemoteProcess.setSmallerYear(currentSmallerYear);
        getRemoteProcess.setSmallerData(lastDataProcessed);
        sourceElement = getRemoteProcess.get();
        if (thereIsTooMuchResources()) {
            aElement = sourceElement.selectFirst(ascendingOrderFilter);
            if (aElement != null) {
                String url = relativeToAbsoluteUrl(aElement.attr("href"));
                getRemoteProcessAux.setUrl(url);
                getRemoteProcessAux.setDefaultCookies(getRemoteProcess.getCookies());
                sourceElement = getRemoteProcessAux.get();
            }
            updateCurrentBiggerYear();
        }
    }

    protected void updateCurrentBiggerYear() {
        Element aElem = sourceElement.selectFirst(morePubYearFilter);
        if (aElem != null) {
            //paging publication years
            String url = relativeToAbsoluteUrl(aElem.attr("href"));
            getRemoteProcessAux.setUrl(url);
            getRemoteProcessAux.setDefaultCookies(getRemoteProcess.getCookies());
            currentBiggerYear = getMaximumYearOfPaginatedList(getRemoteProcessAux.get());
        } else {
            //navigate in default list
            currentBiggerYear = getMaximumYearOfSingleList(sourceElement.select(mainPublicationYearsFilter));
        }
    }

    protected int getMaximumYearOfSingleList(Elements list) {
        int value;
        int ret = Integer.valueOf(list.get(0).child(0).text());
        for (Element elem : list) {
            value = Integer.valueOf(elem.child(0).text());
            if (value > ret) {
                ret = value;
            }
        }
        return ret;
    }

    protected int getMaximumYearOfPaginatedList(Element paginatedList) {
        String url;
        int ret = currentBiggerYear;
        Element nextPage = paginatedList.selectFirst(pubYearPaginatedRegistersNextPageFilter);
        //Alerta. Comprovar que si nextPage és null, ñes degut a que no hi ha més anys disponibles i cal aplicar getMaximumYearOfSingleList
        if (nextPage != null) {
            logger.debug("Cercant getMaximumYearOfPaginatedList");
            do {
                Elements list = paginatedList.select(pubYearPaginatedRegistersFilter);
                int value = getMaximumYearOfSingleList(list);
                if (value > ret || ret == -1) {
                    ret = value;
                }
                url = relativeToAbsoluteUrl(nextPage.attr("href"));
                getRemoteProcessAux.setUrl(url);
                paginatedList = getRemoteProcessAux.get();
                nextPage = paginatedList.selectFirst(pubYearPaginatedRegistersNextPageFilter);
            } while (nextPage != null);
            logger.debug("getMaximumYearOfPaginatedList trobat");
        } else {
            logger.error("next page of getMaximumYearOfPaginatedList és null. Suposem que l'ultim any és l'actual currentBiggerYear:".concat(String.valueOf(ret)).concat(". Es llançarà una excepció preventiva."));
            logger.info("L'element que no conté la informació per trobar la llista d'anys (nextPage) és: ".concat(paginatedList.toString()));
            throw new PreventiveException("currentBiggerYear no trobat");
        }
        return ret;
    }

    protected String relativeToAbsoluteUrl(String relative) {
        return AbstractGetRemoteProcess.relativeToAbsoluteUrl(getRemoteProcess.getUrl(), relative);
    }
    
    protected abstract T getResource(Element a);

    
///// ------ CLASS BvphBlockSearhIterator  ---------------------//

    private class BvphBlockSearhIterator implements Iterator<T>{
        Element elementToNextPage;
        Elements blocElements;
        int elementsToProcess=0;
                
        public BvphBlockSearhIterator() {
            updateValues();
            
            nextBlockIsNeeded = false;
            initilized = true;
        }
        
        

        @Override
        public boolean hasNext() {
            logger.debug(String.format("Elements actuals: %d", elementsToProcess));
            logger.debug(String.format("Hi hamés pàgines: %s", elementToNextPage!=null?"SI":"NO"));
            return elementToNextPage!=null || elementsToProcess>0;
        }

        @Override
        public T next() {
            T ret;
            Element a;
            if(elementsToProcess==0){
                logger.debug("S'ha acabta la pàgina actual, s'inicia la càrrega d'una nova pàgina (LoadingNextPage)");
                loadNextPage();
                updateValues();          
                logger.debug("nova pàgina carregada");
            }
            a = blocElements.get(blocElements.size()-elementsToProcess);
            --elementsToProcess;
            logger.debug("S'ha localitzat el registre. Es passa a extreure'n la informació");
            ret = getResource(a);
            logger.debug("Informació extreta");
            return ret;
        }
        
        private void loadNextPage(){
            if(elementToNextPage!=null){
                String url =  url = relativeToAbsoluteUrl(elementToNextPage.attr("href"));
                getRemoteProcessAux.setUrl(url);
                getRemoteProcessAux.setDefaultCookies(getRemoteProcess.getCookies());
//                if(getRemoteProcessAux.getCookies()==null || getRemoteProcessAux.getCookies().size()==0){
//                    getRemoteProcessAux.setCookies(getRemoteProcess.getCookies());
//                }
                sourceElement = getRemoteProcessAux.get();
            }
        }
        
        private void updateValues(){
            elementToNextPage = sourceElement.selectFirst(navPagesNextFilter);
            blocElements = sourceElement.select(newsPaperEditionListFilter);
            elementsToProcess = blocElements.size();
        }

//        private BvphResource getResource(Element a) {       
//            BvphResource ret;
//            String urlToDownloadJpg = downloadPdfJpg.concat("&").concat(Utils.urlQueryPath(a.child(0).attr("href")));
//            ret = new BvphResource(fragmentsFilter, actionsFilter, urlToDownloadJpg, titleFilter, editionDateBloc, patterToExtractDateFromTitle);
//            ret.updateFromElement(a, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
//            return ret;
//        }
    }    
}
