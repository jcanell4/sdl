package org.elsquatrecaps.jig.sdl.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.configuration.DownloaderProperties;
import org.elsquatrecaps.jig.sdl.exception.EntityNotFoundException;
import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

@Service
public class ExportService {

    ResourceRepository resourceRepository;
    DownloaderProperties dp;
    
    private final int HIGHLIGHT_PADDING = 7;
    

    public ExportService(ResourceRepository resourceRepository, DownloaderProperties dp) {
        this.resourceRepository = resourceRepository;
        this.dp = dp;
    }

    public void exportResourcesById(String[] ids, String format) {
        
        exportResourcesById(ids, format, "");
        
    }
    
    public void exportResourcesById(String[] ids, String format, String criteria) {
        for (String id : ids) {
            exportResourceById(id, format, criteria);
        }
    }

    
    public void exportResourceById(String id, String format, String criteria) {
        System.out.println("Exportant " + id + " - " + format);

        Resource ret;
        Optional<Resource> optional = resourceRepository.findById(id);
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
        String baseName = ret.getFileName();        
        if (format.equals("jpg") && criteria.length()>0) {
            baseName = baseName.concat("_").concat(criteria);
        }
        
        File file = new File(fileExportPath, baseName.concat(".").concat(format));
        
        ret.setLocalFilePath(dp.getLocalReasourceRepo());
        FormatedFile ff = ret.getFormatedFile(format);
        if (!path.exists()) {
            path.mkdirs();
        }
        
        
        
        if (!file.exists()) {
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
            }
            
            if (format.equals("jpg") && criteria.length()>0) {
                File inFile = new File(this.dp.getLocalReasourceRepo(), ff.getFileName());
                exportHighlightedImage(ret, inFile, file, criteria);
                        
                        
            } else {
                Utils.copyToFile(ff.getImInputStream(), fileOutputStream);
            
            }
            
            
            
            
            System.out.println("Fitxer copiat");
        } else {
            System.out.println("El fitxer ja existeix, no el copiem");
        }
    }
    
    // TODO: Moure això al util i afegir el format per poder gestionar altrs tipus defitexer
    private void exportHighlightedImage(Resource resource, File inFile, File outFile, String criteria) {
        // TODO: Obtenir del XML les coordenades on s'ha de dibuixar el resaltat
        
        FormatedFile XMLFormatedFile = resource.getFormatedFile("xml");
        File XMLFile = new File(this.dp.getLocalReasourceRepo(), XMLFormatedFile.getFileName());
        
        
        
        FileInputStream XMLInputStream;
        List<Rectangle> rectangles = new ArrayList<>();
        
        try {
            XMLInputStream = new FileInputStream(XMLFile);
            Document doc = Jsoup.parse(XMLInputStream, null, "", Parser.xmlParser());
            Elements elements = doc.select("[CONTENT*=".concat(criteria).concat("]"));
            
            for ( Element element : elements) {
                
                int x = Integer.parseInt(element.attr("HPOS"))/2-HIGHLIGHT_PADDING;
                int y = Integer.parseInt(element.attr("VPOS"))/2-HIGHLIGHT_PADDING;
                int width = Integer.parseInt(element.attr("WIDTH"))/2+HIGHLIGHT_PADDING*2;
                int height = Integer.parseInt(element.attr("HEIGHT"))/2+HIGHLIGHT_PADDING*2;
                
                
                rectangles.add(new Rectangle(x, y, width, height));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        
     
        try {
        BufferedImage br = ImageIO.read(inFile);
        BufferedImage tmp = new BufferedImage(br.getWidth(), br.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tmp.createGraphics();
        g.drawImage(br, 0, 0, null);
        g.setColor(new Color(255, 255, 0, 127));
        
        for (Rectangle rectangle : rectangles) {
            g.fill(rectangle);
        }
        
        
        g.dispose();

            ImageIO.write(tmp, "jpg", outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
