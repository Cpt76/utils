/*
 * SubPathsFileFilter.java
 *
 * Created on 14 ottobre 2002, 11.07
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;

/** E' un filtro che permette di fare
 * un list su filesystem con selezione di subdirectory specificate.
 * Sono accettate tutte le directory il cui path termina con
 * quelli presenti in una lista data (per esempio é possibile
 * ricercare al fondo un path del tipo "nuove/nokia").
 */
public class SubPathsFileFilter
    implements FileFilter{

    private String[] subPaths;

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param subPaths La stringa da ricercare al fondo del path del file.
     */
    public SubPathsFileFilter(String[] subPaths){
        this.subPaths = subPaths;
    }

    ///////////////////////////////////////////////////////////////////
    /** Metodo di accettazione dei file
     * @param file Il file o la directory da accettare o filtrare.
     * @return <CODE>true</CODE> se il file é accettato;
     * <CODE>false</CODE> se é rifiutato.
     */
    public boolean accept(File file){
        String filename = file.getPath();
        if(File.separator.equals("\\")){
            filename = filename.replace('\\', '/');
        }
        if(file.isDirectory()){
            for(int i = 0; i < subPaths.length; i++){
                if(subPaths[i].endsWith("/")){
                    subPaths[i] = subPaths[i].substring(0,
                                                        subPaths[i].length() -
                                                        1);
                }
                if(filename.endsWith(subPaths[i])){
                    return true;
                }
            }
        }
        return false;
    }
}
