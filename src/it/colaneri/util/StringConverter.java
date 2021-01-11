package it.colaneri.util;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Classe di utility per la conversione di stringhe in altri tipi
 * I valori in input devono essere gia' trimmati
 * All' interno dei metodi vengono gestiti tutti i possibili valori in input
 * (per es. anche null)
 *
 * @todo Decidere se sollevare ConversionException (che estende CheckException)
 * @todo Deprecare DateUtils.parse e tutti gli altri metodi contenenti le logiche di
 * questi metodi
 * @todo Creare classe con variabili globali (con ad es. il valore "yyyyMMddHHmmssSSS")
 */
public class StringConverter{

    /**
     * Converte il valore passato in int
     * @param value String il valore da convertire
     * @param base la base da utilizzare per la conversione
     * @return int il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static int toPrimitiveInteger(String value, int base) throws CheckException {
        try{
            return Integer.parseInt(value, base);
        }
        catch(Exception e){
            throw new CheckException("Il valore \""+value+"\" non rappresenta un int");
        }
    }

    /**
     * Converte il valore passato in int utilizzando come base 10
     * @param value String il valore da convertire
     * @return int il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static int toPrimitiveInteger(String value) throws CheckException {
        return toPrimitiveInteger(value, 10);
    }

    /**
     * Converte il valore passato in long
     * @param value String il valore da convertire
     * @param base la base da utilizzare per la conversione
     * @return long il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static long toPrimitiveLong(String value, int base) throws CheckException {
        try{
            return Long.parseLong(value, base);
        }
        catch(Exception e){
            throw new CheckException("Il valore \""+value+"\" non rappresenta un long");
        }
    }

    /**
     * Converte il valore passato in long utilizzando come base 10
     * @param value String il valore da convertire
     * @return long il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static long toPrimitiveLong(String value) throws CheckException {
        return toPrimitiveLong(value, 10);
    }

    /**
     * Converte il valore passato in boolean
     * @param value String il valore da convertire
     * @return boolean il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static boolean toPrimitiveBoolean(String value) throws CheckException {

        String msg="il valore deve rappresentare un boolean in uno dei modi seguenti:\n - yes | no (case insensitive)\n - true | false (case insensitive)\n - si | no (case insensitive)\n - 1 | 0\n";

        if(value==null)
            throw new CheckException(msg);

        if(value.equalsIgnoreCase("yes") ||
           value.equalsIgnoreCase("true") ||
           value.equalsIgnoreCase("si") ||
           value.equals("1")){
            return true;
        }

        if(value.equalsIgnoreCase("no") ||
           value.equalsIgnoreCase("false") ||
           value.equals("0")){
            return false;
        }

        throw new CheckException(msg);
    }

    /**
     * Converte il valore passato in Integer
     * @param value String il valore da convertire
     * @param base la base da utilizzare per la conversione
     * @return Integer il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Integer toInteger(String value, int base) throws CheckException {
        try {
            return Integer.valueOf(value, base);
        }
        catch(Exception ex) {
            throw new CheckException("Il valore \""+value+"\" non rappresenta un Integer");
        }
    }

    /**
     * Converte il valore passato in Integer utilizzando come base 10
     * @param value String il valore da convertire
     * @return Integer il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Integer toInteger(String value) throws CheckException {
        return toInteger(value, 10);
    }

    /**
     * Converte il valore passato in Long
     * @param value String il valore da convertire
     * @param base la base da utilizzare per la conversione
     * @return Long il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Long toLong(String value, int base) throws CheckException {
        try {
            return Long.valueOf(value, base);
        }
        catch(Exception ex) {
            throw new CheckException("Il valore \""+value+"\" non rappresenta un Long");
        }
    }

    /**
     * Converte il valore passato in Long utilizzando come base 10
     * @param value String il valore da convertire
     * @return Long il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Long toLong(String value) throws CheckException {
        return toLong(value, 10);
    }

    /**
     * Converte il valore passato in Boolean
     * @param value String il valore da convertire
     * @return Boolean il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Boolean toBoolean(String value) throws CheckException {
        if (!(value.trim().equalsIgnoreCase("true") || value.trim().equalsIgnoreCase("false"))) throw new CheckException("La stringa value non è true o false");
        return Boolean.valueOf(value);
    }

    /**
     * Converte il valore passato in Date
     * @param value String il valore da convertire
     * @param pattern String il pattern da applicare al valore passato
     * @return Date il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Date toDate(String value, String pattern) throws CheckException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            return sdf.parse(value);
        }
        catch(Exception e) {
            throw new CheckException(
                "Data \""+value+"\" non conforme al pattern specificato: "+pattern);
        }
    }

    /**
     * Converte il valore passato in Date
     * @param value String il valore da convertire utilizzando il pattern di default
     * (yyyyMMddHHmmssSSS)
     * @return Date il valore convertito
     * @throws CheckException in caso di problemi di conversione
     */
    public static Date toDate(String value) throws CheckException {
        return toDate(value, "yyyyMMddHHmmssSSS");
    }

}
