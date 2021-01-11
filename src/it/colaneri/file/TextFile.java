// Static functions for reading and writing text files as
// a single string, and treating a file as an ArrayList.
package it.colaneri.file;

import java.io.*;
import java.util.*;

public class TextFile extends ArrayList<String> {
	
	private static final long serialVersionUID = -3079776673642046255L;

    /**
     * Legge il file specificato come singola stringa
     * @param fileName il file da leggere
     * @return una stringa col contenuto
     */
	public static String read(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(fileName).getAbsoluteFile()));
			try {
				String s;
				while ((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	/**
	 * Scrive un file con una sola istruzione
	 * @param fileName il nome del file
	 * @param text il testo del file
	 */
	public static void write(String fileName, String text) {
		try {
			PrintWriter out = new PrintWriter(new File(fileName).getAbsoluteFile());
			try {
				out.print(text);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Legge un file tramutandolo in una rappresentazione a "righe" divise dallo "splitter" specificato
	 * @param fileName il nome del file
	 * @param splitter il divisore delle righe
	 */
	public TextFile(String fileName, String splitter) {
		super(Arrays.asList(read(fileName).split(splitter)));
		// Regular expression split() often leaves an empty
		// String at the first position:
		if (get(0).equals(""))
			remove(0);
	}

	/**
	 * Legge un file tramutandolo in una rappresentazione a "righe" divise dallo "splitter" standard ovvero a capo
	 * @param fileName il nome del file
	 */
	public TextFile(String fileName) {
		this(fileName, "\n");
	}

	/**
	 * Persiste il file rappresentato da questa istanza
	 * @param fileName il nome del file
	 */
	public void write(String fileName) {
		try {
			PrintWriter out = new PrintWriter(new File(fileName).getAbsoluteFile());
			try {
				for (String item : this)
					out.println(item);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Simple test:
	public static void main(String[] args) {
		String file = read("utils.iml");
		write("test.txt", file);
		TextFile text = new TextFile("test.txt");
		text.write("test2.txt");
		// Break into unique sorted list of words:
		TreeSet<String> words = new TreeSet<String>(new TextFile("utils.iml", "\\W+"));
		// Display the capitalized words:
		System.out.println(words.headSet("a"));
	}
} /*
	 * Output: [0, ArrayList, Arrays, Break, BufferedReader, BufferedWriter, Clean,
	 * Display, File, FileReader, FileWriter, IOException, Normally, Output,
	 * PrintWriter, Read, Regular, RuntimeException, Simple, Static, String,
	 * StringBuilder, System, TextFile, Tools, TreeSet, W, Write]
	 */// :~