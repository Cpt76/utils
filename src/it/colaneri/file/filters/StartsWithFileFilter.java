/*
 * StartsWithFileFilter.java
 *
 * Created on 11 September 2002, 17:50
 */

package it.colaneri.file.filters;

import java.io.File;
import java.io.FileFilter;

///////////////////////////////////////////////////////////////////////////
/** Questo {@link FileFilter} permette di filtrare
 * file e directory il cui nome ha un certo prefisso.
 */
public class StartsWithFileFilter
    implements FileFilter{
    String prefix;

    ///////////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param prefix Il prefisso da filtrare.
     */
    public StartsWithFileFilter(String prefix){
        this.prefix = prefix;
    }

    ///////////////////////////////////////////////////////////////////////
    /** Overriding del metodo corrispondente.
     * @param file Il file da accettare o rifiutare.
     * @return File accettato o rifiutato.
     */
    public boolean accept(File file){
        String name = file.getName();
        return name.startsWith(prefix);
    }

}
