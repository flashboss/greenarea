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
				InputStream propsStream = Tracer.class.getClassLoader().getResourceAsStream(TNTPROPSFILE);
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
