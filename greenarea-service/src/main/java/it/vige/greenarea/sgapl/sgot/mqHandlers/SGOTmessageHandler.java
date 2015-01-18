/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.mqHandlers;

import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Administrator
 */
public class SGOTmessageHandler {

	private static SGOTmessageHandler sGOmessagHandler;
	private static Map<String, ItseasyConsumer> consumerMap;

	/**
	 * A private Constructor prevents any other class from instantiating.
	 */
	private SGOTmessageHandler() {
		// Optional Code
		consumerMap = new HashMap<String, ItseasyConsumer>();
	}

	public static synchronized SGOTmessageHandler getSGOTmessagHandler() {
		if (sGOmessagHandler == null) {
			sGOmessagHandler = new SGOTmessageHandler();

		}
		return sGOmessagHandler;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public ItseasyConsumer getConsumer(String l) {

		// verifico se esiste gia' una connessione attiva per questa
		// destinazione:
		if (consumerMap.containsKey(l)) {
			return consumerMap.get(l);
		}
		ItseasyConsumer c = new ItseasyConsumer();
		consumerMap.put(l, c);
		return c;
	}

	public void releaseConsumer(String l) {

		// recupero la connessione attiva per questa destinazione:
		if (consumerMap.containsKey(l)) {
			consumerMap.remove(l);
		}
	}

	public void putConsumer(String l, ItseasyConsumer c) {
		consumerMap.put(l, c);
	}
}
