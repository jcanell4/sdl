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
public class ArcaSearchIterator extends SearchIterator<ArcaResource>{
    int cnt=0;
    @XmlElement
    private String navPagesFilter = "#cdmResultsBrowseAllItemsView";
    @XmlElement
    private String navPagesNextFilter = "div#link_bar_search div#link_bar_content div#link_bar_container div.link_bar_pagination ul > li#pagination_button_next a#pagination_button_next_link";    //
    @XmlElement
    private String newsPaperEditionListFilter = "#cdmResultsBrowseAllItemsView div.listItem";    
    @XmlElement
    private String basicInfoNewsPaperListFilter = "div.listItem div.listContentBottom a.body_link_11"; //.child[0].attr("href")   //relative to its father, a tag li got aplying  newsPaperEditionListFilter filter
    @XmlElement
    private String fragmentsFilter = "div#img_view_text_container div#img_view_text_content pre#full_text_container";
    @XmlElement
    private String savePdfFilter = "div#img_view_container div#viewer_wrapper_outer div#viewer_wrapper_inner object#itemViewer embed";
    @XmlElement
    private String editionDateFilter = "td#metadata_data a.body_link_11";
    @XmlElement
    private String noPdfFileUrl = "http://localhost:8888/files/nopdf.pdf";
    
    @XmlTransient
    private Element sourceElement;
    @XmlTransient
    private boolean initilized = false;
    @XmlTransient
    private ArcaBlockSearhIterator currentBlockIterator;
    @XmlTransient
    private boolean nextBlockIsNeeded;
    @XmlTransient
    private ArcaGetRemoteProcess getRemoteProcess;    
    @XmlTransient
    private GetRemoteProcess getRemoteProcessAux = new GetRemoteProcess();

    
    public ArcaSearchIterator(Element element,  ArcaGetRemoteProcess getRemoteProcess){
        this._init(element, getRemoteProcess);
    }
    
    public ArcaSearchIterator(ArcaGetRemoteProcess getRemoteProcess){
        this._init(getRemoteProcess);
    }

    public ArcaSearchIterator(){
    }
    
    public void init(GetRemoteProcess getRemoteProcess){
        this._init((ArcaGetRemoteProcess) getRemoteProcess);
    }

    public void init(Element element,  ArcaGetRemoteProcess getRemoteProcess){
        this._init(element, getRemoteProcess);
    }

    private void _init(ArcaGetRemoteProcess getRemoteProcess){
        this.getRemoteProcess = getRemoteProcess;
    }

    private void _init(Element element,  ArcaGetRemoteProcess getRemoteProcess){
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
    public ArcaResource next() {
        ArcaResource ret = null;
        ret = currentBlockIterator.next();
        return ret;
    }
    
    private void checkNewBlock(){
        if(!initilized){
            if(sourceElement==null){
                updateOriginalSource();
            }
            if(!noResources()){
                currentBlockIterator = new ArcaBlockSearhIterator();
            }
        }
    }
            
    protected boolean noResources(){
        boolean ret;
        ret = sourceElement.select(navPagesFilter).size()==0;        
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
        return  GetRemoteProcess.relativeToAbsoluteUrl(getRemoteProcess.getUrl(), relative);
    }
    
///// ------ CLASS ArcaBlockSearhIterator  ---------------------//

    private class ArcaBlockSearhIterator implements Iterator<ArcaResource>{
        Element elementToNextPage;
        Elements blocElements;
        int elementsToProcess=0;
                
        public ArcaBlockSearhIterator() {
            updateValues();
            
            nextBlockIsNeeded = false;
            initilized = true;
        }
        
        

        @Override
        public boolean hasNext() {
            return elementToNextPage!=null || elementsToProcess>0;
        }

        @Override
        public ArcaResource next() {
            ArcaResource ret;
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

        private ArcaResource getResource(Element elem) {      
            Element basicInfoElem = elem.selectFirst(basicInfoNewsPaperListFilter);
            String urlInfoContent = GetRemoteProcess.relativeToAbsoluteUrl(getRemoteProcess.getUrl(), basicInfoElem.attr("href"));
            getRemoteProcessAux.setUrl(urlInfoContent);
            getRemoteProcessAux.setCookies(getRemoteProcess.getCookies());
            Element contentDocum = getRemoteProcessAux.get();
            ArcaResource ret = new ArcaResource(editionDateFilter, fragmentsFilter, getRemoteProcess._getText(), savePdfFilter, noPdfFileUrl);
            ret.updateFromElement(basicInfoElem, contentDocum, getRemoteProcess.getUrl(), getRemoteProcess.getCookies());
            return ret;
        }
    }
}
