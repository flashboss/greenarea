package it.vige.greenarea.gtg.utilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import static org.slf4j.LoggerFactory.getLogger;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;

/**
 * 
 * @author UE012442
 */
public class XML2POJO {

	private static Logger logger = getLogger(XML2POJO.class);

	/**
	 * 
	 * Metodo che controlla le credenziali dell'utente e ritorna il suo ruolo
	 */
	public static void store(Collection<Object> pojos, String filename)
			throws FileNotFoundException, IOException {
		// Create file output stream.
		XMLEncoder ostream = null;
		File file = new File(filename);
		file.createNewFile();
		FileOutputStream fstream = new FileOutputStream(file);
		ostream = new XMLEncoder(fstream);
		for (Object q : pojos) {
			ostream.writeObject(q);
		}
		ostream.flush();
		ostream.close();
		fstream.close();
	}

	/* ****************************************************************************
	 * 
	 * ATTENZIONE: Nel caso si verifichi in fase di "load" l'exception:
	 * org.xml.sax.SAXParseException: Content is not allowed in prolog. Si
	 * faccia attenzione al formato del file: nel mio caso stavo cercando di far
	 * caricare un file con 16bit per carattere (me ne sono accorto con un HEX
	 * EDITOR) e' bastato copiare un file con 8bit per carattere, aprirlo con
	 * notepad++, cancellarne il contenuto, ed incollarci dentro il contenuto
	 * dell'altro file.
	 * *********************************************************
	 * ******************
	 */

	public static Collection<Object> load(String filename)
			throws FileNotFoundException, IOException {
		List<Object> pojos = null;
		File file = new File(filename);
		FileInputStream fstream = new FileInputStream(file);
		XMLDecoder istream = new XMLDecoder(fstream);
		pojos = new ArrayList<Object>();
		Object obj;
		try {
			for (;;) {
				obj = istream.readObject();
				pojos.add(obj);
			}
		} catch (Exception ex) {
			logger.error("gtg service", ex);
		} finally {
			istream.close();
			fstream.close();
		}
		return pojos;
	}
}
