package it.colaneri.util;

import java.util.StringTokenizer;

public class StringUtils {


	/**
	 * Trimma la stringa in input e la ritorna maiuscola con le lettere seguenti minuscole
	 * @param nome la stringa da rendere minuscola con iniziale maiuscola
	 * @return prima lettera maiuscola, le altre minuscole, trimmata
	 */
	public static String capitalize(String nome){
    	String minuscolo = nome.trim().toLowerCase();
    	
        return minuscolo.substring(0, 1).toUpperCase() + minuscolo.substring(1);
    }

	/**
	 * Utilità per println
	 * @param x ciò che si vuole stampare
	 */
	public static void println(Object x) {
		System.out.println(x);
	}

	/**
	 * Utilità per print
	 * @param x ciò che si vuole stampare
	 */
	public static void print(Object x) {
		System.out.print(x);
	}


	/**
	 * Informa se il valore <value> e' presente all' interno della lista
	 * passata <list> i cui elementi sono separati dai delimitatori <delim>.
	 * @param list String Stringa rappresentante la lista degli elementi tra i
	 * quali eseguire la ricerca
	 * @param delim String Stringa contenente i separatori utilizzati nella lista
	 * passata
	 * @param value String Il valore da cercare all' interno della lista passata
	 * @return boolean True se l' elemento e' presente nella lista. False
	 * altrimenti.
	 */
	public static boolean isContained(String list, String delim, String value) {
		if(list==null) return false;
		StringTokenizer st= new StringTokenizer(list, delim);
		while(st.hasMoreTokens()) {
			if(st.nextToken().equals(value))
				return true;
		}
		return false;
	}

	/* Sostituisce in source i caratteri compresi tra startIndex e endIndex con with.
	 * @param source La stringa su cui operare
	 * @param startIndex L'indice del primo carattere che deve essere sostituito
	 * @param endIndex L'indice dell' ultimo carattere che deve essere sostituito + 1
	 * @param with La stringa che sostituira' i caratteri compresi tra startIndex e endIndex
	 * @return La stringa ottenuta dalla sostituzione
	 */
	/**
	 *
	 * @param source stringa origine
	 * @param startIndex indice di partenza
	 * @param endIndex indice finale
	 * @param with stringa da sostituire
	 * @return la stringa risultante
	 */
	public static String replace(String source, int startIndex, int endIndex, String with) {
		StringBuilder sb= new StringBuilder(source);
		sb.replace(startIndex, endIndex, with);
		return new String(sb);
	}

	/* Sostituisce all' interno di source tutte le occorrenze di what con with.
	 * La sostituzione' e' Key-Sensitive.
	 * @param source La stringa su cui operare
	 * @param what La stringa che deve essere sostituita
	 * @param with La stringa che sostituira' what
	 * @return La stringa ottenuta dalla sostituzione
	 * @throws IllegalArgumentException Se qualunque parametro e' null e se what e' stringa vuota
	 */
	/**
	 *
	 * @param source stringa origine
	 * @param what cosa sostituire
	 * @param with con cosa
	 * @return la nuova stringa
	 */
	public static String replaceAll(String source, String what, String with) {
		if(source==null||what==null||with==null||what.equals(""))
			throw new IllegalArgumentException();
		StringBuilder sb = new StringBuilder(source);
		int index= 0;
		while((index= sb.indexOf(what, index)) != -1) {
			sb.replace(index, index+what.length(), with);
			index+= with.length();
		}
		return new String(sb);
	}

	/**
	 * Sostituisce all' interno di source tutte le occorrenze di what con with.
	 * La sostituzione' e' Key-Sensitive.
	 * @param source La stringa su cui operare
	 * @param what La stringa che deve essere sostituita
	 * @param with La stringa che sostituira' what
	 * @return La stringa ottenuta dalla sostituzione
	 * @throws IllegalArgumentException Se qualunque parametro e' null e se what e' stringa vuota
	 * @deprecated Usare replaceAll
	 */
	public static String replace(String source, String what, String with) {
		return StringUtils.replaceAll(source, what, with);
	}

	/**
	 * Rimuove da source tutte le occorrenze di what. La rimozione' e' Key-Sensitive
	 * @param source La stringa su cui operare
	 * @param what La stringa che deve essere rimossa
	 * @return La stringa ottenuta dalla rimozione
	 * @throws IllegalArgumentException Se qualunque parametro e' null e se what e' stringa vuota
	 */
	public static String removeAll(String source, String what) {
		return StringUtils.replaceAll(source, what, "");
	}

	/**
	 * Rimuove da source tutte le occorrenze di what. La rimozione' e' Key-Sensitive
	 * @param source La stringa su cui operare
	 * @param what La stringa che deve essere rimossa
	 * @return La stringa ottenuta dalla rimozione
	 * @throws IllegalArgumentException Se qualunque parametro e' null e se what e' stringa vuota
	 * @deprecated Usare removeAll
	 */
	public static String remove(String source, String what) {
		return StringUtils.removeAll(source, what);
	}

}
