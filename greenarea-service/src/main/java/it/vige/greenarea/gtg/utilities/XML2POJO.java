/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.gtg.utilities;

/*
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

public class XML2POJO {

	private static Logger logger = getLogger(XML2POJO.class);

	/**
	 * 
	 * Metodo che controlla le credenziali dell'utente e ritorna il suo ruolo
	 */
	public static void store(Collection<Object> pojos, String filename) throws FileNotFoundException, IOException {
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

	/*
	 * *************************************************************************
	 * ***
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

	public static Collection<Object> load(String filename) throws FileNotFoundException, IOException {
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
