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

@XmlRootElement
public class HdSearchIterator extends SearchIterator<HdResource>{
    int cnt=0;
    @XmlElement
    private String navPagesFilter = "div#top-results div.results";
    @XmlElement
    private String navPagesNextFilter = "a#top-next";    //
    @XmlElement
    private String newsPaperEditionListFilter = "body form div.list div.list-frame";    
    @XmlElement
    private String idFilter = "input"; //relative to its father, a tag li got aplying  newsPaperEditionListFilter filter
    @XmlElement
    private String basicInfoNewsPaperListFilter = "div.list-record div a[id^=\"details\"]"; //relative to its father, a tag li got aplying  newsPaperEditionListFilter filter
    @XmlElement
    private String fragmentsFilter = "table#generic-pane tbody td.value textarea#text";
    @XmlElement
    private String savePdfFilter = "iframe#page-preview";
    @XmlElement
    private String titleFilter = "p#breadcrumbs em a[title=\"Títol\"]";
    @XmlElement
    private String pageNumFilter = "p#breadcrumbs em span[title=\"Pàgina\"]";
    @XmlElement
    private String editionDateFilter = "p#breadcrumbs em a[title=\"Exemplar\"]";
    @XmlElement
    private String noPdfFileUrl = "http://localhost:8888/files/nopdf.pdf";
    
    @XmlTransient
    private Element sourceElement;
    @XmlTransient
    private boolean initilized = false;
    @XmlTransient
    private HdBlockSearhIterator currentBlockIterator;
    @XmlTransient
    private boolean nextBlockIsNeeded;
    @XmlTransient
    private HdGetRemoteProcess getRemoteProcess;    
    @XmlTransient
    private AbstractGetRemoteProcess getRemoteProcessAux = new GetRemoteProcessWithoutParams();
    
    public HdSearchIterator(Element element,  HdGetRemoteProcess getRemoteProcess){
        this._init(element, getRemoteProcess);
    }
    
    public HdSearchIterator(HdGetRemoteProcess getRemoteProcess){
        this._init(getRemoteProcess);
    }

    public HdSearchIterator(){
    }
    
    public void init(AbstractGetRemoteProcess getRemoteProcess){
        this._init((HdGetRemoteProcess) getRemoteProcess);
    }

    public void init(Element element,  HdGetRemoteProcess getRemoteProcess){
        this._init(element, getRemoteProcess);
    }

    private void _init(HdGetRemoteProcess getRemoteProcess){
        this.getRemoteProcess = getRemoteProcess;
    }

    private void _init(Element element,  HdGetRemoteProcess getRemoteProcess){
        this._init(getRemoteProcess);
        this.sourceElement = element;
    }

    @Override
    public boolean hasNext() {
        boolean ret;
        
        checkNewBlock();
        
        ret = !this.noResources();
        ret = ret && hasNextInCurrentBlock();
        return ret;
    }

    @Override
    public HdResource next() {
        HdResource ret = null;
        ret = currentBlockIterator.next();
        return ret;
    }
    
    private void checkNewBlock(){
        if(!initilized){
            if(sourceElement==null){
                updateOriginalSource();
            }
            if(!noResources()){
                currentBlockIterator = new HdBlockSearhIterator();
            }
        }
    }
            
    protected boolean noResources(){
        boolean ret;
        ret = sourceElement.select(navPagesFilter)==null;        
        return ret;
    }
    
    private boolean hasNextInCurrentBlock() {
        boolean ret= false;
        if(currentBlockIterator!=null){
            ret = currentBlockIterator.hasNext();
        }
        return ret;
    }
    
    private void updateOriginalSource(){
        sourceElement = getRemoteProcess.get();
    }
    
    private String relativeToAbsoluteUrl(String relative){
        return  AbstractGetRemoteProcess.relativeToAbsoluteUrl(getRemoteProcess.getUrl(), relative);
    }
    
///// ------ CLASS HdBlockSearhIterator  ---------------------//

    private class HdBlockSearhIterator implements Iterator<HdResource>{
        Element elementToNextPage;
        Elements blocElements;
        int elementsToProcess=0;
                
        public HdBlockSearhIterator() {
            updateValues();
            
            nextBlockIsNeeded = false;
            initilized = true;
        }
        
        

        @Override
        public boolean hasNext() {
            return elementToNextPage!=null || elementsToProcess>0;
        }

        @Override
        public HdResource next() {
            HdResource ret;
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
                String url = relativeToAbsoluteUrl(elementToNextPage.attr("href"));
                getRemoteProcessAux.setUrl(url);
                getRemoteProcessAux.setCookies(getRemoteProcess.getCookies());
                sourceElement = getRemoteProcessAux.get();
            }
        }
        
        private void updateValues(){
            elementToNextPage = sourceElement.selectFirst(navPagesNextFilter);
            blocElements = sourceElement.select(newsPaperEditionListFilter);
            elementsToProcess = blocElements.size();
        }

        private HdResource getResource(Element elem) {
            HdResource ret =null;
            String id = elem.selectFirst(idFilter).val();
            Element basicInfoElem = elem.selectFirst(basicInfoNewsPaperListFilter);
            String urlInfoContent = AbstractGetRemoteProcess.relativeToAbsoluteUrl(getRemoteProcess.getUrl(), basicInfoElem.attr("href"));
            getRemoteProcessAux.setUrl(urlInfoContent);
            getRemoteProcessAux.setCookies(getRemoteProcess.getCookies());
            Element contentDocum = getRemoteProcessAux.get();
            ret = new HdResource(titleFilter, editionDateFilter, pageNumFilter, fragmentsFilter, savePdfFilter, noPdfFileUrl);
            ret.updateFromElement(contentDocum, id, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
            return ret;
        }
    }
}
