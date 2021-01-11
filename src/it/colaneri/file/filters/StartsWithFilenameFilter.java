/*
 * StartsWithFileFilter.java
 *
 * Created on 11 September 2002, 17:50
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FilenameFilter;

///////////////////////////////////////////////////////////////////////////
/** Questo {@link FilenameFilter} permette di filtrare
 * file e directory il cui nome inizia con un certo prefisso.
 */
public class StartsWithFilenameFilter
    implements FilenameFilter{
    String prefix;

    ///////////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param prefix Il prefisso da filtrare.
     */
    public StartsWithFilenameFilter(String prefix){
        this.prefix = prefix;
    }

    ///////////////////////////////////////////////////////////////////////
    /** Overriding del metodo corrispondente.
     * @param file Il file da accettare o rifiutare.
     * @param name Nome file da accettare o rifiutare.
     * @return File accettato o rifiutato.
     */
    public boolean accept(File file, String name){
        return name.startsWith(prefix);
    }

}
