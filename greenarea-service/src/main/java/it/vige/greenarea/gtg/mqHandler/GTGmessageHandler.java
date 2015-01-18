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
package it.vige.greenarea.gtg.mqHandler;

import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyConsumer;

import java.util.HashMap;
import java.util.Map;

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
