/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.geo;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.slf4j.Logger;

/**
 * 
 * @author 00917308
 */
public class CAP {

	private static Logger logger = getLogger(CAP.class);

	private static InputStream iStream = null;
	private static HashMap<String, CapRecord> caps = null;

	// CAP scaricati da www.geonet.it/CAP-geografici.html

	public static String getProvincia(String cap) {
		CapRecord result;
		if (caps == null)
			_loadCaps();
		if (cap.length() != 5)
			return null;
		if ((result = caps.get(cap)) != null)
			return result.Provincia;
		// if((result = caps.get(cap.subSequence(0, 4)+"x"))!=null) return
		// result.Provincia;
		// if((result = caps.get(cap.subSequence(0, 3)+"xx"))!=null) return
		// result.Provincia;
		return (null);
	}

	private static void _loadCaps() {
		String line;
		caps = new HashMap<String, CapRecord>();
		iStream = CAP.class.getClassLoader().getResourceAsStream("cap.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens;
				tokens = line.split(";");
				CapRecord cr = new CapRecord();
				// cr.Abitanti = Integer.parseInt(tokens[7]);
				cr.CAP = tokens[1];
				// cr.CodFisco= tokens[6];
				cr.Comune = tokens[0];
				// cr.Istat= tokens[0];
				// cr.Link= tokens[8];
				// cr.Prefisso= tokens[4];
				cr.Provincia = tokens[2];
				// cr.Regione= tokens[3];
				cr.Sigla = tokens[3];
				caps.put(cr.CAP, cr);
			}
		} catch (IOException ex) {
			logger.error("errore sgr common", ex);
		}

	}

}
