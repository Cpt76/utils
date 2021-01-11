/*
 * FileSystem.java
 *
 * Created on 23 maggio 2002, 14.19
 */

package it.colaneri.file;

import it.colaneri.time.Timestamp;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.StringTokenizer;

///////////////////////////////////////////////////////////////////////////
/** Questa classe offre metodi static per effettuare le
 * operazioni più comuni su filesystem
 */
public class FileSystem{
    /** Dimensione del buffer di trasferimento standard */
    public static final int DEFAULTBUFFERSIZE = 1024;

    ///////////////////////////////////////////////////////////////////////
    /** Crea recursivamente le directory contenute in un path di file.
     * Il path deve essere di un file oppure terminare col
     * carattere /; il separatore è /
     * @return La directory creata o null in caso di errore
     * @param path Il path del quale bisogna creare le directory
     */
    public static File createDir(String path){
        if(!path.startsWith("/")){
            return null;
        }

        int from = path.indexOf("/");
        int to = path.lastIndexOf("/");
        path = path.substring(from, to);

        String dir = "";
        StringTokenizer st = new StringTokenizer(path, "/");
        File newdir = null;
        while(st.hasMoreTokens()){
            dir += "/" + st.nextToken();
            newdir = new File(dir);
            //noinspection ResultOfMethodCallIgnored
            newdir.mkdir();
        }
        return newdir;
    }

    ///////////////////////////////////////////////////////////////////////
    /** Copia un file
     * @param in File sorgente
     * @param out File destinazione
     * @throws FileNotFoundException File sorgente non trovato
     * @throws IOException Impossibile effettuare la copia
     */
    public static void copy(File in, File out)
        throws FileNotFoundException, IOException{

        copy(in, out, DEFAULTBUFFERSIZE);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Copia un file
     * @param in File sorgente
     * @param out File destinazione
     * @param bufferSize Dimensione del buffer
     * @throws FileNotFoundException File sorgente non trovato
     * @throws IOException Impossibile effettuare la copia
     */
    public static void copy(File in, File out, int bufferSize)
        throws FileNotFoundException, IOException{

        if(in.isDirectory()){
            throw new IOException("Il path " + in.getPath() +
                                  " corrisponde " +
                                  "a una directory");
        }

        int i;
        byte[] buf = new byte[bufferSize];

        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);

        while((i = fis.read(buf)) != -1){
            fos.write(buf, 0, i);
        }

        fis.close();
        fos.close();
    }

    ///////////////////////////////////////////////////////////////////////
    /** Sposta un file
     * @return true se è stato possibile cancellare il
     * file sorgente; false se non è stato possibile.
     * @param bufferSize Dimensione del buffer in byte
     * @param in File sorgente
     * @param out File destinazione
     * @throws FileNotFoundException File sorgente non trovato
     * @throws IOException Impossibile effettuare la copia
     */
    public static boolean move(File in, File out, int bufferSize)
        throws FileNotFoundException, IOException{

        FileSystem.copy(in, out, bufferSize);
        return in.delete();
    }

    ///////////////////////////////////////////////////////////////////////
    /** Sposta un file
     * @param in File sorgente
     * @param out File destinazione
     * @throws FileNotFoundException File sorgente non trovato
     * @throws IOException Impossibile effettuare la copia
     * @return true se è stato possibile cancellare il
     * file sorgente; false se non è stato possibile.
     */
    public static boolean move(File in, File out)
        throws FileNotFoundException, IOException{

        FileSystem.copy(in, out, DEFAULTBUFFERSIZE);
        return in.delete();
    }

    ///////////////////////////////////////////////////////////////////////
    /** Sposta un file appendendo un timestamp in testa al nome
     * Se la stringa di timestamp è null si comporta esattamente come
     * una normale move @link FileSystem#move(File in, File out)
     * @param in File sorgente
     * @param out File destinazione
     * @param timestampFormat formato del timestamp da appendere
     * @throws FileNotFoundException File sorgente non trovato
     * @throws IOException Impossibile effettuare la copia
     * @return true se è stato possibile cancellare il
     * file sorgente; false se non è stato possibile.
     */
    public static boolean move(File in, File out, String timestampFormat)
        throws FileNotFoundException, IOException{
        if(timestampFormat != null){

            String timestamp = new Timestamp(timestampFormat).toString();
            String outFilePath = out.getPath();
            if(File.separator.equals("\\")){
                outFilePath = outFilePath.replace('\\', '/');
            }
            String outFileDirPath = outFilePath.substring(0,
                                                          (outFilePath.
                                                           lastIndexOf('/') +
                                                           1));
            String outFilename = out.getName();
            FileSystem.move(in,
                            new File(outFileDirPath +
                                     timestamp + "_" + outFilename));
        }
        else{
            FileSystem.copy(in, out, DEFAULTBUFFERSIZE);
        }
        return in.delete();
    }

