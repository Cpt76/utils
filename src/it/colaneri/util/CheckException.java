/*
 * CheckException.java
 *
 * Created on 29 maggio 2002, 11.19
 */

package it.colaneri.util;

import java.io.Serial;

/** Indica un errore sintattico/semantico rilevato
 * dalla classe
 * {@link Check}
 *
 * @author Colaneri
 * @version 1.0
 */
public class CheckException extends Exception {

    /**
	 * 
	 */
    @Serial
	private static final long serialVersionUID = 1L;

	/** Crea una nuova <CODE>CheckException</CODE>.
     */
    public CheckException() {
        super();
    }


    /** Crea una nuova <CODE>CheckException</CODE>
     * col messaggio di dettaglio specificato.
     * @param msg Il messaggio di dettaglio.
     */
    public CheckException(String msg) {
        super(msg);
    }

    /** Crea una nuova <CODE>CheckException</CODE>
     * con la causa e il messaggio di dettaglio specificati.
     * @param msg Il messaggio di dettaglio.
     * @param cause La causa
     */
    public CheckException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /** Crea una nuova <CODE>CheckException</CODE>
     * con la causa specificata.
     * @param cause La causa
     */
    public CheckException(Throwable cause) {
        super(cause);
    }

}


