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
public class MatchTimestampFileFilter
    implements FileFilter{

    private String match; //stringa da ricercare nel nome del file

    private int shift;

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param match La stringa da ricercare nel nome del file.
     * @param shift La posizione relativa del timestamp all'interno del
     * filename. Se maggiore o uguale a 0 indica lo shift da sinistra,
     * mentre se minore di 0 indica lo shift da destra rispetto
     * all'estensione se presente o alla fine del nome del file.
     */
    public MatchTimestampFileFilter(String match, int shift){
        this.match = match;
        this.shift = shift;
    }

    ///////////////////////////////////////////////////////////////////
    /** Metodo di accettazione dei file
     * @param file Il file o la directory da accettare o filtrare.
     * @return <CODE>true</CODE> se il file é accettato;
     * <CODE>false</CODE> se é rifiutato.
     */
    public boolean accept(File file){
        //nome del file
        String filename = file.getName();
        //nome del file senza estensione e punto
        int extIndex = filename.lastIndexOf(".");
        String elWithoutExt;
        if(extIndex < 0){
            elWithoutExt = filename;
        }
        else{
            elWithoutExt = filename.substring(0, extIndex);
        }

        //indice inizio stringa di timestamp
        int firstIndex;
        //indice fine stringa di timestamp
        int lastIndex;

        if(shift < 0){
            firstIndex = elWithoutExt.length() +
                         shift + 1 -
                         match.length();
            lastIndex = elWithoutExt.length() +
                        shift + 1;
        }
        else{
            firstIndex = shift;
            lastIndex = shift + match.length();
        }
        //timestamp
        String TS = null;
        try{
            TS = elWithoutExt.substring(firstIndex, lastIndex);
        }
        catch(Exception ex){
            //Timestamp non trovato!!!!!!!!
        }

        return(match.equals(TS));
    }
}
