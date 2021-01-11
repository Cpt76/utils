/*
 * MissingParameterException.java
 *
 * Created on 29 maggio 2002, 14.51
 */

package it.colaneri.util;

import java.io.Serial;

/** Questa eccezione viene scatenata per segnalare che uno
 * o piú parametri passati a un metodo sono null.
 *
 * @author Colaneri
 * @version 1.0
 */
public class MissingParameterException extends CheckException {

    /**
	 * 
	 */
    @Serial
	private static final long serialVersionUID = 1L;


	/** Usato se non si desidera specificare un messaggio
     * di errore.
     */
    public MissingParameterException() {
        super();
    }


    /** Usando questo costruttore viene automaticamente
     * generato il messaggio di dettaglio standard:
     * "Il parametro <I>nome parametro</I>
     * non é stato inizializzato"
     * @param param Il nome del parametro <CODE>null</CODE>
     */
    public MissingParameterException(String param){
        super("Il parametro \""+param+"\" non é stato inizializzato");
    }
}


