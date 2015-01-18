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
package it.vige.greenarea.itseasy.lib.logger;

import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyProducer;

import java.util.Date;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSProducer;

public class ItseasyLogger implements MqConstants {
	private ItseasyProducer producer = new ItseasyProducer();

	public void logMessage(String source, String eventType,
			Map<String, String> properties, String message,
			Destination destination, JMSProducer jmsProducer) {

		Date now = new Date();

		// aggiungo alcune proprieta' del Logger
		// Timestamp
		properties.put(MQ_KEY_TIMESTAMP, now.toString());

		properties.put("LOG_SOURCE", source);

		properties.put("LOG_EVENT", eventType);

		// Invio il messaggio alla destinazione Itseasy per il Log
		producer.publishTextMessage(destination, jmsProducer, message,
				properties);
	}

}
