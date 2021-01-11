package it.colaneri.file.filters;

import java.io.FilenameFilter;
import java.io.File;

/**
 * <p>Title: MatchFilenameFilter</p>
 * <p>Description: </p>
 * E' un filtro che permette di fare
 * un list su filesystem con selezione.
 * Sono accettati tutti i file e le directory il cui nome
 * contiene una stringa specificata.
 */

public class MatchFilenameFilter implements FilenameFilter {

    private final String match;  //stringa da ricercare nel nome del file

    ///////////////////////////////////////////////////////////////////
    /** Costruttore.
     * @param match La stringa da ricercare nel nome del file.
     */
    public MatchFilenameFilter(String match) {
        this.match = match;
    }

    /**
     * @inheritDoc
     */
    public boolean accept(File file, String name) {
        return  (name.contains(match));
    }

}
