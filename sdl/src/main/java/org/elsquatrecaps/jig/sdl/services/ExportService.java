package org.elsquatrecaps.jig.sdl.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.EntityNotFoundException;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.util.Utils;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.elsquatrecaps.jig.sdl.exception.ErrorCopyingFileFormaException;
import org.elsquatrecaps.jig.sdl.exception.UnsupportedFormat;
import org.elsquatrecaps.jig.sdl.model.SearchResource;
import org.elsquatrecaps.jig.sdl.model.SearchResourceId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.elsquatrecaps.jig.sdl.persistence.SearchResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExportService {
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);

    SearchResourceRepository resourceRepository;
    DownloaderProperties dp;
    
    private final int HIGHLIGHT_PADDING = 15;
    

    public ExportService(SearchResourceRepository resourceRepository, DownloaderProperties dp) {
        this.resourceRepository = resourceRepository;
        this.dp = dp;
    }

//    public void exportResourcesById(String[] ids, String format) {
//        
//        exportResourcesById(ids, format, "", "");
//        
//    }
    
    public int exportResourcesById(String[] ids, String format, String process) {
        int ret =0;
        for (String id : ids) {
            id = id.replaceAll("\\|", ",");
            try{
                exportResourceById(id, format, process);
                ++ret;
            } catch (UnsupportedFormat e) {
            } catch(ErrorCopyingFileFormaException e){
                throw new ErrorCopyingFileFormaException("Error en copiar el fitxer: ".concat(id));
            }
        }
        return ret;
    }

    
    public int exportResourcesById(String id, String formats[], String process) {
        int ret =0;
        for (String format : formats) {
            id = id.replaceAll("\\|", ",");
            try{
                exportResourceById(id, format, process);
                ++ret;
            } catch (UnsupportedFormat e) {
            } catch(ErrorCopyingFileFormaException e){
                throw new ErrorCopyingFileFormaException("Error en copiar el fitxer: ".concat(id));
            }
        }
        return ret;
    }

    
    public void exportResourceById(String id, String format, String process) {
        String[] aId = id.split(",");
        SearchResource ret;
        Optional<SearchResource> optional = resourceRepository.findById(new SearchResourceId(aId[0], aId[1], aId[2]));
        if (optional.isPresent()) {
            ret = optional.get();
        } else {
            throw new EntityNotFoundException("Resource", "id", id);
        }

        String fileExportPath;
        fileExportPath = this.dp.getLocalExportPath().trim();

        FileOutputStream fileOutputStream = null;
        File path = new File(fileExportPath);
        
        // ALERTA[Xavi] només cal per les imatges jpg (de moment)
        String baseName = ret.getResource().getFileName(format);        
        if(aId[0].length()>0){
            baseName = baseName.concat("_").concat(Utils.buildNormalizedFilename(aId[0]));
        }
        if(aId[1].length()>0){
            baseName = baseName.concat("_").concat(Utils.buildNormalizedFilename(aId[1]));
        }

        
        File file = new File(fileExportPath, baseName.concat(".").concat(format));
        
        ret.getResource().setLocalFilePath(dp.getLocalReasourceRepo());
        ret.addProcessingAnalysis(process);
        FormatedFile ff = ret.getResource().getFormatedFile(format);
        if (!path.exists()) {
            path.mkdirs();
        }
        
        
        
        if (!file.exists()) {
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
            }
            
            if (format.equals("jpg") && aId[1].length()>0) {
                File inFile = new File(this.dp.getLocalReasourceRepo(), ff.getFileName());
                if(ret.getResource().isFormatSupported("xml")){
                    exportHighlightedImage(ret.getResource(), inFile, file, aId[1]);                        
                }else{
                    Utils.copyToFile(ff.getImInputStream(), fileOutputStream);         
                }
            } else {
                Utils.copyToFile(ff.getImInputStream(), fileOutputStream);            
            }
            
            resourceRepository.saveAndFlush(ret);
        } else {
            logger.info(String.format("El fitxer %s ja existeix, no el copiem", file.getName()));
        }
    }
    
    private void exportHighlightedImageFromWholePhrase(Resource resource, File inFile, File outFile, String pCriteria) {
         // TODO: Obtenir del XML les coordenades on s'ha de dibuixar el resaltat
        double totWidth =  0;
        double totHeight = 0;
        int criteriaInd;
        
        // TODO: Obtenir del XML les coordenades on s'ha de dibuixar el resaltat
        FormatedFile XMLFormatedFile = resource.getFormatedFile("xml");
        File XMLFile = new File(this.dp.getLocalReasourceRepo(), XMLFormatedFile.getFileName());
        
        pCriteria = pCriteria.substring(1, pCriteria.length()-1);
        String[] aCriteria = pCriteria.split(" +");
        
        for(int x=0; x<aCriteria.length; x++){
            if(aCriteria[x]!=null && !aCriteria[x].isEmpty()){
                aCriteria[x] = aCriteria[x].trim().replaceAll("\\*", ".*").replaceAll("\\?", ".?");
            }
            if(x==0){
                aCriteria[x] = "\\p{Punct}*".concat(aCriteria[x]);
            }
            if(x==aCriteria.length-1){
                aCriteria[x] = aCriteria[x].concat("\\p{Punct}*");
            }
        }
        

        FileInputStream XMLInputStream;
        List<Rectangle> rectangles = new ArrayList<>();

        try {
            XMLInputStream = new FileInputStream(XMLFile);
            Document doc = Jsoup.parse(XMLInputStream, null, "", Parser.xmlParser());
            Element page = doc.getElementsByTag("a:page").first();            
            if(page==null){
                page = doc.getElementsByTag("page").first();            
            }
            totWidth =  Integer.parseInt(page.attr("WIDTH"));
            totHeight =  Integer.parseInt(page.attr("HEIGHT"));
            criteriaInd =0;
            while(criteriaInd<aCriteria.length && (aCriteria[criteriaInd]==null || aCriteria[criteriaInd].isEmpty())) {
                criteriaInd++;
            }            
            if(criteriaInd<aCriteria.length){
                Pattern pattern = Pattern.compile(aCriteria[criteriaInd]
                        ,Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.UNICODE_CHARACTER_CLASS);
                Elements elements = doc.getElementsByAttributeValueMatching("CONTENT", pattern);
//                Elements elements = doc.select("[CONTENT~=".concat(aCriteria[criteriaInd]).concat("]"));
                
                for(Element element: elements){
                    Elements elementsList = new Elements();
                    if(matchContentElementWithCriteria(element, aCriteria, criteriaInd+1, elementsList)){
                        addRectangles(elementsList, rectangles);
                    }
                }
                elements = doc.getElementsByAttributeValueMatching("SUBS_CONTENT", pattern);                
//                elements = doc.select("[SUBS_CONTENT*=".concat(aCriteria[criteriaInd]).concat("]"));

                for(Element element: elements){
                    Elements elementsList = new Elements();
                    if(matchContentElementWithCriteria(element, aCriteria, criteriaInd+1, elementsList)){
                        Element hyphenSibling = nextSiblingStringFromString(element); 
                        if(hyphenSibling!=null){
                            elementsList.add(hyphenSibling);
                        }
                        addRectangles(elementsList, rectangles);
                    }
                }

            }
            try {
                BufferedImage br = ImageIO.read(inFile);
                Graphics2D g = br.createGraphics();
                g.setColor(new Color(1f, 0.1f, 0, 0.5f));

                for (Rectangle rectangle : rectangles) {
                    double factorh = br.getHeight()/totHeight;
                    double factorw = br.getWidth()/totWidth;
                    g.fillRect((int)(rectangle.x*factorw), (int)(rectangle.y*factorh), (int)(rectangle.width*factorw), (int)(rectangle.height*factorh));
                }

                g.dispose();

                ImageIO.write(br, "jpg", outFile);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    private void exportHighlightedImageFromSeparatedWords(Resource resource, File inFile, File outFile, String pCriteria) {
         // TODO: Obtenir del XML les coordenades on s'ha de dibuixar el resaltat
        double totWidth =  0;
        double totHeight = 0;
        
        // TODO: Obtenir del XML les coordenades on s'ha de dibuixar el resaltat
        FormatedFile XMLFormatedFile = resource.getFormatedFile("xml");
        File XMLFile = new File(this.dp.getLocalReasourceRepo(), XMLFormatedFile.getFileName());
        
        String[] aCriteria = pCriteria.split(" +");
        
        for(int x=0; x<aCriteria.length; x++){
            if(aCriteria[x]!=null && !aCriteria[x].isEmpty()){
                aCriteria[x] = aCriteria[x].trim().replaceAll("\\*", ".*").replaceAll("\\?", ".?");
            }
            aCriteria[x] = "\\p{Punct}*".concat(aCriteria[x]).concat("\\p{Punct}*");
        }

        FileInputStream XMLInputStream;
        List<Rectangle> rectangles = new ArrayList<>();

        try {
            XMLInputStream = new FileInputStream(XMLFile);
            Document doc = Jsoup.parse(XMLInputStream, null, "", Parser.xmlParser());
            Element page = doc.getElementsByTag("a:page").first();            
            if(page==null){
                page = doc.getElementsByTag("page").first();            
            }
            totWidth =  Integer.parseInt(page.attr("WIDTH"));
            totHeight =  Integer.parseInt(page.attr("HEIGHT"));
            for(String criteria: aCriteria){
                if(criteria==null || criteria.isEmpty()){
                    continue;
                }
                Pattern pattern = Pattern.compile(criteria
                        ,Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.UNICODE_CHARACTER_CLASS);
                Elements elements = doc.getElementsByAttributeValueMatching("CONTENT", pattern);    
//                Elements elements = doc.select("[CONTENT*=".concat(criteria).concat("]"));
                addRectangles(elements, rectangles);
                
                elements = doc.getElementsByAttributeValueMatching("SUBS_CONTENT", pattern);                
//                elements = doc.select("[SUBS_CONTENT*=".concat(criteria).concat("]")); 
                ArrayList<Element> list = new ArrayList<>(elements);
                for(Element element: list){
                    Element hyphenSibling = nextSiblingStringFromString(element); 
                    if(hyphenSibling!=null){
                        elements.add(hyphenSibling);
                    }
                }
                addRectangles(elements, rectangles);

            }
            try {
                BufferedImage br = ImageIO.read(inFile);
                Graphics2D g = br.createGraphics();
                g.setColor(new Color(1f, 0.1f, 0, 0.5f));

                for (Rectangle rectangle : rectangles) {
                    double factorh = br.getHeight()/totHeight;
                    double factorw = br.getWidth()/totWidth;
                    g.fillRect((int)(rectangle.x*factorw), (int)(rectangle.y*factorh), (int)(rectangle.width*factorw), (int)(rectangle.height*factorh));
                }

                g.dispose();

                ImageIO.write(br, "jpg", outFile);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    // TODO: Moure això al util i afegir el format per poder gestionar altrs tipus defitexer
    private void exportHighlightedImage(Resource resource, File inFile, File outFile, String pCriteria) {
        pCriteria = pCriteria.trim();
        if(pCriteria.charAt(0)=='"' && pCriteria.charAt(pCriteria.length()-1)=='"'){
            //allCriteria
            exportHighlightedImageFromWholePhrase(resource, inFile, outFile, pCriteria);
        }else{
            //singleCriteria
            exportHighlightedImageFromSeparatedWords(resource, inFile, outFile, pCriteria);
        }                
    }
    
    private void addRectangles(Elements elements, List<Rectangle> rectangles){
        for ( Element element : elements) {

            int x = Integer.parseInt(element.attr("HPOS"))-HIGHLIGHT_PADDING;
            int y = Integer.parseInt(element.attr("VPOS"))-HIGHLIGHT_PADDING;
            int width = Integer.parseInt(element.attr("WIDTH"))+HIGHLIGHT_PADDING*2;
            int height = Integer.parseInt(element.attr("HEIGHT"))+HIGHLIGHT_PADDING*2;

            rectangles.add(new Rectangle(x, y, width, height));
        }
    }

    private boolean matchContentElementWithCriteria(Element element, String[] aCriteria, int criteriaInd, Elements elementsList) {
        boolean ret=false;
        Element siblingElement;
        while(criteriaInd<aCriteria.length && (aCriteria[criteriaInd]==null || aCriteria[criteriaInd].isEmpty())) {
            criteriaInd++;
        }  
        if(criteriaInd>=aCriteria.length){
            ret = true;
        }else{
            siblingElement = nextSiblingStringFromString(element);                       
            Pattern pattern = Pattern.compile(aCriteria[criteriaInd]
                        ,Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.UNICODE_CHARACTER_CLASS);
            if(siblingElement!=null && (pattern.matcher(siblingElement.attr("CONTENT").trim()).matches() ||
                    pattern.matcher(siblingElement.attr("SUBS_CONTENT").trim()).matches())){
                ret = matchContentElementWithCriteria(siblingElement, aCriteria, ++criteriaInd, elementsList);
            } 
        }
        if(ret){
            elementsList.add(element);
        }
        return ret;
    }
    
    private Element nextSiblingStringFromString(Element element){
        Element siblingElement = null;
        siblingElement = stringFromStringLevel(element.nextElementSibling());
        if(siblingElement==null){
            Element siblingTextLine = element.parent().nextElementSibling();
            siblingElement = nextSiblingStringFromTextLine(element.parent());
        }        
        return siblingElement;
    }
    
    private Element elementFromElementLevel(Element element, String tagname){
        while(element!=null && !element.tagName().equalsIgnoreCase(tagname)) {
            element = element.nextElementSibling();
        }
        return element;
    }

    private Element stringFromStringLevel(Element element){
        return elementFromElementLevel(element, "a:String");
    }
    
    private Element nextSiblingStringFromTextLine(Element parentTextLine){
        Element siblingElement = null;
        Element siblingTextLine = textLineFromTextLineLevel(parentTextLine.nextElementSibling());
        if(siblingTextLine==null){
            siblingElement = nextSiblingStringFromTextBlock(parentTextLine.parent());
        }else{
            siblingElement = stringFromStringLevel(siblingTextLine.firstElementChild());
        }
        return siblingElement;
    }
    
    private Element textLineFromTextLineLevel(Element element){
        return elementFromElementLevel(element, "a:TextLine");
    }
    
    private Element nextSiblingStringFromTextBlock(Element parentTextBlock){
        Element siblingElement = null;
        Element siblingTextLine =null;
        Element siblingtextBlock = textBlockFromTextBlockLevel(parentTextBlock.nextElementSibling());
        if(siblingtextBlock==null){
            siblingElement = nextSiblingStringFromPrintSpace(parentTextBlock.parent());
        }else{
            siblingTextLine = textLineFromTextLineLevel(siblingtextBlock.firstElementChild());
            if(siblingTextLine!=null){
                siblingElement = stringFromStringLevel(siblingTextLine.firstElementChild());
            }
        }        
        return siblingElement;
    }
    
    private Element textBlockFromTextBlockLevel(Element element){
        return elementFromElementLevel(element, "a:TextBlock");
    }
    
    private Element nextSiblingStringFromPrintSpace(Element parentPrintSpace){
        Element siblingElement = null;
        Element siblingTextLine =null;
        Element siblingtextBlock = null;
        Element siblingtextPrintSpace = printSpaceFromPrintSpaceLevel(parentPrintSpace.nextElementSibling());
        if(siblingtextPrintSpace==null){
            siblingElement = nextSiblingStringFromPage(parentPrintSpace.parent());
        }else{
            siblingtextBlock = textBlockFromTextBlockLevel(siblingtextPrintSpace.firstElementChild());
            if(siblingtextBlock!=null){
                siblingTextLine = textLineFromTextLineLevel(siblingtextBlock.firstElementChild());
                if(siblingTextLine!=null){
                    siblingElement = stringFromStringLevel(siblingTextLine.firstElementChild());
                }
            }
        }        
        return siblingElement;
    }
    
    private Element printSpaceFromPrintSpaceLevel(Element element){
        return elementFromElementLevel(element, "a:PrintSpace");
    }
    
    private Element nextSiblingStringFromPage(Element parentPage){
        Element siblingElement = null;
        Element siblingTextLine =null;
        Element siblingtextBlock = null;
        Element siblingPrintSpace = null;
        Element siblingPage = printSpaceFromPrintSpaceLevel(parentPage.nextElementSibling());
        if(siblingPage==null){
            siblingElement = nextSiblingStringFromLayout(parentPage.parent());
        }else{
            siblingPrintSpace = printSpaceFromPrintSpaceLevel(siblingPage.firstElementChild());
            if(siblingPrintSpace!=null){
                siblingtextBlock = textBlockFromTextBlockLevel(siblingPrintSpace.firstElementChild());
                if(siblingtextBlock!=null){
                    siblingTextLine = textLineFromTextLineLevel(siblingtextBlock.firstElementChild());
                    if(siblingTextLine!=null){
                        siblingElement = stringFromStringLevel(siblingTextLine.firstElementChild());
                    }
                }
            }
        }        
        return siblingElement;
    }
    
    private Element pageFromPageLevel(Element element){
        return elementFromElementLevel(element, "a:Page");
    }

    private Element nextSiblingStringFromLayout(Element parentLayout){
        Element siblingElement = null;
        Element siblingTextLine =null;
        Element siblingtextBlock = null;
        Element siblingPrintSpace = null;
        Element siblingPage = null;
        Element siblingLayout = layoutFromLayoutLevel(parentLayout.nextElementSibling());
        if(siblingLayout!=null){
            siblingPrintSpace = printSpaceFromPrintSpaceLevel(siblingPage.firstElementChild());
            if(siblingPrintSpace!=null){
                siblingtextBlock = textBlockFromTextBlockLevel(siblingPrintSpace.firstElementChild());
                if(siblingtextBlock!=null){
                    siblingTextLine = textLineFromTextLineLevel(siblingtextBlock.firstElementChild());
                    if(siblingTextLine!=null){
                        siblingElement = stringFromStringLevel(siblingTextLine.firstElementChild());
                    }
                }
            }
        }        
        return siblingElement;
    }
    
    private Element layoutFromLayoutLevel(Element element){
        return elementFromElementLevel(element, "a:Layout");
    }

}
