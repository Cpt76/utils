/*
 * MatchDirFileFilter.java
 *
 * Created on 14 ottobre 2002, 11.07
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;

/** E' un filtro che permette di fare
 * un list su filesystem con selezione di subdirectory specificate.
 * Sono accettate tutte le directory il cui nome
 * è presente in una lista data.
 */
public class MatchDirFileFilter
    implements FileFilter{

    private String[] dirsName;

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param dirsName La stringa da ricercare nel nome del file.
     */
    public MatchDirFileFilter(String[] dirsName){
        this.dirsName = dirsName;
    }

    ///////////////////////////////////////////////////////////////////
    /** Metodo di accettazione dei file
     * @param file Il file o la directory da accettare o filtrare.
     * @return <CODE>true</CODE> se il file é accettato;
     * <CODE>false</CODE> se é rifiutato.
     */
    public boolean accept(File file){
        String filename = file.getName();

        if(file.isDirectory()){
            for(int i = 0; i < dirsName.length; i++){
                if(dirsName[i].endsWith("/")){
                    dirsName[i] = dirsName[i].substring(0,
                                                        dirsName[i].length() -
                                                        1);
                }
                if(filename.equals(dirsName[i])){
                    return true;
                }
            }
        }
        return false;
    }
}
