/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.mqClientUtil;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Session;

import org.slf4j.Logger;

/**
 * 
 * @author Administrator
 */
public class MQconnectorManager {

	private Logger logger = getLogger(getClass());

	private MQconnectorManager mqConnectorManager;
	// private static Map<String, Connection> connectorTable;
	private Map<String, Session> sessionTable;

	/**
	 * A private Constructor prevents any other class from instantiating.
	 */
	public MQconnectorManager() {
		// Optional Code
		sessionTable = new HashMap<String, Session>();
	}

	public MQconnectorManager getMQconnectorManager() {
		if (mqConnectorManager == null) {
			mqConnectorManager = new MQconnectorManager();

		}
		return mqConnectorManager;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public Session getSession(ItseasyStoreInfo store, Session s, String cfName) {
		String connectionKey = store.getObjStoreType() + "|" + store.getUrl()
				+ "|" + cfName;
		// verifico se esiste gia' una connessione attiva per questa
		// destinazione:
		if (sessionTable.containsKey(connectionKey)) {
			return sessionTable.get(connectionKey);
		}

		// se non c'e' devo creare una connection, inserirla nella table e poi
		// restituirla:
		// ConnectionFactory cf;
		try {
			// Lookup my connection factory from the admin object store.
			// The name used here here must match the lookup name
			// used when the admin object was stored.
			logger.debug("Looking up Connection Factory object with lookup name: "
					+ cfName);
			logger.debug("Connection Factory object found.");
		} catch (Exception e) {
			logger.error("Failed to lookup Connection Factory object.");
			logger.error("Please make sure you have created the Connection Factory object using the command:");
			logger.error("\timqobjmgr -i add_cf.props");
			return null;
		}

		logger.debug("");

		// try {
		logger.debug("Creating connection to broker.");
		// Connection connection = cf.createConnection();
		logger.debug("Connection to broker created.");
		// connection.start();
		/*
		 * Session session = connection.createSession(false,
		 * Session.AUTO_ACKNOWLEDGE);
		 */
		// inserisco la session nella tabella:
		sessionTable.put(connectionKey, s);
		return s;
	}

	public void release(ItseasyStoreInfo store, String cfName) {
		String connectionKey = store.getObjStoreType() + "|" + store.getUrl()
				+ "|" + cfName;
		// verifico se esiste gia' una connessione attiva per questa
		// destinazione:
		if (!sessionTable.containsKey(connectionKey)) {
			logger.error("Failed to close Session: " + connectionKey);
			return;
		}

		Session s = sessionTable.get(connectionKey);
		try {
			s.close();
			sessionTable.remove(connectionKey);

		} catch (JMSException e) {
			logger.error("Failed to close connection.");

		}
	}
}
