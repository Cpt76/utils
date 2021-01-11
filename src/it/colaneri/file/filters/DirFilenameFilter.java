/*
 * MatchDirFileFilter.java
 *
 * Created on 14 ottobre 2002, 11.07
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FilenameFilter;

/** E' un filtro che permette di fare
 * un list su filesystem con selezione dei soli file.
 * Tutte le directory vengono scartate
 */
public class DirFilenameFilter
    implements FilenameFilter{

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     */
    public DirFilenameFilter(){
    }

    /**
     * @inheritDoc
     */
    public boolean accept(File dir, String name){
        File localFile = new File(dir.getPath() + "/" + name);
        return !localFile.isDirectory();
    }
}
