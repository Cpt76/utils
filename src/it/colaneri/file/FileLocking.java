/*
 * Locking.java
 *
 * Created on 20 dicembre 2001, 11.43
 */

package it.colaneri.file;

import java.io.File;
import java.io.IOException;

///////////////////////////////////////////////////////////////////////////
/** Questa classe implementa il locking su file
 */
public class FileLocking{

    private File semaphore;

    private long m_delta_t;

    ///////////////////////////////////////////////////////////////////////
    /** Predispone al lock un file
     * @param semaphoreFileName Nome del file di semaforo che viene
     *  utilizzato per gestire il locking.
     * @param delta_t Durata massima del locking.
     */
    public FileLocking(String semaphoreFileName, long delta_t){
        semaphore = new File(semaphoreFileName);
        m_delta_t = delta_t;
    }

    ///////////////////////////////////////////////////////////////////////
    /** Mette il lock sul file.
     * @throws IOException Sollevata in caso di problemi di accesso
     * al file di locking.
     * @return Lo stato del lock.
     */
    public boolean lock()
        throws IOException{
        if(!semaphore.createNewFile()){
            if(System.currentTimeMillis() >
               (semaphore.lastModified() + m_delta_t)){

                //noinspection ResultOfMethodCallIgnored
                semaphore.setLastModified(System.currentTimeMillis());
                semaphore.deleteOnExit();
                return true;
            }
            else{
                return false;
            }
        }
        else{
            semaphore.deleteOnExit();
            return true;
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /** Utilizzata per togliere il lock al file.
     * @return true se é stato possibile togliere il lock;
     * altrimenti false.
     */
    public boolean unlock(){
        return semaphore.delete();
    }

}
