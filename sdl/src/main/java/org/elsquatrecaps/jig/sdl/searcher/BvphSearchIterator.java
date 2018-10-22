/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.searcher;

import java.util.Iterator;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class BvphSearchIterator extends SearchIterator<BvphResource>{
    private static final Logger logger = LoggerFactory.getLogger(BvphSearchIterator.class);
    
    int cnt=0;
    @XmlElement
    private int numResourcesThreshold = 10000;
    @XmlElement
    private String thereIsTooMuchFilter = "div#consulta_resultados_sumario div.resultados_opciones p.warning span.texto_warning strong"; 
    @XmlElement
    private String descendingOrderFilter = "#consulta_resultados_sumario div.resultados_opciones p.resultados_orden span.valor span.campo_fechapublicacionorden span.links_orden span.enlace_cambio span.boton_orden_descendente a";    
    @XmlElement
    private String navPagesFilter = "#navegacion_resultados div.nav_marco div.nav_paginas span.nav_descrip";    
    @XmlElement
    private String navPagesNextFilter = "#navegacion_resultados div.nav_marco div.nav_paginas span.nav_alante a#boton_siguiente";    
    @XmlElement
    private String newsPaperEditionListFilter = "#navegacion_resultados div.nav_marco ol#nav_registros li ul>li.unidad_textual";    
    @XmlElement
    private String pageNewsPaperListFilter = "ul>li.unidad_textual"; //.child[0].attr("href")   //relative to its father, a tag li got aplying  newsPaperEditionListFilter filter
    @XmlElement
    private String morePubYearFilter = "#contenidos_anyopublicacion ul li.enlacemas a";    
    @XmlElement
    private String mainPublicationYearsFilter = "dd#contenidos_anyopublicacion ul li";    
    @XmlElement
    private String pubYearPaginatedRegistersFilter = "ol#nav_registros.nav_registros li";    
    @XmlElement
    private String pubYearPaginatedRegistersNextPageFilter = "a#boton_siguiente";    
    @XmlElement
    private String fragmentsFilter = "ul.texto_ocurrencias li";
    @XmlElement
    private String actionsFilter = "div#tab_acciones ul li";
    @XmlElement
    private String saveJpgFilter = "ol#nav_registros div.visualizador_menu span#grupo_1 a";
    @XmlElement
    private String titleFilter = "dt span span.titulo a";
    @XmlElement
    private String pageFilter = "p strong a";
    @XmlElement
    private String editionDateBloc = "dt span span.datos_publicacion bdi";
    @XmlElement
    private String downloadPdfJpg = "http://prensahistorica.mcu.es/es/catalogo_imagenes/iniciar_descarga.cmd";
    @XmlElement
    private String patterToExtractDateFromTitle = ".*(\\d{4}\\s+([Ee]nero|[Ff]ebrero|[Mm]arzo|[Aa]bril|[Mm]ayo|[Jj]unio|[Jj]ulio|[Aa]gosto|[Ss]eptiembre|[Oo]ctubre|[Nn]oviembre|[Dd]iciembre)\\s+\\d{2}).*";

//    @XmlElement
//    private String noResourcesText = "No hay resultados"; 
//    @XmlElement
//    private String noResourcesFilter = "div#consulta_resultados_sumario div.navegacion_resultados p";    
//    @XmlElement
//    private String unfulfilledContitionsText = "No hay ningún registro que cumpla las condiciones de búsqueda.";    
    
    @XmlTransient
    private Element sourceElement;
    @XmlTransient
    private boolean initilized = false;
    @XmlTransient
    private BvphBlockSearhIterator currentBlockIterator;
    @XmlTransient
    private boolean nextBlockIsNeeded;
    @XmlTransient
    private BvphGetRemoteProcess getRemoteProcess;    
    @XmlTransient
    private GetRemoteProcess getRemoteProcessAux = new GetRemoteProcess();
    @XmlTransient
    private int currentBiggerYear;
    @XmlTransient
    private int currentSmallerYear=-1;

    
    public BvphSearchIterator(Element element,  BvphGetRemoteProcess getRemoteProcess){
        this._init(element, getRemoteProcess);
    }
    
    public BvphSearchIterator(BvphGetRemoteProcess getRemoteProcess){
        this._init(getRemoteProcess);
    }

    public BvphSearchIterator(){
    }
    
    public void init(GetRemoteProcess getRemoteProcess){
        this._init((BvphGetRemoteProcess) getRemoteProcess);
    }

    public void init(Element element,  BvphGetRemoteProcess getRemoteProcess){
        this._init(element, getRemoteProcess);
    }

    private void _init(BvphGetRemoteProcess getRemoteProcess){
        this.getRemoteProcess = getRemoteProcess;
        currentBiggerYear = getRemoteProcess.getDefaultBiggerYear();
    }

    private void _init(Element element,  BvphGetRemoteProcess getRemoteProcess){
        this._init(getRemoteProcess);
        this.sourceElement = element;
        currentBiggerYear = getRemoteProcess.getDefaultBiggerYear();
    }

    @Override
    public boolean hasNext() {
        boolean ret;
        
        checkNewBlock();
        
        ret = !this.noResources();
        ret = ret && hasNextInCurrentBlock();
        
        logger.debug(String.format("hasNext: %s", (ret?"si":"no")));
        
        return ret;
    }

    @Override
    public BvphResource next() {
        BvphResource ret = null;
        ret = currentBlockIterator.next();
        logger.debug(String.format("next: %s", ret.getTitle()));
        return ret;
    }
    
    protected int getCurrentBiggerYear(){
        return currentBiggerYear;
    }

    protected int getCurrentSmallerYear(){
        return currentSmallerYear;
    }

    private void checkNewBlock(){
        if(!initilized){
            if(sourceElement==null){
                updateOriginalSource();
            }
            if(!noResources()){
                currentBlockIterator = new BvphBlockSearhIterator();
            }
        }else{
            nextBlockIsNeeded = !hasNextInCurrentBlock() && thereIsTooMuchResources();
        }
        if(nextBlockIsNeeded){    
            updateOriginalSource(getCurrentSmallerYear());
            currentBlockIterator = new BvphBlockSearhIterator();
        }
    }
            
    protected boolean noResources(){
        boolean ret;
        Element elem;
        String message="";
        ret = sourceElement.select(navPagesFilter).size()==0;        
        return ret;
    }
    
    private boolean thereIsTooMuchResources(Element e){
        Element elem;
        int moreThanThreshold=0;
        elem = e.selectFirst(thereIsTooMuchFilter);
        if(elem!=null){
            moreThanThreshold = Integer.valueOf(elem.text());
        }
        return moreThanThreshold > numResourcesThreshold;
    }
    
    protected boolean thereIsTooMuchResources(){
        return thereIsTooMuchResources(sourceElement);
    }

    private boolean hasNextInCurrentBlock() {
        boolean ret= false;
        if(currentBlockIterator!=null){
            ret = currentBlockIterator.hasNext();
        }
        return ret;
    }
    
    private void updateOriginalSource(){
        updateOriginalSource(currentBiggerYear);
    }
    
    private void updateOriginalSource(int currentBiggerYear){
        Element aElement;

        getRemoteProcess.setBiggerYear(currentBiggerYear);
        sourceElement = getRemoteProcess.get();
        if(thereIsTooMuchResources()){
            aElement = sourceElement.selectFirst(descendingOrderFilter);
            if(aElement!=null){
                String url = relativeToAbsoluteUrl(aElement.attr("href"));
                getRemoteProcessAux.setUrl(url);
                sourceElement = getRemoteProcessAux.get();
                updateCurrentSmallerYear();
            }
        }
    }
    
    private void updateCurrentSmallerYear(){
        Element aElem = sourceElement.selectFirst(morePubYearFilter);
        if(aElem!=null){
            //paging publication years
            String url = relativeToAbsoluteUrl(aElem.attr("href"));
            getRemoteProcessAux.setUrl(url);
            currentSmallerYear = getMinimumYearOfPaginatedList(getRemoteProcessAux.get());
        }else{
            //navigate in default list
            currentSmallerYear = getMinimumYearOfSingleList(sourceElement.select(mainPublicationYearsFilter));
        }
    }
    
    private int getMinimumYearOfSingleList(Elements list){
        int value;
        int ret = Integer.valueOf(list.get(0).child(0).text());
        for(Element elem: list){
            value = Integer.valueOf(elem.child(0).text());
            if(value<ret){
                ret=value;
            }
        }
        return ret;
    }
    
    private int getMinimumYearOfPaginatedList(Element paginatedList){
        Element nextPage = paginatedList.selectFirst(pubYearPaginatedRegistersNextPageFilter);
        String url;
        int ret=currentSmallerYear;      
        logger.debug("Cercant getMinimumYearOfPaginatedList");
        do{
            Elements list = paginatedList.select(pubYearPaginatedRegistersFilter);
            int value = getMinimumYearOfSingleList(list);
            if(value<ret || ret==-1){
                ret = value;
            }
            url = relativeToAbsoluteUrl(nextPage.attr("href"));
            getRemoteProcessAux.setUrl(url);
            paginatedList = getRemoteProcessAux.get();
            nextPage = paginatedList.selectFirst(pubYearPaginatedRegistersNextPageFilter);
        }while(nextPage!=null);
        logger.debug("getMinimumYearOfPaginatedList trobat");
        return ret;
    }
    
    private String relativeToAbsoluteUrl(String relative){
        return  GetRemoteProcess.relativeToAbsoluteUrl(getRemoteProcess.getUrl(), relative);
    }
    
///// ------ CLASS BvphBlockSearhIterator  ---------------------//

    private class BvphBlockSearhIterator implements Iterator<BvphResource>{
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
            return elementToNextPage!=null || elementsToProcess>0;
        }

        @Override
        public BvphResource next() {
            BvphResource ret;
            Element a;
            if(elementsToProcess==0){
                loadNextPage();
                updateValues();                
            }
            a = blocElements.get(blocElements.size()-elementsToProcess);
            --elementsToProcess;
            ret = getResource(a);
            
            return ret;
        }
        
        private void loadNextPage(){
            if(elementToNextPage!=null){
                String url =  url = relativeToAbsoluteUrl(elementToNextPage.attr("href"));
                getRemoteProcessAux.setUrl(url);
                sourceElement = getRemoteProcessAux.get();
            }
        }
        
        private void updateValues(){
            elementToNextPage = sourceElement.selectFirst(navPagesNextFilter);
            blocElements = sourceElement.select(newsPaperEditionListFilter);
            elementsToProcess = blocElements.size();
        }

        private BvphResource getResource(Element a) {       
            BvphResource ret = new BvphResource(fragmentsFilter, actionsFilter, saveJpgFilter, titleFilter, pageFilter, editionDateBloc, patterToExtractDateFromTitle);
            ret.updateFromElement(a, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
            return ret;
        }
    }
}
