/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.mqHandler;

import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author 00917377
 */
public class GTGmessageHandler {

	private static GTGmessageHandler gTGmessageHandler;
	private static Map<String, ItseasyConsumer> consumerMap;

	/**
	 * A private Constructor prevents any other class from instantiating.
	 */
	private GTGmessageHandler() {
		// Optional Code
		consumerMap = new HashMap<String, ItseasyConsumer>();
	}

	public static synchronized GTGmessageHandler getGTGmessageHandler() {
		if (gTGmessageHandler == null) {
			gTGmessageHandler = new GTGmessageHandler();

		}
		return gTGmessageHandler;
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
