/*
 * MatchPropertiesFileFilter.java
 *
 * Created on 14 ottobre 2002, 11.07
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;
import java.util.Enumeration;
import java.util.Properties;

/** E' un filtro che permette di fare
 * un list su filesystem con selezione.
 * Sono accettati tutti i file e le directory il cui nome
 * è presente come valore nel properties specificato
 */
public class MatchPropertiesFileFilter
    implements FileFilter{

    private final Properties renaming; //stringa da ricercare nel nome del file

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param renaming una properties in cui cercare NEI VALORI i nomi dei file da filtrare
     */
    public MatchPropertiesFileFilter(Properties renaming){
        this.renaming = renaming;
    }

    ///////////////////////////////////////////////////////////////////
    /** Metodo di accettazione dei file
     * @param file Il file o la directory da accettare o filtrare.
     * @return <CODE>true</CODE> se il file é accettato;
     * <CODE>false</CODE> se é rifiutato.
     */
    public boolean accept(File file){
        boolean check = false;
        Enumeration<Object> e = renaming.elements();
        while(e.hasMoreElements()){
            String value = (String)e.nextElement();
            if(file.getName().startsWith((value))){
                check = true;
                break;
            }
        }
        return check;
    }
}
