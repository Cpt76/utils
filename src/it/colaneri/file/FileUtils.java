package it.colaneri.file;

import it.colaneri.time.Timestamp;
import it.colaneri.util.Check;
import it.colaneri.util.CheckException;
import it.colaneri.util.MissingParameterException;
import it.colaneri.util.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.Properties;


public class FileUtils{

    /* E' il carattere iniziale delle chiavi rappresentanti comandi speciali (es: <@TS:yyyyMMdd>*/
    public final static char commandIdentifier = '@';

    /* Dato un filename, ritorna l' estensione comprensiva di punto. Se nel filename passato
     * non c'e' un' estensione, viene ritornata stringa vuota.
     * @param filename Il nome del file su cui operare
     * @return L' estensione ottenuta
     */
    public static String getExtension(String filename){
        int index = filename.lastIndexOf('.');
        if(index != -1){
            return filename.substring(index);
        }
        return "";
    }

    /* Dato un filename, ritorna il nome del file senza estensione. Se nel filename passato
     * non c'e' un' estensione, viene ritornato l' intero filename.
     * @param filename Il nome del file su cui operare
     * @return Il nome del file cosi' ottenuto
     */
    public static String getFilenameOnly(String filename){
        int index = filename.lastIndexOf('.');
        if(index != -1){
            return filename.substring(0, index);
        }
        return filename;
    }

    /* Dato un filename, ritorna il nome del file cambiato di estensione. Se nel filename passato
     * non c'e' un' estensione, viene accodata quella passata.
     * @param filename Il nome del file su cui operare
     * @param extension L' estensione da utilizzare nella sostituzione comprensiva di punto
     * @return Il nome del file cosi' ottenuto
     */
    public static String changeExtension(String filename, String extension){
        return getFilenameOnly(filename) + extension;
    }

    /* Dato un filename, ritorna il nome del file con estensione "tmp". Se nel filename passato
     * non c'e' un' estensione, viene accodata quella passata.
     * @param filename Il nome del file su cui operare
     * @return Il nome del file cosi' ottenuto
     */
    public static String changeExtension(String filename){
        return changeExtension(filename, ".tmp");
    }

    /* Ritorna il filename ottenuto sostituendo nel fileformat i tags ivi contenuti
     * con le Properties passate. I tags devono essere maiuscoli (es: <VARIABILE1>).
     * Nel caso venga passato nel fileformat un comando TS, per generare l' opportuno
     * timestamp verra' utilizzata la data corrente. Se nel properties manca il valore
     * di un tag viene sollevata una MissingParameterException.
     * @param fileformat Il fileformat su cui eseguire le sostituzioni
     * @param params I parametri da sostituire
     * @return Il nome del file cosi' ottenuto
     */
    public static String filenameFormat(String fileformat,
                                        Properties params)
            throws CheckException {
        return filenameFormat(fileformat, params, new Date());
    }

    /* Ritorna il filename ottenuto sostituendo nel fileformat i tags ivi contenuti
     * con le Properties passate. I tags devono essere maiuscoli (es: <VARIABILE1>).
     * Nel caso venga passato nel fileformat un comando TS, per generare l' opportuno
     * timestamp verra' utilizzata la data corrente. Se nel properties manca il valore
     * di un tag viene utilizzato un valore di default (parametro defaultValue).
     * @param fileformat Il fileformat su cui eseguire le sostituzioni
     * @param params I parametri da sostituire
     * @param defaultValue il valore di default da utilizzare nel caso in cui manchi
     * nel properties il valore di un tag.
     * @return Il nome del file cosi' ottenuto
     */
    public static String filenameFormat(String fileformat,
                                        Properties params,
                                        String defaultValue)
            throws CheckException{
        return filenameFormat(fileformat, params, new Date(), defaultValue);
    }

    /* Ritorna il filename ottenuto sostituendo nel fileformat i tags ivi contenuti
     * con le Properties passate. I tags devono essere maiuscoli (es: <VARIABILE1>)
     * @param fileformat Il fileformat su cui eseguire le sostituzioni. Se nel
     * properties manca il valore di un tag viene sollevata una MissingParameterException.
     * @param params I parametri da sostituire
     * @param when La data da considerare per il comando TS (timestamp)
     * @return Il nome del file cosi' ottenuto
     */
    public static String filenameFormat(String fileformat,
                                        Properties params, Date when)
            throws CheckException{
        return filenameFormat(fileformat, params, when, null);
    }

