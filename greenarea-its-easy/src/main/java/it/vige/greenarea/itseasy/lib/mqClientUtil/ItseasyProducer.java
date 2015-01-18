/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.mqClientUtil;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSProducer;

import org.slf4j.Logger;

/**
 * 
 * @author Administrator
 */
public class ItseasyProducer implements MqConstants {

	private Logger logger = getLogger(getClass());

	public boolean publishTextMessage(Destination destination,
			JMSProducer jmsProducer, String message,
			Map<String, String> properties) {
		if (properties != null) {
			for (Map.Entry<String, String> e : properties.entrySet()) {
				jmsProducer.setProperty(e.getKey(), e.getValue());
			}
		}
		// Publish the message
		logger.debug("Publishing a message to Destination: " + destination);
		logger.debug("        >>> " + message + " <<<");
		for (String k : properties.keySet()) {
			logger.debug("- " + k + ":\t" + properties.get(k));
		}
		jmsProducer.send(destination, message);
		return true;
	}

	public boolean publishObjectMessage(Destination destination,
			JMSProducer jmsProducer, Serializable srlzObj,
			Map<String, String> properties) {

		logger.debug("");

		if (properties != null) {
			for (Map.Entry<String, String> e : properties.entrySet()) {
				jmsProducer.setProperty(e.getKey(), e.getValue());
			}
		}
		// Publish the message
		logger.debug("Publishing a object message to Destination: "
				+ destination);
		jmsProducer.send(destination, srlzObj);
		return true;
	}
}
