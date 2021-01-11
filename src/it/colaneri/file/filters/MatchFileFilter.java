/*
 * FileMatchFilter.java
 *
 * Created on 9 settembre 2002, 11.07
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;

/** E' un filtro che permette di fare
 * un list su filesystem con selezione.
 * Sono accettati tutti i file e le directory il cui nome
 * contiene una stringa specificata.
 */
public class MatchFileFilter
    implements FileFilter{

    private String match; //stringa da ricercare nel nome del file

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param match La stringa da ricercare nel nome del file.
     */
    public MatchFileFilter(String match){
        this.match = match;
    }

    ///////////////////////////////////////////////////////////////////
    /** Metodo di accettazione dei file
     * @param file Il file o la directory da accettare o filtrare.
     * @return <CODE>true</CODE> se il file é accettato;
     * <CODE>false</CODE> se é rifiutato.
     */
    public boolean accept(File file){
        return(file.getName().contains(match));
    }
}
