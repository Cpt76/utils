/*
 * Check.java
 *
 * Created on 28 maggio 2002, 17.01
 */
package it.colaneri.util;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/** Questa classe ha lo scopo di raccogliere una collezione
 * di metodi di verifica dei parametri passati a una
 * applicazione java sotto forma di stringa, tramite
 * file di properties o come argomenti della linea
 * di comando. Nel messaggio di dettaglio delle eccezioni
 * sollevate é specificato il nome del parametro
 * mancante ed é fornita una succinta descrizione del
 * corretto formato atteso.
 *
 *
 * @author Colaneri
 * @version 1.0
 */
public class Check{
    
    //Configurazione attualmente sotto esame
    private Properties cfg;
    
    public Check(Properties cfg){
        if (cfg==null) throw new NullPointerException();
        this.cfg=cfg;
    }

    /**
     * Ritorna la parte iniziale dei messaggi di errore in caso di check
     * non superato
     * @param name String Il nome del parametro o null se il parametro passato
     * non ha un nome (cioé se non proviene da una configurazione)
     * @return String la parte iniziale dei messaggi di errore in caso di check
     * non superato
     */
    public static String getHeader(String name){
        if(name == null){
            name = "";
        }
        return "Il parametro \"" + name + "\" non é corretto: ";
    }
    
    protected static String getIllegalStateMessage(String name){
        if (name == null){
            name = "";
        }
        return "Metodo di check non statico invocato sul parametro \""+ name +
                "\" senza aver prima impostato la configurazione col metodo setCheckedConfiguration";
    }

    /** Questo metodo controlla la correttezza sintattica E
     * SEMANTICA di un path di directory. Si noti che in sostanza
     * questo metodo controlla anche che il path corrisponda
     * realmente a una directory nel sistema locale (su cui
     * gira la JVM). Se si vuole controllare un path su un altro
     * sistema si utilizzi il metodo <CODE>remotePath()</CODE>
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     * @deprecated Utilizzare il metodo <CODE>directoryPath()</CODE>
     */
    public static String dirPath(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();

        File prova = new File(param);
        if(!prova.isDirectory()){
            throw new CheckException(getHeader(name)
                                     + "un path deve essere nella forma /A/B/../K e K deve essere "
                                     + "una directory");
        }
        return param;
    }

    /** Questo metodo controlla la correttezza sintattica E
     * SEMANTICA di un path di directory. Si noti che in sostanza
     * questo metodo controlla anche che il path corrisponda
     * realmente a una directory nel sistema locale (su cui
     * gira la JVM). Se si vuole controllare un path su un altro
     * sistema si utilizzi il metodo <CODE>remotePath()</CODE>
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato" e con uno slash al fondo.
     */
    public static String directoryPath(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        if(!param.endsWith("/")){
            param += "/";
        }
        param = oneOnlySlashSeparatedPath(param);
        File prova = new File(param);
        if(!prova.isDirectory()){
            throw new CheckException(getHeader(name)
                                     + "il path passato non e' realmente esistente");
        }
        return param;
    }

    /**
     * Vedi directoryPath
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato" e con uno slash al fondo.
     */
    public String directoryPath(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return directoryPath(name, cfg.getProperty(name));
    }
    
    private static String oneOnlySlashSeparatedPath(String path){
        int maxLength = path.length();
        StringBuilder temp = new StringBuilder(maxLength);
        temp.append(path.charAt(0));
        char previous, current;
        for(int i = 1; i < maxLength; i++){
            previous = path.charAt(i - 1);
            current = path.charAt(i);
            if(current != previous || current != '/'){
                temp.append(current);
            }
        }
        return new String(temp);
    }

