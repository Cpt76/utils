/*
 * ExtensionFileFilter.java
 *
 * Created on 08 January 2002, 11:50
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Questo {@link FileFilter} permette di filtrare
 * i file in base alla loro estensione.
 */
public class ExtensionFileFilter
    implements FileFilter{
    /**
     * Il separatore usato nell'elenco delle estensioni valide
     */
    public static final String SEPARATOR = ";, \t";

    /** Stringa per indicare "tutte le estensioni"
     */
    public static final String ALL = "*";

    /** String per indicare "file senza estensione"
     */
    public static final String NOEXTENSION = "?none?";

    /**
     * Lista di estensioni ammesse, separate da ;
     */
    private final List<String> extensions = new ArrayList<>();

    /** Costruttore.
     * @param extension L'elenco di estensioni (senza punto) valide per il filtro
     */
    public ExtensionFileFilter(String extension){
        StringTokenizer st = new StringTokenizer(extension.trim(), SEPARATOR);
        while(st.hasMoreTokens()){
            String ext = st.nextToken();
            this.extensions.add(ext);
        }
    }

    /** Overriding del metodo corrispondente.
     * @param file Il file da accettare o rifiutare.
     * @return File accettato o rifiutato.
     */
    @Override
    public boolean accept(File file){
        if(file.isFile()){
            String name = file.getName();

            //Gestione "tutti"
            if(extensions.contains(ALL)){
                return true;
            }

            //Gestione "nessuna estensione"
            if(extensions.contains(NOEXTENSION)){
                if(!name.contains(".")){
                    return true;
                }
            }

            //Altri casi
            String filename = file.getName();
            String fileExtension = filename.substring(filename.lastIndexOf('.') + 1);
            return extensions.contains(fileExtension);
        }
        else{
            return false;
        }
    }
}