    /* Ritorna il filename ottenuto sostituendo nel fileformat i tags ivi contenuti
     * con le Properties passate. I tags devono essere maiuscoli (es: <VARIABILE1>)
     * @param fileformat Il fileformat su cui eseguire le sostituzioni. Se nel
     * properties manca il valore di un tag viene utilizzato un valore di
     * default (parametro defaultValue).
     * @param params I parametri da sostituire
     * @param when La data da considerare per il comando TS (timestamp)
     * @param defaultValue il valore di default da utilizzare nel caso in cui manchi
     * nel properties il valore di un tag.
     * @return Il nome del file cosi' ottenuto
     */
    public static String filenameFormat(String fileformat,
                                        Properties params, Date when,
                                        String defaultValue)
            throws CheckException{
        return getFilenameFormat(fileformat, params, when, defaultValue);
    }

    private static String getFilenameFormat(String fileformat,
                                            Properties params, Date when,
                                            String defaultValue)
            throws CheckException{
        checkFileformat(fileformat);
        int smallerOfIndex = 0;
        int greaterOfIndex;
        String key, value;
        String temp = fileformat;
        while((smallerOfIndex = temp.indexOf('<', smallerOfIndex)) != -1){
            greaterOfIndex = temp.indexOf('>', smallerOfIndex);
            key = temp.substring(smallerOfIndex + 1, greaterOfIndex);
            value = getValue(key, params, when, defaultValue);
            temp = StringUtils.replace(temp, smallerOfIndex,
                                       greaterOfIndex + 1, value);
            smallerOfIndex += value.length();
        }
        return temp;
    }

    private static void checkFileformat(String fileformat)
            throws CheckException{
        char[] charArray = fileformat.toCharArray();
        char nextSeparator = '<';
        boolean closed = true;
        for (char c : charArray) {
            if (c == '<') {
                if (nextSeparator == '>') {
                    throw new CheckException();
                }
                nextSeparator = '>';
                closed = false;
            }
            if (c == '>') {
                if (nextSeparator == '<') {
                    throw new CheckException();
                }
                nextSeparator = '<';
                closed = true;
            }
        }
        if(!closed){
            throw new CheckException();
        }
    }

    private static String getValue(String key, Properties params, Date when,
                                   String defaultValue)
            throws CheckException{
        String value;

        if(key.charAt(0) == commandIdentifier){
            return getCommand(key, when);
        }

        if(params == null){
            throw new CheckException("Parametro params null");
        }

        value = params.getProperty(key);

        if(value == null){
            if(defaultValue == null){
                throw new MissingParameterException(key);
            }
            else{
                value = defaultValue;
            }
        }

        return value;
    }

