package it.colaneri.file.comparators;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Permette di ordinare o confrontare i timestamp dei file
 */
public class FileTimeStampComparator
    implements Comparator<File>{

    private final String timestampFormat;

    private final int timestampShift;

    private final SimpleDateFormat sdf;

    private boolean oldestFirst = true;

    ////////////////////////////////////////////////////////////////////////////
    /** Costruttore con logging
     *
     * @param timestampFormat il formato del timestamp, come definito dalla
     *   classe <CODE>java.text.SimpleDateFormat</CODE>
     * @param timestampShift se 0 il timestamp � in testa al nome del file;
     *   se maggiore di zero indica la dimensione del prefisso che precede il
     *   timestamp; se minore indica la posizione rispetto al postfisso, quindi
     *   -1 indica che il timestamp � subito prima dell'estensione, -3 che tra
     *   timestamp e estensione ci sono 2 caratteri di postfisso
     * @throws IllegalArgumentException se il formato passato come argomento �
     *   illegale
     */
    public FileTimeStampComparator(String timestampFormat,
                                   int timestampShift)
        throws IllegalArgumentException{
        if (timestampFormat==null) throw new IllegalArgumentException("Il parametro timestampFormat non può essere null");

        this.timestampFormat = timestampFormat;
        this.timestampShift = timestampShift;
    
        sdf = new SimpleDateFormat(timestampFormat);
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Consente di invertire il funzionamento del comparator.
     */
    public void setLIFOMode(){
        this.oldestFirst = false;
    }

    ////////////////////////////////////////////////////////////////////////////
    /** Overriding.
     * @param file1 primo file
     * @param file2 secondo file
     * @return -1 se il timestamp del primo file � precedente
     * al secondo; 0 se sono uguali;
     * 1 se il timestamp del primo file � successivo
     * al secondo.
     * @throws IllegalArgumentException se i File non esistono o non rispettano
     * il naming previsto
     */
    @Override
    public int compare(File file1, File file2){
        int result;

        Date date1, date2;
        String value1, value2;
        try{
            value1 = getTimestamp(file1);
            value2 = getTimestamp(file2);
            date1 = sdf.parse(value1);
            date2 = sdf.parse(value2);

            if(date1.before(date2)){
                result = -1;
            }
            else if(date1.after(date2)){
                result = 1;
            }
            else{
                result = 0;
            }
        }
        catch(IOException e){
            throw new IllegalArgumentException("Errore critico: " + e.getMessage());
        }
        catch(ParseException e){
            throw new IllegalArgumentException("Impossibile parsificare i file: " + e.getMessage());
        }

        if(oldestFirst){
            return result;
        }
        else{
            return -result;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /** Permette di stabilire se due File hanno lo stesso timestamp
     *
     * @param obj l'altro file
     * @return true se i due file hanno lo stesso timestamp
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof File)) return false;
        try{
            return this.timestampFormat.equals(getTimestamp((File)obj));
        }
        catch(IOException e){
            return false;
        }
    }
    @Override
    public int hashCode(){
        int hash = 5;
        hash = 37 * hash + this.timestampFormat.hashCode();
        hash = 37 * hash + this.timestampShift;
        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////
    /** Dato un file ritorna il timestamp associato
     *
     * @param file il file dal cui nome si vuole estrarre il timestamp
     * @return il timestamp richiesto
     * @throws IOException se � impossibile estrarre il timestamp
     */
    public String getTimestamp(File file)
        throws IOException{

        String timestamp;

        String name = file.getName();
        int timestampLength = timestampFormat.length();

        try{
            int index = name.lastIndexOf(".");
            if(index > 0){
                name = name.substring(0, index);
            }

            if(timestampShift >= 0){
                timestamp = name.substring(timestampShift,
                                           timestampShift + timestampLength);
            }
            else{
                timestamp = name.substring(index - timestampLength +
                                           timestampShift + 1,
                                           index + timestampShift + 1);
            }
        }
        catch(Exception e){
            throw new IOException(
                "Impossibile estrarre il timestamp dal file " +
                file.getPath());
        }

        return timestamp;
    }

}
