/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.utilities;

/**
 *
 * @author 00917308
 */
import static org.slf4j.LoggerFactory.getLogger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.slf4j.Logger;

public class NetworkResourceTest {

	private static Logger logger = getLogger(NetworkResourceTest.class);

	// private static Logger log = Logger.getLogger(NetworkResourceTest.class);
	/*
	 * COSTANTI PER LA SELEZIONE DEL TEST DA ESEGUIRE
	 */

	public enum NetworkTestType {

		DNSTEST, ICMPTEST, TCPTEST, HTTPTEST
	};

	static int timeout = 3000; // millis

	/**
	 * @param args
	 *            the command line arguments
	 */
	// ==========================================================================
	//
	//
	public static boolean test(NetworkTestType testType, String testName,
			String resource) {
		return test(testType, testName, resource, null);
	}

	public static boolean test(NetworkTestType testType, String resource) {
		return test(testType, resource, resource, null);
	}

	public static boolean test(NetworkTestType testType, String testName,
			String resource, Integer tcpPort) {
		boolean passed = true;
		InetAddress address = null;

		switch (testType) {
		case DNSTEST:
			address = dnsTest(resource);
			if (address == null) {
				passed = false;
			}
			break;
		case ICMPTEST:
			break;
		case TCPTEST:
			if (!tcpTest(resource, tcpPort)) {
				passed = false;
			}
			break;
		case HTTPTEST:
			if (!httpTest("http://" + resource)) {
				passed = false;
			}
			break;
		}
		return passed;
	}

	static InetAddress dnsTest(String resource) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(resource);
		} catch (UnknownHostException ex) {
			logger.error("dns test", ex);
		}
		return address;
	}

	static boolean icmpTest(InetAddress address) {
		boolean passed;
		try {
			passed = address.isReachable(timeout);
		} catch (java.io.IOException e) {
			passed = false;
		}
		return passed;
	}

	static boolean httpTest(String resource) {
		java.net.Proxy defaultProxy;
		InetSocketAddress proxySocket;
		String proxySet = System.getProperty("proxySet");
		if (proxySet != null && proxySet.equals("true")) {
			String proxyHost = System.getProperty("http.proxyHost");
			String proxyPort = System.getProperty("http.proxyPort");
			proxySocket = new InetSocketAddress(proxyHost,
					Integer.parseInt(proxyPort));
			defaultProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP,
					proxySocket);
		} else {
			defaultProxy = null;
		}
		return httpTest(resource, defaultProxy);
	}

	static boolean httpTest(String resource, java.net.Proxy httpProxy) {
		boolean passed;
		URLConnection conn;
		try {
			if (httpProxy == null) {
				conn = (new URL(resource)).openConnection();
			} else {
				conn = (new URL(resource)).openConnection(httpProxy);
			}
			conn.connect();
			passed = true;
		} catch (Exception ex) {
			passed = false;
		}
		return passed;
	}

	static boolean tcpTest(String resource, Integer tcpPort) {
		boolean passed;
		try {
			new Socket(resource, tcpPort);
			passed = true;
		} catch (Exception ex) {
			passed = false;
		}
		return passed;
	}

	static void printSysPar(String sysPar) {
		logger.debug(sysPar + "=\"" + System.getProperty(sysPar) + "\"");
	}

	public static String dnsTranslated(String resource) {
		InetAddress a = dnsTest(resource);
		return (a == null ? null : a.getHostAddress());
	}
}
