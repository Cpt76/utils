package it.colaneri.file.comparators;

import java.io.File;
import java.io.IOException;

/**
 * <p>Title: FilenameComparator</p>
 * <p>Description: Permette di comparare e ordinare due file sulla base del
 * numero di sequenza contenuto nei naming. Possibile impostare un
 * ordinamento crescente o decrescente</p>
 */
public class FileNameComparator
    implements java.util.Comparator<File>{

    /**
     * posizione del numero di sequenza all'interno del filename
     */
    private int sequenceShift;

    /**
     * lunghezza del numero di sequenza all'interno del filename
     */
    private int sequenceLength;


    /**
     * abilita l'ordinamento decrescente
     */
    private boolean decreasingMode = false;

    /**
     * Crea un comparatore per ordinare in modo crescente o decrescente una
     * lista di file sulla base del numero di sequenza contenuto all'interno
     * del loro naming.
     * @param sequenceShift int posizione del numero di sequenza all'interno
     * del filename. I valori impostabili sono :
     * sequenceShift = 0 il numero di sequenza inizia in testa al nome del file;
     * sequenceShift > 0 indica la dimensione del prefisso che precede il
     * numero di sequenza;
     * sequenceShift < 0 indica la posizione rispetto all'estensione compresa
     * del '.'; quindi -1 indica che la sequenza � subito prima dell'estensione,
     * -3 che tra inizio sequenza e estensione ci sono 2 caratteri di postfisso
     * @param sequenceLength int numero di caratteri che compone il numero di sequenza
     */
    public FileNameComparator(int sequenceShift, int sequenceLength){
        this.sequenceShift = sequenceShift;
        this.sequenceLength = sequenceLength;
    }


    ////////////////////////////////////////////////////////////////////////////
    /**
     * Consente di abilitare l'ordinamento decrescente della lista di file
     */
    public void setDecreasingMode(){
        this.decreasingMode = true;
    }

    /** Overriding.
     * @param file1 Il primo File
     * @param file2 Il secondo File
     * @return -1 se il numero di sequenza contenuta nel naming del primo file �
     * minore di quella del secondo; 0 se sono uguali; 1 il numero di sequenza
     * contenuta nel naming del primo file � maggiore di quella del secondo
     * @throws IllegalArgumentException se i file in ingresso non rispettano le
     * regole di naming previste o non esistono
     */
    @Override
    public int compare(File file1, File file2){
        int value = 0;

        try{
            int sequence1 = this.getNumSequence(file1);
            int sequence2 = this.getNumSequence(file2);
            if(!decreasingMode){
                if(sequence1 > sequence2){
                    value = 1;
                }
                if(sequence1 < sequence2){
                    value = -1;
                }
            }
            else{
                if(sequence1 > sequence2){
                    value = -1;
                }
                if(sequence1 < sequence2){
                    value = 1;
                }
            }
        }
        catch(IOException e){
            throw new IllegalArgumentException(e);
        }

        return value;
    }

    ////////////////////////////////////////////////////////////////////////////
    /** Dato un file ritorna il numero di sequenza contenuto nel naming
     *
     * @param file il file dal cui nome si vuole estrarre il numero di sequenza
     * @return il numero di sequenza
     * @throws IOException se � impossibile estrarre la sequenza
     */
    public int getNumSequence(File file)
        throws IOException{

        String numSequence;
        int num;

        String name = file.getName();

        try{
            int index = name.lastIndexOf(".");
            if(index > 0){
                name = name.substring(0, index);
            }

            if(sequenceShift >= 0){
                numSequence = name.substring(sequenceShift,
                                             sequenceShift + sequenceLength);
            }
            else{
                numSequence = name.substring(index - sequenceLength +
                                             sequenceShift + 1,
                                             index + sequenceShift + 1);
            }

            num = Integer.parseInt(numSequence);
        }
        catch(Exception e){
            throw new IOException(
                "Impossibile estrarre il numero di sequenza dal file " +
                file.getPath());
        }

        return num;
    }

}