    ///////////////////////////////////////////////////////////////////////
    /** Copia la directory sorgente nella directory destinazione, creando
     * la directory destinazione se necessario. Si noti che la procedura
     * può effettuare la copia integrale della directory sorgente,
     * riproducendo in quella destinazione le stesse sottodirectory
     * e gli stessi file (modalità recursiva), oppure può limitarsi
     * a copiare i soli file presenti nella directory sorgente,
     * ignorando le subdirectory (modalità non recursiva).
     * Utilizza per la copia dei file la
     * {@link FileSystem#DEFAULTBUFFERSIZE}
     * @param in La directory sorgente.
     * @param out La directory destinazione. Questa può anche
     * non esistere fisicamente, nel qual caso
     * viene creata.
     * @param recursive Imposta la modalità recursiva o quella
     * non recursiva.
     * @throws FileNotFoundException Sollevata se la directory sorgente
     * non esiste
     * @throws IOException Sollevata in caso di errore di I/O
     * durante la copia
     */
    public static void copyDir(File in,
                               File out,
                               boolean recursive)
        throws FileNotFoundException, IOException{

        copyDir(in, out, recursive, null, DEFAULTBUFFERSIZE);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Copia la directory sorgente nella directory destinazione, creando
     * la directory destinazione se necessario. Si noti che la procedura
     * può effettuare la copia integrale della directory sorgente,
     * riproducendo in quella destinazione le stesse sottodirectory
     * e gli stessi file (modalità recursiva), oppure può limitarsi
     * a copiare i soli file presenti nella directory sorgente,
     * ignorando le subdirectory (modalità non recursiva).
     * Una nota sul FileFilter: agisce esclusivamente sui file, quindi
     * le eventuali subdirectory sono sempre replicate.
     * Utilizza per la copia dei file la
     * {@link FileSystem#DEFAULTBUFFERSIZE}
     * @param filter Un filtro per selezionare quali file copiare.
     * @param in La directory sorgente.
     * @param out La directory destinazione. Questa può anche
     * non esistere fisicamente, nel qual caso
     * viene creata.
     * @param recursive Imposta la modalità recursiva o quella
     * non recursiva.
     * @throws FileNotFoundException Sollevata se la directory sorgente
     * non esiste
     * @throws IOException Sollevata in caso di errore di I/O
     * durante la copia
     */
    public static void copyDir(File in,
                               File out,
                               boolean recursive,
                               FileFilter filter)
        throws FileNotFoundException, IOException{

        copyDir(in, out, recursive, filter, DEFAULTBUFFERSIZE);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Copia la directory sorgente nella directory destinazione, creando
     * la directory destinazione se necessario. Si noti che la procedura
     * può effettuare la copia integrale della directory sorgente,
     * riproducendo in quella destinazione le stesse sottodirectory
     * e gli stessi file (modalità recursiva), oppure può limitarsi
     * a copiare i soli file presenti nella directory sorgente,
     * ignorando le subdirectory (modalità non recursiva).
     * Una nota sul FileFilter: agisce esclusivamente sui file, quindi
     * le eventuali subdirectory sono sempre replicate.
     * @param filter Un filtro per selezionare quali file copiare.
     * @param bufferSize Specifica la dimensione del buffer di copia.
     * Il valore di default è {@link FileSystem#DEFAULTBUFFERSIZE}
     * @param in La directory sorgente.
     * @param out La directory destinazione. Questa può anche
     * non esistere fisicamente, nel qual caso
     * viene creata.
     * @param recursive Imposta la modalità recursiva o quella
     * non recursiva.
     * @throws FileNotFoundException Sollevata se la directory sorgente
     * non esiste
     * @throws IOException Sollevata in caso di errore di I/O
     * durante la copia
     */
    public static void copyDir(File in,
                               File out,
                               boolean recursive,
                               FileFilter filter,
                               int bufferSize)
        throws FileNotFoundException, IOException{

        if(!in.exists()){
            throw new FileNotFoundException();
        }
        else{
            if(!in.isDirectory()){
                throw new IOException("Il parametro " +
                                      "passato non è una directory valida");
            }
        }

        if(out.isFile()){
            throw new IOException("La destinazione non è una directory: " +
                                  out.getPath());
        }

        if(!out.exists()){
            if(!out.mkdirs()){
                throw new IOException("Impossibile creare " +
                                      "la directory destinazione " +
                                      out.getPath());
            }
        }

        File[] list = in.listFiles();
        assert list != null; //è stato verificato prima
        for(File list1:list){
            File outFile = new File(out.getPath() + "/" + list1.getName());
            if(list1.isDirectory()){
                if(recursive){
                    copyDir(list1, outFile, true, filter, bufferSize);
                }
            }
            else{
                if(filter == null || filter.accept(outFile)){
                    copy(list1, outFile, bufferSize);
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
    /** Sposta in modo recursivo la directory sorgente nella directory
     * destinazione, creando la directory destinazione se necessario.
     * Utilizza per lo spostamento dei file la
     * {@link FileSystem#DEFAULTBUFFERSIZE}
     * @param in La directory sorgente.
     * @param out La directory destinazione. Questa può anche
     * non esistere fisicamente, nel qual caso
     * viene creata.
     * @throws FileNotFoundException Sollevata se la directory sorgente
     * non esiste
     * @throws IOException Sollevata in caso di errore di I/O
     * durante la copia */
    public static void moveDir(File in,
                               File out)
        throws FileNotFoundException, IOException{

        copyDir(in, out, true, null, DEFAULTBUFFERSIZE);

        cleanDir(in);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Sposta in modo recursivo la directory sorgente nella directory
     * destinazione, creando la directory destinazione se necessario.
     * @param bufferSize La dimensione del buffer usato per la copia
     * @param in La directory sorgente.
     * @param out La directory destinazione. Questa può anche
     * non esistere fisicamente, nel qual caso
     * viene creata.
     * @throws FileNotFoundException Sollevata se la directory sorgente
     * non esiste
     * @throws IOException Sollevata in caso di errore di I/O
     * durante la copia
     */
    public static void moveDir(File in,
                               File out,
                               int bufferSize)
        throws FileNotFoundException, IOException{

        copyDir(in, out, true, null, bufferSize);

        cleanDir(in);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Questo metodo effettua il cleanup di un albero
     * di directories, agendo in modo recursivo e cancellando tutti i file
     * "expired" che rispettano il filtro specificato. E'possibile
     * specificare il numero di livelli di directory che devono essere
     * preservati anche qualora la directory restasse vuota (safelevels)
     * @param dir La directory sulla quale operare
     * @param filter Un filtro che stabilisce quali file cancellare
     * @param minutes Il numero di minuti superato il quale
     * una dedica si considera scaduta.
     * @param safelevels Questo parametro permette di preservare dalla
     * cancellazione tutte le directories vuote
     * di livello minore o uguale al suo valore:
     * ad es. safelevels impostato a 2 garantisce
     * che la directory stessa indicata come primo
     * parametro e tutte le directories in essa
     * contenute NON SARANNO cancellate anche se
     * vuote; tutte le directories contenute in queste
     * ultime saranno invece cancellate qualora
     * tutti i file in esse contenuti fossero scaduti.
     * @throws IOException Scatenata se il path indicato come primo
     * parametro è errato o in caso di errori bloccanti durante il cleanup
     * @return Il numero di file cancellati
     */
    public static int cleanExpired(File dir,
                                   FileFilter filter,
                                   int minutes,
                                   int safelevels)
        throws IOException{

        return clean(dir, filter, true, minutes, safelevels);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Questo metodo effettua la cancellazione di un albero
     * di directories, agendo in modo recursivo.
     * Tutte le directory e i file sono cancellati.
     * Equivale a {@link FileSystem#cleanExpired(File,FileFilter,int,int)}
     * con <CODE>minutes=0</CODE> e <CODE>safelevels=1</CODE>
     * @param dir La directory sulla quale operare
     * @throws IOException Scatenata se il path indicato come primo
     * parametro è errato.
     * @return Il numero di file cancellati
     */
    public static int cleanDir(File dir)
        throws IOException{

        return clean(dir, null, true, 0, 1);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Questo metodo effettua la cancellazione di un albero
     * di directories, agendo in modo recursivo.
     * Tutte le directory e i file sono cancellati.
     * Equivale a
     * {@link FileSystem#cleanExpired(File, FileFilter, int, int)}
     * con <CODE>minutes=0</CODE> e <CODE>safelevels=0</CODE>
     * @param dir La directory sulla quale operare
     * @throws IOException Scatenata se il path indicato come primo
     * parametro è errato.
     * @return Il numero di file cancellati
     */
    public static int eraseDir(File dir)
        throws IOException{

        return clean(dir, null, true, 0, 0);
    }
    
    /**
     * Elimina tutte le directories vuote sotto una certa root
     * @param rootDir la directory dentro cui cercare le directories vuote; non
     * viene cancellata anche se vuota!
     * @return il numero di directories vuote trovate e cancellate
     */
    public static int cleanEmptyDir(File rootDir){
            if (rootDir==null) throw new IllegalArgumentException("rootDir non può essere null");
            //Listing della directory
            File[] listing = rootDir.listFiles();
            if (listing==null) throw new IllegalArgumentException("rootDir non è un path valido");
            int count= 0;
            //Cancellazione recursiva
            for(File f:listing){
             
                if(f.isDirectory()){
                    //Esegui la pulizia della subdir
                    count+= cleanEmptyDir(f);
                    //Poi se l'attuale dir � vuota cancellala
                    if (Objects.requireNonNull(f.listFiles()).length==0){
                        if (f.delete()) count++;
                    }
                }
            }

            return count;
    }

    ///////////////////////////////////////////////////////////////////////
    /** Questo metodo effettua il cleanup di un albero
     * di directories, agendo in modo recursivo e cancellando tutti i file
     * selezionati dal filtro.
     * Equivale a
     * {@link FileSystem#cleanSelectedFiles(File, FileFilter, boolean)}
     * con <CODE>recursive=true</CODE>
     * @return Il numero di file cancellati
     * @param filter Un filtro che stabilisce cosa cancellare
     * @param dir La directory sulla quale operare
     * @throws IOException Scatenata se il path indicato come primo
     * parametro è errato.
     */
    public static int cleanSelectedFiles(File dir,
                                         FileFilter filter)
        throws IOException{

        return cleanSelectedFiles(dir, filter, true);
    }

    ///////////////////////////////////////////////////////////////////////
    /** Questo metodo effettua il cleanup di una o più
     * directories (agendo in modo recursivo oppure
     * sulla singola directory specificata, parametro
     * <CODE>recursive</CODE>).
     * Cancella solo i file accettati dal filtro,
     * le directory anche vuote sono sempre preservate.
     * @return Il numero di file cancellati
     * @param recursive Abilita/disabilita la recursione
     * @param dir La directory sulla quale operare
     * @param filter Un filtro che stabilisce cosa cancellare
     * @throws IOException Scatenata se il path indicato come primo
     * parametro è errato.
     */
    public static int cleanSelectedFiles(File dir,
                                         FileFilter filter,
                                         boolean recursive)
        throws IOException{

        return clean(dir, filter, recursive, 0, Integer.MAX_VALUE);
    }

    ///////////////////////////////////////////////////////////////////////
    /**
     * Ritorna la dimensione di una directory espressa in byte.
     * Se recursive è true la dimensione totale viene calcolata in modo
     * recursivo su tutte le subdirectory.
     *
     * @param dir File directory di cui calcolare la dimesione
     * @param recursive boolean se true abilita il calcolo recursivo
     * della dimensione
     *
     * @return long dimensione della directory espressa in byte
     */
    public static long directorySize(File dir, boolean recursive){
        if (dir==null) throw new IllegalArgumentException("Il parametro dir non può essere null");
        File[] listing = dir.listFiles();
        if (listing==null) throw new IllegalArgumentException("Il parametro dir deve essere una directory valida");
        long size = 0;
        for(File listing1:listing){
            if(listing1.isFile()){
                size = size + listing1.length();
            }
            if(listing1.isDirectory() && recursive){
                size = size + directorySize(listing1, recursive);
            }
        }
        return size;
    }

    ///////////////////////////////////////////////////////////////////////
    //Il metodo privato su cui poggiano le clean
    private static int clean(File dir,
                             FileFilter filter,
                             boolean recursive,
                             int minutes,
                             int safelevels)
        throws IOException{

        int counter = 0; //Per il conteggio dei file cancellati

        assert dir!= null;
        assert dir.isDirectory();

        try{
            //Listing della directory
            File[] listing = dir.listFiles();
            assert listing!=null;

            //Cancellazione recursiva
            for(File listing1:listing){
                if(listing1.isDirectory()){
                    if(recursive){
                        counter += clean(listing1, filter, true, minutes, safelevels - 1);
                    }
                }
                else{
                    Date now = new Date();
                    long delta = now.getTime() - (long) minutes * 60 * 1000;
                    if(listing1.lastModified() < delta){
                        if(filter == null || filter.accept(listing1)){
                            boolean delete = listing1.delete();
                            if (!delete) System.err.println("Impossibile cancellare "+listing1.getPath());
                            counter++;
                        }
                    }
                }
            }

            File[] list= dir.listFiles();
            assert list!=null;
            if(list.length == 0 && safelevels <= 0){
                boolean delete = dir.delete();
                if (!delete) System.err.println("Impossibile cancellare "+dir.getPath());
            }
            return counter;
        }
        catch(IOException e){
            throw(IOException)e.fillInStackTrace();
        }
        
    }


    ////////////////////////////////////////////////////////////////////////////
    //////////////// Metodi main per testare i singoli metodi //////////////////
    ////////////////////////////////////////////////////////////////////////////

    /* Test del metodo createDir
         public static void main (String args[]) {
        System.out.println("Esecuzione iniziata.");
        if(args.length == 1) {
            String path = args[0];
            File f= createDir(path);
     if(f==null) System.out.println("Non e' stata creata alcuna directory");
            else System.out.println("E' stata creata la directory "+f);
        }
        else System.out.println("Parametri errati");
        System.out.println("Esecuzione terminata.");
         }*/

    /* Test del metodo copyDir
         public static void main (String args[]) {
        System.out.println("Esecuzione iniziata.");
        if(args.length == 4) {
            File in= new File(args[0]);
            File out= new File(args[1]);
            boolean recursive= Boolean.valueOf(args[2]).booleanValue();
            int bufferSize= Integer.parseInt(args[3]);
            System.out.println("Parametri passati:");
            System.out.println("in: "+in);
            System.out.println("out: "+out);
            System.out.println("recursive: "+recursive);
            System.out.println("bufferSize: "+bufferSize);
            try {
                copyDir(in, out, recursive, bufferSize);
            }
            catch(FileNotFoundException e){
                System.out.println("Si e' verificata una FileNotFoundException: "+e.getMessage());
            }
            catch(IOException e){
     System.out.println("Si e' verificata una IOException: "+e.getMessage());
            }
        }
        else System.out.println("Parametri errati");
        System.out.println("Esecuzione terminata.");
         }*/

    /* Test del metodo clean
         public static void main (String args[]) {
        System.out.println("Esecuzione iniziata.");
        if(args.length == 5) {
            File dir= new File(args[0]);
            ExtensionFileFilter filter= new ExtensionFileFilter(args[1]);
            boolean recursive= Boolean.valueOf(args[2]).booleanValue();
            int minutes= Integer.parseInt(args[3]);
            int safelevels= Integer.parseInt(args[4]);
            System.out.println("Parametri passati:");
            System.out.println("dir: "+dir);
            System.out.println("filter: "+filter);
            System.out.println("recursive: "+recursive);
            System.out.println("minutes: "+minutes);
            System.out.println("safelevels: "+safelevels);
            int deletedFilesNumber= 0;
            try {
     deletedFilesNumber= clean(dir, filter, recursive, minutes, safelevels);
     System.out.println("Numero di files cancellati: "+deletedFilesNumber);
            }
            catch(IOException e){
     System.out.println("Si e' verificata una IOException: "+e.getMessage());
            }
        }
        else System.out.println("Parametri errati");
        System.out.println("Esecuzione terminata.");
         }*/

    /* Test del metodo cleanDir
         public static void main (String args[]) {
        System.out.println("Esecuzione iniziata.");
        if(args.length == 1) {
            File dir= new File(args[0]);
            System.out.println("Parametri passati:");
            System.out.println("dir: "+dir);
            int deletedFilesNumber= 0;
            try {
                deletedFilesNumber= cleanDir(dir);
     System.out.println("Numero di files cancellati: "+deletedFilesNumber);
            }
            catch(IOException e){
     System.out.println("Si e' verificata una IOException: "+e.getMessage());
            }
        }
        else System.out.println("Parametri errati");
        System.out.println("Esecuzione terminata.");
         }*/

    /* Test del metodo eraseDir
         public static void main (String args[]) {
        System.out.println("Esecuzione iniziata.");
        if(args.length == 1) {
            File dir= new File(args[0]);
            System.out.println("Parametri passati:");
            System.out.println("dir: "+dir);
            int deletedFilesNumber= 0;
            try {
                deletedFilesNumber= eraseDir(dir);
     System.out.println("Numero di files cancellati: "+deletedFilesNumber);
            }
            catch(IOException e){
     System.out.println("Si e' verificata una IOException: "+e.getMessage());
            }
        }
        else System.out.println("Parametri errati");
        System.out.println("Esecuzione terminata.");
         }*/

}
