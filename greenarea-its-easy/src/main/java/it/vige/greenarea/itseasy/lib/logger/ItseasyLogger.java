/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.logger;

import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyProducer;

import java.util.Date;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSProducer;

/**
 * 
 * @author 00917377
 */
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
