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
import javax.imageio.ImageIO;
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
    
    public void exportResourcesById(String[] ids, String format, String process) {
        for (String id : ids) {
            id = id.replaceAll("\\|", ",");
            exportResourceById(id, format, process);
        }
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
        fileExportPath = this.dp.getLocalExportPath();

        FileOutputStream fileOutputStream = null;
        File path = new File(fileExportPath);
        
        // ALERTA[Xavi] només cal per les imatges jpg (de moment)
        String baseName = ret.getResource().getFileName();        
        if (aId[0].equalsIgnoreCase("BVPH") && format.equals("jpg") && aId[1].length()>0) {
            baseName = baseName.concat("_").concat(Utils.buildNormalizedFilename(aId[1]));
        }else if(aId[1].length()>0){
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
                exportHighlightedImage(ret.getResource(), inFile, file, aId[1]);
                        
                        
            } else {
                Utils.copyToFile(ff.getImInputStream(), fileOutputStream);
            
            }
            
            resourceRepository.saveAndFlush(ret);
        } else {
            logger.info(String.format("El fitxer %s ja existeix, no el copiem", file.getName()));
        }
    }
    
    // TODO: Moure això al util i afegir el format per poder gestionar altrs tipus defitexer
    private void exportHighlightedImage(Resource resource, File inFile, File outFile, String pCriteria) {
        // TODO: Obtenir del XML les coordenades on s'ha de dibuixar el resaltat
        double totWidth =  0;
        double totHeight = 0;
        
        FormatedFile XMLFormatedFile = resource.getFormatedFile("xml");
        File XMLFile = new File(this.dp.getLocalReasourceRepo(), XMLFormatedFile.getFileName());
        
        
        
        FileInputStream XMLInputStream;
        List<Rectangle> rectangles = new ArrayList<>();
        String[] aCriteria = pCriteria.split("\\s|\\p{Punct}");
        pCriteria = pCriteria.replaceAll("()", ".*?)|(.*?");
        
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
                Elements elements = doc.select("[CONTENT*=".concat(criteria).concat("]"));
                addRectangles(elements, rectangles);
                
                elements = doc.select("[SUBS_CONTENT*=".concat(criteria).concat("]"));
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
    
    private void addRectangles(Elements elements, List<Rectangle> rectangles){
        for ( Element element : elements) {

            int x = Integer.parseInt(element.attr("HPOS"))-HIGHLIGHT_PADDING;
            int y = Integer.parseInt(element.attr("VPOS"))-HIGHLIGHT_PADDING;
            int width = Integer.parseInt(element.attr("WIDTH"))+HIGHLIGHT_PADDING*2;
            int height = Integer.parseInt(element.attr("HEIGHT"))+HIGHLIGHT_PADDING*2;

            rectangles.add(new Rectangle(x, y, width, height));
        }
    }
    
}
