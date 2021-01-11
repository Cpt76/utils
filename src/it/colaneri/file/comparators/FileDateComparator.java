/*
 * FileDateComparator.java
 *
 * Created on 08 January 2002, 14:53
 */

package it.colaneri.file.comparators;

import java.io.File;
import java.util.Comparator;

/** <CODE>Comparator</CODE> in base alla data
 * di ultimo accesso: ordina dalla data piú recente
 * alla piú remota.
 */
public class FileDateComparator implements Comparator<File> {

    /** Overriding.
     * @param file1 Il primo File.
     * @param file2 Il secondo File.
     * @return -1 se il primo file é stato modificato DOPO
     * il secondo; 0 se sono stati modificati allo
     * stesso momento; 1 se il primo file é stato
     * modificato PRIMA del secondo.
     */    
    @Override
    public int compare(File file1, File file2) {

        int value=0;
        if (file1.lastModified() > file2.lastModified()) value= -1;
        if (file1.lastModified() < file2.lastModified()) value= 1;
        if (file1.lastModified() == file2.lastModified()) value= 0;
        return value;
    }
}