    /** Questo metodo controlla la correttezza sintattica E
     * SEMANTICA di un path di contesto.
     * Un path di contesto rappresenta un path di directory
     * utilizzato come punto di partenza per path relativi.
     * Un path di contesto inizia e finisce sempre col carattere
     * '/' mentre un path relativo inizia sempre <B>senza</B> slash
     * e puó essere controllato col metodo {@link #relativePath}.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public static String contextPath(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();

        if(!param.endsWith("/") || !param.startsWith("/")){
            throw new CheckException(getHeader(name)
                                     + "un context path deve essere nella forma /A/B/../K/ "
                                     + "e K deve essere una directory");
        }

        File prova = new File(param);
        if(!prova.isDirectory()){
            throw new CheckException(getHeader(name)
                                     + "un context path deve essere nella forma /A/B/../K/ "
                                     + "e K deve essere una directory");
        }

        return param;
    }

    /**
     * Vedi contextPath
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public String contextPath(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return contextPath(name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla la correttezza sintattica di un path
     * relativo, riferito a un context path, verificabile col
     * metodo {@link #contextPath}.
     * Un path di contesto rappresenta un path di directory
     * utilizzato come punto di partenza per path relativi.
     * Un path di contesto inizia e finisce sempre col carattere
     * '/' mentre un path relativo inizia sempre {@code <B>senza<B>} slash.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é {@code <CODE>null</CODE>}.
     * @return Il path "trimmato".
     */
    public static String relativePath(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        if(param.startsWith("/")){
            throw new CheckException(getHeader(name)
                                     + "un relative path deve essere nella forma A/B/../K");
        }
        return param;
    }

    /**
     * Vedi relativePath
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public String relativePath(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return relativePath(name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla la correttezza sintattica E
     * SEMANTICA di un path di file. Si noti che in sostanza
     * questo metodo controlla anche che il path corrisponda
     * realmente a un file nel sistema locale (su cui
     * gira la JVM). Se si vuole controllare un path su un altro
     * sistema si utilizzi il metodo <CODE>remotePath()</CODE>
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public static String filePath(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        File prova = new File(param);
        if(!prova.isFile()){
            throw new CheckException(getHeader(name)
                                     + "un filepath deve essere nella forma /A/B/../K e K deve "
                                     + "essere un file (NON una directory)");
        }
        return param;
    }

    /**
     * Vedi filePath
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public String filePath(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return filePath(name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla la correttezza sintattica
     * (ma NON semantica) di un path, di file o directory.
     * Si usi questo metodo quando il path é su un sistema
     * remoto oppure se non si vuole verificare la
     * correttezza semantica del path fornito.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public static String remotePath(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        if(!param.startsWith("/") || param.endsWith("/")){
            throw new CheckException(getHeader(name)
                                     + "un path remoto deve essere nella forma /A/B/../K");
        }
        return param;
    }

    /**
     * Vedi remotePath
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il path "trimmato".
     */
    public String remotePath(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return remotePath(name, cfg.getProperty(name));
    }
    
    /**
     * 
     * @param param un livello di severity
     * @return Una severity compresa tra 0 e 5
     * @throws CheckException se il parametro non è valido
     * @throws MissingParameterException se il parametro è null
     */
    public static int severity(String param)
            throws CheckException,
                   MissingParameterException{
        return Check.severity(null, param);
    }

    /** Questo metodo verifica la correttezza di un
     * parametro <I>severity</I>, come definito nell'
     * interfaccia com.citecvoice.sftelco.kernel.logging.Logger
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Una severity compresa tra 0 e 5
     * Questo metodo è reso obsoleto dall'utilizzo di slf4j
     */
    public static int severity(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        String msg = "";
        int severity;
        try{
            severity = Integer.parseInt(param);
            if(severity < 0 || severity > 6){// 0 e 6
                if(name != null){
                    msg = msg + getHeader(name);
                }
                msg += "la severity deve essere un numero compreso tra "
                       + 0 + " e " + 6;
                throw new CheckException(msg);
            }
        }
        catch(NumberFormatException e){
            if(param.equalsIgnoreCase("trace")){
                severity = 0;
            }
            else if(param.equalsIgnoreCase("debug")){
                severity = 1;
            }
            else if(param.equalsIgnoreCase("info")){
                severity = 2;
            }
            else if(param.equalsIgnoreCase("warning")){
                severity = 3;
            }
            else if(param.equalsIgnoreCase("error")){
                severity = 4;
            }
            else if(param.equalsIgnoreCase("fatal")){
                severity = 5;
            }
            else if(param.equalsIgnoreCase("none")){
                severity = 6;
            }
            else{
                if(name != null){
                    msg = msg + getHeader(name);
                }
                msg += "la severity deve essere un valore compreso tra (TRACE,DEBUG,INFO,WARNING,ERROR,FATAL,NONE)";
                throw new CheckException(msg);
            }
        }
        return severity;
    }

