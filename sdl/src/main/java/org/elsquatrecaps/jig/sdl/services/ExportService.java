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

@Service
public class ExportService {

    ResourceRepository resourceRepository;
    DownloaderProperties dp;

    public ExportService(ResourceRepository resourceRepository, DownloaderProperties dp) {
        this.resourceRepository = resourceRepository;
        this.dp = dp;
    }

    public void exportResourcesById(String[] ids, String format) {
        for (String id : ids) {
            exportResourceById(id, format);
        }
    }

    
    public void exportResourceById(String id, String format) {
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
        File file = new File(fileExportPath, ret.getFileName().concat(".").concat(format));
        ret.setLocalFilePath(dp.getLocalExportPath());
        FormatedFile ff = ret.getFormatedFile(format);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
            }
            Utils.copyToFile(ff.getImInputStream(), fileOutputStream);
            System.out.println("Fitxer copiat");
        } else {
            System.out.println("El fitxer ja existeix, no el copiem");
        }
    }
}
