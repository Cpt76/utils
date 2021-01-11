/*
 * PatternFileFilter.java
 *
 * Created on 08 January 2002, 11:50
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Questo {@link FileFilter} permette di filtrare
 * i file aventi nome rispettante un pattern dato.
 */
public class PatternFileFilter
    implements FileFilter{
    private Pattern pattern;

    /** Costruttore.
     * @param pattern il pattern da filtrare.
     */
    public PatternFileFilter(Pattern pattern){
        this.pattern = pattern;
    }

    /** Overriding del metodo corrispondente.
     * @param file Il file da accettare o rifiutare.
     * @return File accettato o rifiutato.
     */
    public boolean accept(File file){
        if(file.isFile()){
            Matcher matcher = pattern.matcher(file.getName());
            boolean found = false;
            while(matcher.find()){
                found = true;
            }
            return found;
        }
        else{
            return false;
        }
    }

}
