package org.elsquatrecaps.jig.sdl.searcher;

import org.elsquatrecaps.jig.sdl.model.FormatedFile;
import java.io.InputStream;

public abstract class AbstractFormatedFile implements FormatedFile{
    

    @Override
    public String getFormat() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}