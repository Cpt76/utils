/*
 * ExtensionFilenameFilter.java
 *
 * Created on 2 settembre 2002, 11.10
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FilenameFilter;

/** Utile a filtrare il contenuto di una directory
 * in base all'estensione.
 */
public class ExtensionFilenameFilter
    implements FilenameFilter{

    private String pattern;

    /** Costruisce un nuovo filtro.
     * @param extension L'estensione da filtrare
     */
    public ExtensionFilenameFilter(String extension){
        this.pattern = extension;
    }

    /** Filtra in base all'estensione
     * @param dir La directory da filtrare
     * @param name Il nome del file da filtrare
     * @return <CODE>true</CODE> se il file termina con l'estensione
     * corretta, <CODE>false</CODE> altrimenti
     */
    public boolean accept(File dir, String name){
        return(name.endsWith("." + pattern));
    }
}
