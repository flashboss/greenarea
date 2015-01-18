package it.vige.greenarea.gtg.utilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;

/**
 * 
 * @author 00917308
 */
public class Tracer {

	private static HashMap<String, Tracer> tracerMap;
	private static String tracerDir = null;
	private static final String TNTDIR = "TrackNtrace.directory";
	private static final String TNTPROPSFILE = "tnt.properties";
	public static final String FIELDSEPARATOR = "#";
	public static final String OPENITEM = "[";
	public static final String CLOSEITEM = "]";
	public static String lineSeparator = null;
	private FileOutputStream tracerFstream = null;
	private File tracerFile = null;
	private String tracerFileName = null;

	private Tracer(String tracerName) {
		StringBuilder sb = new StringBuilder(256);
		if (tracerDir == null) {
			try {
				InputStream propsStream = Tracer.class.getClassLoader()
						.getResourceAsStream(TNTPROPSFILE);
				Properties tntProps = new Properties();
				tntProps.load(propsStream);
				tracerDir = tntProps.getProperty(TNTDIR);
				if (tracerDir == null) {
					tracerDir = "/";
				}
			} catch (Exception ex) {
				tracerDir = "/";
			}
		}
		sb.append(tracerDir);
		if (!tracerDir.endsWith("/"))
			sb.append("/");
		sb.append(tracerName);
		timeStamp(sb);
		sb.append(".txt");
		tracerFileName = sb.toString();

		tracerFile = new File(tracerFileName);
		try {
			tracerFile.createNewFile();
			tracerFstream = new FileOutputStream(tracerFile);
		} catch (Exception ex) {
		}
	}

	public static synchronized Tracer getInstance(String tracerName) {
		if (tracerName == null || tracerName.isEmpty()) {
			return null;
		}
		Tracer t = null;
		if (lineSeparator == null) {
			lineSeparator = System.getProperty("line.separator");
			if (lineSeparator == null)
				lineSeparator = "\r\n";
		}
		if (tracerMap == null) {
			tracerMap = new HashMap<String, Tracer>();
			t = new Tracer(tracerName);
			tracerMap.put(tracerName, t);
		} else {
			t = tracerMap.get(tracerName);
			if (t == null) {
				t = new Tracer(tracerName);
				tracerMap.put(tracerName, t);
			}
		}
		return t;
	}

	public void trace(String... strings) {
		if (tracerFstream != null) {
			StringBuilder sb = new StringBuilder(256);
			timeStamp(sb);
			for (String s : strings) {
				sb.append(FIELDSEPARATOR);
				sb.append(s);
			}
			sb.append(lineSeparator);
			try {
				tracerFstream.write(sb.toString().getBytes());
			} catch (Exception ex) {
			}
		}
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

	private static void timeStamp(StringBuilder sb) {
		Calendar cal = new GregorianCalendar();
		sb.append(sdf.format(cal.getTime()));
	}
}