    private static String getCommand(String key, Date when)
            throws CheckException{
        int index;
        if((index = key.indexOf(':')) == -1){
            throw new CheckException(
                    "Errore di sintassi: carattere ':' mancante");
        }
        String command = key.substring(1, index);
        if(command.equals("TS")){
            if(when == null){
                throw new CheckException("Data passata non corretta");
            }
            String pattern = Check.simpleDateFormat(key, key.substring(index + 1));
            return new Timestamp(pattern).toString();
        }
        else{
            throw new CheckException("Comando non supportato: " + command);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    //                          supporto utf-8                                //
    ////////////////////////////////////////////////////////////////////////////
    public static String readLine(FileInputStream in, String charset)
            throws IOException{
        return readLine((InputStream)in, charset);
    }

    /**
     * Restituisce una riga di un file convertita in una stringa UTF8
     *
     * @param in input stream
     * @param charset charset
     * @return String la linea letta
     * @throws IOException in caso di errore
     */
    public static String readLine(InputStream in, String charset)
            throws IOException{
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        int b;
        do{
            b = in.read();
            if(b != -1 && b != '\n' && b != '\r' && b != '\t'){
                bo.write(b);
            }
        }
        while(b != -1 && b != '\n');//fine riga
        return bo.toString(charset).trim();
    }

    /**
     * Controlla se IL fILE passato ha come primi 3 bytes
     * il BOM utf-8
     *
     * @param file il file
     * @return true se inizia col BOM
     * @throws IOException errore
     */
    public static boolean checkBOM(File file)
            throws IOException{
        try (FileInputStream in = new FileInputStream(file)) {
            return FileUtils.checkBOM(in);
        }
    }

    /**
     * Controlla se l'InputStream passato ha come primi 3 bytes
     * il BOM utf-8
     *
     * -------
     * - N.B.-
     * -------
     * ATTENZIONE!!! SIA IN CASO DI ESITO POSITIVO CHE IN CASO DI ESITO NEGATIVO
     * I PRIMI 3 BYTES SONO STATI COMUNQUE CONSUMATI !!
     * 
     * @param in input stream
     * @return true se inizia col BOM
     * @throws IOException in caso di errore
     */
    public static boolean checkBOM(InputStream in)
            throws IOException{
        byte[] b = new byte[3];
        if(in != null && in.available() > 0){
            //noinspection ResultOfMethodCallIgnored
            in.read(b);//leggo i primi 3 bytes
        }
        return checkBOM(b);
    }

    /**
     * Controlla se l'InputStreamReader passato ha come primi 3 bytes
     * il BOM utf-8
     *
     * -------
     * - N.B.-
     * -------
     * ATTENZIONE!!! SIA IN CASO DI ESITO POSITIVO CHE IN CASO DI ESITO NEGATIVO
     * I PRIMI 3 BYTES SONO STATI COMUNQUE CONSUMATI !!
     *
     * @param reader un reader
     * @return true se inizia col BOM
     * @throws IOException in caso di errore
     */
    public static boolean checkBOM(InputStreamReader reader)
            throws IOException{
        int c = -1;
        if(reader != null && reader.ready()){
            c = reader.read();
        }
        return c == 65279;//presenza di BOM
    }

    /**
     * Controlla se l'array passato ha come primi 3 bytes
     * il BOM utf-8
     * 
     * @param buf un buffer di byte
     * @return true se inizia col BOM
     */
    public static boolean checkBOM(byte[] buf){
        //(byte) 0xEF
        //(byte) 0xBB
        //(byte) 0xBF
        return (buf[0] == (byte) 0xEF)
                && (buf[1] == (byte) 0xBB)
                && (buf[2] == (byte) 0xBF);
    }

    ////////////////////////////////////////////////////////////////////////////
    //                          Writer e Reader                               //
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Charset di default
     *
     * @param file il file per cui si vuole ottenere il writer
     * @param append se si vuole appendere
     * @return un OutputStreamWriter con charset di default
     * @throws IOException in caso di errore
     */
    public static OutputStreamWriter getFileWriter(File file, boolean append)
            throws IOException{
        return FileUtils.getFileWriter(file, null, append);
    }

    /**
     * Direttamente per file utf-8
     *
     * @param file il file per cui si vuole ottenere il writer
     * @param append se si vuole appendere
     * @return un OutputStreamWriter con charset UTF-8
     * @throws IOException in caso di errore
     */
    public static OutputStreamWriter getUTF8FileWriter(File file, boolean append)
            throws IOException{
        return FileUtils.getFileWriter(file, "UTF-8", append);
    }

    /**
     * Istanzia il writer corretto in base al charset, se UTF-8 SCRIVE IL BOM AUTOMATICAMENTE
     *
     * @param file il file per cui si vuole ottenere il writer
     * @param charset il charset
     * @param append se si vuole appendere
     * @return un OutputStreamWriter con charset specificato
     * @throws IOException in caso di errore
     */
    public static OutputStreamWriter getFileWriter(File file, String charset,
                                                   boolean append)
            throws IOException{
        OutputStreamWriter writer;
        FileOutputStream f_out = new FileOutputStream(file, append); //apro lo stream di scrittura in append
        // scelta del charset
        if(charset != null){
            writer = new OutputStreamWriter(f_out, charset);
            // scrivo il BOM utf-8  (EF BB BF)
            // (byte)0xEF ,(byte)0xBB ,(byte)0xBF}
            if(!append && charset.equalsIgnoreCase("UTF-8"))//file non in append ed in utf-8
            {
                //scrivo l'header
                f_out.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            }
        }
        else //charset di default
        {
            writer = new OutputStreamWriter(f_out);
        }// flush parametri
        return writer;
    }

    /**
     * Charset di default
     *
     * @param file il file
     * @return un InputStreamReader
     * @throws IOException in caso di errore
     */
    public static InputStreamReader getFileReader(File file)
            throws IOException{
        return FileUtils.getFileReader(file, null);
    }

    /**
     * Direttamente per file utf-8
     *
     * @param file il file
     * @return un InputStreamReader UTF-8
     * @throws IOException in caso di errore
     */
    public static InputStreamReader getUTF8FileReader(File file)
            throws IOException{
        return FileUtils.getFileReader(file, "UTF-8");
    }

    /**
     * Istanzia un reader per lo stream di caratteri da leggere in base al charset
     *
     * --------
     * - N.B. -
     * --------
     * Nel caso in cui il file sia stato formattato in utf-8 restituisce un Reader
     * che ha gia' skippato il primi 3 byte (BOM)
     *
     * @param file il file
     * @param charset il charset
     * @return un InputStreamReader
     * @throws IOException in caso di errore
     */
    public static InputStreamReader getFileReader(File file, String charset)
            throws IOException{
        if(charset != null){
            InputStreamReader i = new InputStreamReader(new FileInputStream(file), charset);
            if(charset.equalsIgnoreCase("UTF-8") && FileUtils.checkBOM(file)){
                //noinspection ResultOfMethodCallIgnored
                i.skip(1); //serve per skippare il BOM
            }
            return i;
        }
        else{
            return new InputStreamReader(new FileInputStream(file));
        }
    }

}