    /** Ritorna il parametro di configurazione trimmato oppure il default value se il parametro è null.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @param defaultValue valore di default
     * @return Il parametro "trimmato" o il valore di default specificato.
     */
    public static String parameterWithDefault(String name, String param, String defaultValue){
        try {
            return parameter(name, param);
        } catch (MissingParameterException ex) {
            return defaultValue;
        }
    }

    /** Questo metodo controlla esclusivamente la presenza di un
     * parametro di configurazione e lo ritorna trimmato.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il parametro "trimmato".
     */
    public static String parameter(String name, String param)
            throws MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        return param;
    }

    /**
     * Vedi parameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il parametro "trimmato".
     */
    public String parameter(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return parameter(name, cfg.getProperty(name));
    }

    /** Ritorna l'intero corrispondente al parametro di configurazione trimmato oppure il default value se il parametro è null.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @param defaultValue valore di default
     * @return Il parametro "trimmato" o il valore di default specificato.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     */
    public static int intParameterWithDefault(String name, String param, int defaultValue)
            throws CheckException{
        try {
            return intParameter(name, param);
        } catch (MissingParameterException ex) {
            return defaultValue;
        }
    }

    /** Questo metodo controlla la presenza di un
     * parametro intero.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il valore <CODE>int</CODE> del parametro.
     */
    public static int intParameter(String name, String param)
            throws CheckException,
                   MissingParameterException{
        int value;
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            value = Integer.parseInt(param);
        }
        catch(NumberFormatException e){
            throw new CheckException(getHeader(name)
                                     + "il parametro deve essere un numero intero");
        }
        return value;
    }
    
    /**
     * Vedi intParameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return  Il valore <CODE>int</CODE> del parametro.
     */
    public int intParameter(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return intParameter(name, cfg.getProperty(name));
    }

    /** Ritorna il long corrispondente al parametro di configurazione trimmato oppure il default value se il parametro è null.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @param defaultValue valore di default
     * @return Il parametro "trimmato" o il valore di default specificato.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     */
    public static long longParameterWithDefault(String name, String param, long defaultValue)
            throws CheckException{
        try {
            return longParameter(name, param);
        } catch (MissingParameterException ex) {
            return defaultValue;
        }
    }

    /** Questo metodo controlla la presenza di un
     * parametro di tipo long.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il valore <CODE>long</CODE> del parametro.
     */
    public static long longParameter(String name, String param)
            throws CheckException,
                   MissingParameterException{
        long value;
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            value = Long.parseLong(param);
        }
        catch(NumberFormatException e){
            throw new CheckException(getHeader(name)
                                     + "il parametro deve essere un numero long");
        }
        return value;
    }

    /**
     * Vedi longParameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il valore <CODE>long</CODE> del parametro.
     */
    public long longParameter(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return longParameter(name, cfg.getProperty(name));
    }

    /** Ritorna il float corrispondente al parametro di configurazione trimmato oppure il default value se il parametro è null.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @param defaultValue valore di default
     * @return Il parametro "trimmato" o il valore di default specificato.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     */
    public static float floatParameterWithDefault(String name, String param, float defaultValue)
            throws CheckException{
        try {
            return floatParameter(name, param);
        } catch (MissingParameterException ex) {
            return defaultValue;
        }
    }

    /** Questo metodo controlla la presenza di un
     * parametro float.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il valore <CODE>float</CODE> del parametro.
     */
    public static float floatParameter(String name, String param)
            throws CheckException,
                   MissingParameterException{
        float value;
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            value = Float.parseFloat(param);
        }
        catch(NumberFormatException e){
            throw new CheckException(getHeader(name)
                                     + "il parametro deve essere un numero float");
        }
        return value;
    }

    /**
     * Vedi longParameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il valore <CODE>float</CODE> del parametro.
     */
    public float floatParameter(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return floatParameter(name, cfg.getProperty(name));
    }

    /** Ritorna il boleano corrispondente al parametro di configurazione trimmato oppure il default value se il parametro è null.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @param defaultValue valore di default
     * @return Il parametro "trimmato" o il valore di default specificato.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     */
    public static boolean booleanParameterWithDefault(String name, String param, boolean defaultValue)
            throws CheckException{
        try {
            return booleanParameter(name, param);
        } catch (MissingParameterException ex) {
            return defaultValue;
        }
    }
    
    /** Questo metodo controlla la presenza di un
     * parametro associabile a un tipo boolean.
     * Si associano true | yes | si | 1 al valore true,
     * e false | no | 0 al valore false.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il valore <CODE>boolean</CODE> del parametro.
     */
    public static boolean booleanParameter(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        if(param.equalsIgnoreCase("yes")
           || param.equalsIgnoreCase("true")
           || param.equalsIgnoreCase("si")
           || param.equals("1")){
            return true;
        }
        else if(param.equalsIgnoreCase("no")
                || param.equalsIgnoreCase("false")
                || param.equals("0")){
            return false;
        }
        throw new CheckException(getHeader(name)
                                 + "il parametro deve rappresentare un boolean "
                                 + "in uno dei modi seguenti: \n"
                                 + "- yes | no (case insensitive) \n"
                                 + "- true | false (case insensitive) \n"
                                 + "- si | no (case insensitive) \n"
                                 + "- 1 | 0 \n");
    }

    /**
     * Vedi booleanParameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return  Il valore <CODE>boolean</CODE> del parametro.
     */
    public boolean booleanParameter(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return booleanParameter(name, cfg.getProperty(name));
    }

    /** Questo metodo controlla la presenza di un
     * parametro di tipo UUID.
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non ha
     * una dimensione di 32.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return String Il valore (trimmato) del parametro (32 caratteri).
     */
    public static String uuidParameter(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        if(param.length() != 32) // 32 dovrebbe essere una costante statica nella classe defaults!!!
        {
            throw new CheckException(getHeader(name)
                                     + "il parametro deve essere un UUID (String 32 caratteri)");
        }
        return param;
    }
    
    /**
     * Vedi uuidParameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return  String Il valore (trimmato) del parametro (32 caratteri).
     */
    public String uuidParameter(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return uuidParameter(name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla esclusivamente che il parametro
     * relativo all'estensione dei file audio sia "al" o "mp3" o "wav"
     * @param name Il nome del parametro.
     * @param param Il parametro estensione dei file da controllare.
     * @throws CheckException Se il formato passato non é supportato
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE> o non é <CODE>al</CODE>
     * o <CODE>mp3</CODE> o <CODE>wav</CODE>.
     * @return Il parametro "trimmato".
     */
    public static String audioExtension(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        if((!param.equals("al"))
           && (!param.equals("mp3"))
           && (!param.equals("wav"))){
            throw new CheckException(getHeader(name)
                                     + "le estensioni consentite per i file audio sono solo: "
                                     + "al - mp3 - wav");
        }
        return param;
    }
    
    /**
     * Vedi audioExtension
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il parametro "trimmato".
     */
    public String audioExtension(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return audioExtension(name, cfg.getProperty(name));
    }
    
    /** Testa la validitá di un formato di data o timestamp
     * inserito come parametro (es. AAMMGGhhmm)
     * @param name Il nome del parametro da controllare
     * @param param Il parametro
     * @return Il parametro trimmato e verificato
     * @throws CheckException Sollevata se il formato é errato
     * @throws MissingParameterException Sollevata se il parametro é null
     */
    public static String simpleDateFormat(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            new SimpleDateFormat(param);
        }
        catch(IllegalArgumentException e){
            throw new CheckException(getHeader(name)
                                     + "i formati consentiti sono descritti nella javadoc "
                                     + "della classe SimpleDateFormat della SUN");
        }
        return param;
    }
    
    /**
     * Vedi simpleDateFormat
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il parametro trimmato e verificato.
     */
    public String simpleDateFormat(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return simpleDateFormat(name, cfg.getProperty(name));
    }
    
    /**
     * Testa un parametro di introspection di tipo nome classe
     * inserito come parametro (es. com.citecvoice.sftelco.kernel.util.Check)
     * 
     * @param name Il nome del parametro da controllare
     * @param param Il parametro
     * @return la classe corrispondente
     * @throws CheckException Sollevata se il formato é errato oppure se la
     * classe non esiste
     * @throws MissingParameterException Sollevata se il parametro é null
     */
    public static Class<?> className(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        Class<?> theClass;
        try{
            theClass = Class.forName(param);
        }
        catch(Exception e){
            throw new CheckException(getHeader(name)
                                     + "il nome di una classe si compone di stringhe separate da "
                                     + "punti e la classe deve trovarsi nel classpath nel package opportuno");
        }
        return theClass;
    }
    
    /**
     * Vedi className
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
         * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return la classe corrispondente
     */
    public Class<?> className(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return className(name, cfg.getProperty(name));
    }
    
    /**
     * Testa un parametro di introspection di tipo nome classe
     * inserito come parametro (es. com.citecvoice.sftelco.kernel.util.Check)
     * 
     * @param <T> Il tipo/supertipo della classe che ci si aspetta
     * @param name Il nome del parametro da controllare
     * @param param Il parametro
     * @param type Superclasse della classe rappresentata dal parametro
     * @return Il parametro trimmato e verificato
     * @throws CheckException Sollevata se il formato é errato oppure se la
     * classe non esiste
     * @throws MissingParameterException Sollevata se il parametro é null
     */
    public static <T> Class<? extends T> className(String name, String param, Class<T> type)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        Class<?> theClass;
        try{
            theClass = Class.forName(param);
        }
        catch(Exception e){
            throw new CheckException(getHeader(name)
                                     + "il nome di una classe si compone di stringhe separate da "
                                     + "punti e la classe deve trovarsi nel classpath nel package opportuno");
        }
        
        Class<? extends T> castedClass;
        try{
            castedClass = theClass.asSubclass(type);
        }
        catch(ClassCastException e){
            throw new CheckException(getHeader(name)
                                     + "il parametro passato non rappresenta una classe del tipo corretto, "+type);
        }
                
        return castedClass;
    }

    /**
     * Vedi className
     * @param <T> il tipo che ci si aspetta di ottenere
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @param type non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return la classe corrispondente
     */
    public <T> Class<? extends T> className(String name, Class<T> type) throws CheckException{
        if (type==null) throw new NullPointerException();
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return className(name, cfg.getProperty(name),type);
    }
    
    /**
     * 
     * @param cl il class loader
     * @param name il nome del parametro
     * @param param il parametro (la classe)
     * @return la Classe
     * @throws CheckException in caso di errore
     * @throws MissingParameterException se è null
     */
    public static Class<?> className(ClassLoader cl, String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        if(cl == null){
            throw new CheckException("Il ClassLoader e' null.");
        }
        param = param.trim();
        Class<?> theClass;
        try{
            theClass = Class.forName(param, true, cl);
        }
        catch(Exception e){
            throw new CheckException(getHeader(name)
                                     + "il nome di una classe si compone di stringhe separate da "
                                     + "punti e la classe deve trovarsi nel classpath nel package opportuno");
        }
        return theClass;
    }

    /**
     * Vedi className
     * @param cl ClassLoader; non può essere null (altrimenti solleva NullPointerException).
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return la classe
     */
    public Class<?> className(ClassLoader cl, String name) throws CheckException{
        if (cl==null) throw new NullPointerException();
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return className(cl, name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla la correttezza sintattica
     * di un e-mail address.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return L'e-mail address trimmato.
     */
    public static String mailAddress(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        int idxEt = param.indexOf("@");
        String nome = null;
        String dominio = null;
        if(idxEt != -1){
            nome = param.substring(0, idxEt);
            dominio = param.substring(idxEt + 1);
        }
        if(idxEt == -1 || nome.equals("") || dominio.equals("") || dominio.contains("@") || !dominio.contains(".")){
            throw new CheckException(getHeader(name)
                                     + "un e-mail address deve essere nella forma nome@dominio.estensione");
        }
        return param;
    }
    
    /**
     * Vedi mailAddress
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return L'e-mail address trimmato.
     */
    public String mailAddress(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return mailAddress(name, cfg.getProperty(name));
    }

    /** Questo metodo controlla la correttezza sintattica
     * di una regular expression.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return La regular expression trimmata.
     */
    public static String regex(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            Pattern.compile(param);
        }
        catch(PatternSyntaxException pse){
            String msg = getHeader(name)
                         + "il parametro non é una regular expression valida";
            throw new CheckException(msg, pse);
        }
        return param;
    }

    /**
     * Vedi regex
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return La regular expression trimmata.
     */
    public String regex(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return regex(name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla la correttezza sintattica di un environment
     * di logging (vedi classe LoggerFactoryForge)
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return l'environment
     * @deprecated Questo metodo è reso obsoleto dall'utilizzo di slf4j/logback
     */
    public static int loggerEnvironment(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        int value;
        try{
            value = Integer.parseInt(param);
        }
        catch(NumberFormatException e){
            throw new CheckException(getHeader(name)
                                     + "il parametro deve essere un numero intero");
        }
        if(value < 0 || value > 1){
            throw new CheckException(getHeader(name)
                                     + "il parametro deve essere uno dei valori supportati dalla classe LoggerFactoryForge");
        }
        return value;
    }

    /** 
     * Questo metodo verifica che il parametro ricevuto sia un URL.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException se il parametro non specifica
     * un protocollo sconosciuto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return la stringa URL trimmata.
     */
    public static String URL(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            new java.net.URL(param);
        }
        catch(MalformedURLException pse){
            String msg = getHeader(name)
                         + "il parametro non é un URL valido";
            throw new CheckException(msg, pse);
        }
        return param;
    }

    /**
     * Vedi URL
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return la stringa URL trimmata.
     */
    public String URL(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return URL(name, cfg.getProperty(name));
    }
        
    /** 
     * Questo metodo controlla che il parametro ricevuto sia
     * un URI sintatticamente corretto.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException  se il parametro non rispetta
     * la sintassi prevista per un URI.
     * @throws MissingParameterException se il parametro é <CODE>null</CODE>.
     * @return l'oggetto URI generato a partire dal parametro
     * ricevuto
     */
    public static URI URI(String name, String param)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            return new java.net.URI(param);
        }
        catch(URISyntaxException pse){
            String msg = getHeader(name)
                         + "il parametro non é un URI valido";
            throw new CheckException(msg, pse);
        }
    }

    /**
     * Vedi URI
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return l'oggetto URI generato a partire dal parametro
     * ricevuto
     */
    public URI URI(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return URI(name, cfg.getProperty(name));
    }
    
    /** Questo metodo controlla i parametri di tipo Date
     * @param name Il nome del parametro.
     * @param param Il parametro da controllare.
     * @param pattern il pattern
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il Date corrispondente al parametro.
     */
    public static Date dateParameter(String name, String param, String pattern)
            throws CheckException,
                   MissingParameterException{
        if(param == null){
            throw new MissingParameterException(name);
        }
        if(param.equals("null"))//da rivedere!
        {
            return null;
        }
        return StringConverter.toDate(param, pattern);
    }
    
    /**
     * Vedi dateParameter
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @param pattern Il pattern (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return La conversione in data del parametro.
     */
    public Date dateParameter(String name, String pattern) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (pattern==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return dateParameter(name, cfg.getProperty(name), pattern);
    }
    


    /** Questo metodo verifica la correttezza sintattica di un
     * numero di porta IP, cioé che sia compreso tra 1 e 65535.
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio).
     * @param param Il parametro da controllare.
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il numero della porta.
     */
    public static int ipPort(String name, String param)
        throws CheckException, MissingParameterException{

        String msg = getHeader(name) +
                     "una porta IP é identificata da un numero intero " +
                     "compreso tra 1 e 65535";

        if(param == null){
            throw new MissingParameterException(name);
        }
        param = param.trim();
        try{
            int port = Integer.parseInt(param);
            if(port < 1 || port > 65535){
                throw new CheckException(msg);
            }
            return port;
        }
        catch(NumberFormatException e){
            throw new CheckException(msg);
        }
    }
    
    /**
     * Vedi ipPort
     * @param name Il nome del parametro (inserito nel messaggio
     * di dettaglio); non può essere null (altrimenti solleva NullPointerException).
     * @throws CheckException Sollevata se il parametro non é
     * sintatticamente corretto.
     * @throws MissingParameterException Sollevata
     * se il parametro é <CODE>null</CODE>.
     * @return Il numero della porta.
     */
    public int ipPort(String name) throws CheckException{
        if (name==null) throw new NullPointerException();
        if (cfg==null) throw new IllegalStateException(getIllegalStateMessage(name));
        return ipPort(name, cfg.getProperty(name));
    }
}
