/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.utilities;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;

/**
 * 
 * @author 00917308
 */
public class Application {
	public static final String ITSEASYCONFIG = "ITSEASYCONFIG";

	private static Logger logger = getLogger(Application.class);

	private static Properties applicationProperties = null;
	private static String path = null;

	static {
		try {
			InputStream in = null;
			URL url = null;
			// provo con la variabile di sistema
			path = System.getenv(ITSEASYCONFIG);
			if (path == null || path.length() == 0) { // se non c'e' variabile
														// di sistema, cerco col
														// classloader nel
														// package nullo
				logger.info("Variabile di configurazione di sistema ITSEASYCONFIG non definita");
				try {
					url = Application.class.getClassLoader().getResource(
							"Application.properties");
				} catch (Exception ex) {
					logger.error("File Application.properties inesistente in "
							+ url.getPath(), ex);
				}

				if (url != null && !url.getPath().contains("!")) { // se l'ho
																	// trovato
																	// ricavo il
																	// path
					path = url.getPath();
					logger.info("File Application.properties in " + path);
				} else { // altrimenti provo a cercarlo nella user.dir
					path = System.getProperty("user.dir")
							+ "\\Application.properties";
				}
			}
			try {
				logger.info("Application.properties Path =\"" + path + "\"");
				if (url != null)
					in = url.openConnection().getInputStream();
				else
					in = new FileInputStream(path);
				applicationProperties = new Properties();
				applicationProperties.load(in);
				in.close();
			} catch (Exception ex) {
				logger.error(
						" IMPOSSIBILE IDENTIFICARE UN FILE DI CONFIGURAZIONE ITSEASY",
						ex);
			} finally {
				try {
					in.close();
				} catch (IOException ex) {
					logger.error("errore sgr common", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("errore sgr common", ex);
		}
	}

	public static String getProperty(String key) {
		return applicationProperties.getProperty(key);
	}

	public static Object setProperty(String key, String value) {
		return applicationProperties.setProperty(key, value);
	}

	public static void saveProperties() {
		try {
			FileOutputStream out = new FileOutputStream(path);
			applicationProperties.store(out, "");
			out.close();
		} catch (IOException ex) {
			logger.error("errore sgr common", ex);
		}
	}
}
