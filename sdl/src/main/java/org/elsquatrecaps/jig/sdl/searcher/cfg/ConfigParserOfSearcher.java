package org.elsquatrecaps.jig.sdl.searcher.cfg;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.text.WordUtils;
import org.elsquatrecaps.jig.sdl.exception.ErrorCreatingNewInstance;
import org.elsquatrecaps.jig.sdl.exception.ErrorUnmarshallingXmlConfig;
import org.elsquatrecaps.jig.sdl.searcher.AbstractGetRemoteProcess;
import org.elsquatrecaps.jig.sdl.searcher.SearchCriteria;
import org.elsquatrecaps.jig.sdl.searcher.SearchIterator;

public class ConfigParserOfSearcher {
    private static final String SEARCH_ITERATOR = "SearchIterator";
    private static final String GET_REMOTE_PROCESS = "GetRemoteProcess";
    private static final String CGF_DIR = "config";
    File file;

    private ConfigParserOfSearcher(String filename) {
        file = new File(CGF_DIR, filename.concat(".xml"));
    }
    
    private <T> T get(T root){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(root.getClass());
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            root = (T) jaxbUnmarshaller.unmarshal(file);
            return root;
        } catch (JAXBException ex) {
            throw new ErrorUnmarshallingXmlConfig(ex);
        }
    }

    private <T> void save(T root){
        try {
            file.getParentFile().mkdirs();
            JAXBContext jaxbContext = JAXBContext.newInstance(root.getClass());
            Marshaller jaxbmarshaller = jaxbContext.createMarshaller();
            jaxbmarshaller.marshal(root, file);
        } catch (JAXBException ex) {
            throw new ErrorUnmarshallingXmlConfig(ex);
        }
    }
    
    private boolean isConfigured(){
        return this.file.exists();
    }
    
    public static SearchIterator getIterator(String repository, SearchCriteria params){
        String pkg;
        String className;
        repository = WordUtils.capitalizeFully(repository);
        ConfigParserOfSearcher cfg;
        try {
            pkg = AbstractGetRemoteProcess.class.getName().substring(0, AbstractGetRemoteProcess.class.getName().lastIndexOf("."));
            className = pkg.concat(".").concat(repository).concat(GET_REMOTE_PROCESS);
            cfg = new ConfigParserOfSearcher(className);
            AbstractGetRemoteProcess rp = (AbstractGetRemoteProcess) Class.forName(className).newInstance();
            if(!cfg.isConfigured()){
                cfg.save(rp);
            }else{
                rp = cfg.get(rp);
            }
            rp.setCriteria(params);


            className = pkg.concat(".").concat(repository).concat(SEARCH_ITERATOR);
//            cfg = new ConfigParserOfSearcher(className);
            SearchIterator it =  (SearchIterator) Class.forName(className).newInstance();
//            if(!cfg.isConfigured()){
//                cfg.save(it);
//            }else{
//                it = cfg.get(it);
//            }
            it.init(rp);
            return it;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new ErrorCreatingNewInstance(ex);
        }
    }
}
