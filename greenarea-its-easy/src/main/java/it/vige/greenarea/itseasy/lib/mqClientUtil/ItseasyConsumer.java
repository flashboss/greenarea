/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.mqClientUtil;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;

/**
 * 
 * @author Administrator
 */
public class ItseasyConsumer implements MqConstants {

	private Logger logger = getLogger(getClass());

	public Serializable readMessage(Destination destination,
			JMSConsumer jmsConsumer) {
		return readMessage(destination, jmsConsumer, null,
				MAX_READ_WAIT_TIMEOUT);
	}

	public Serializable readMessage(Destination destination,
			JMSConsumer jmsConsumer, long timeout) {
		return readMessage(destination, jmsConsumer, null, timeout);
	}

	public Serializable readMessage(Destination destination,
			JMSConsumer jmsConsumer, String selector) {
		return readMessage(destination, jmsConsumer, selector,
				MAX_READ_WAIT_TIMEOUT);
	}

	public String readTextMessage(String cfName, Destination destination,
			JMSConsumer jmsConsumer) {
		return readTextMessage(cfName, destination, jmsConsumer, null,
				MAX_READ_WAIT_TIMEOUT);
	}

	public String readTextMessage(String cfName, Destination destination,
			JMSConsumer jmsConsumer, long timeout) {
		return readTextMessage(cfName, destination, jmsConsumer, null, timeout);
	}

	public String readTextMessage(String cfName, Destination destination,
			JMSConsumer jmsConsumer, String selector) {
		return readTextMessage(cfName, destination, jmsConsumer, selector,
				MAX_READ_WAIT_TIMEOUT);
	}

	public Serializable readMessage(Destination destination,
			JMSConsumer jmsConsumer, String selector, long waitTimeout) {

		Message msg = null;

		msg = jmsConsumer.receive(waitTimeout);
		if ((msg != null) && (msg instanceof ObjectMessage)) {
			try {
				return ((ObjectMessage) msg).getObject();
			} catch (JMSException ex) {
				logger.error("its esy", ex);
				return null;
			}
		} else {
			return null;
		}
	}

	public String readTextMessage(String cfName, Destination destination,
			JMSConsumer jmsConsumer, String selector, long waitTimeout) {

		Message msg = null;
		msg = jmsConsumer.receive(waitTimeout);
		if (msg != null) {
			return MqUtility.jmsMsgBodyAsString(msg);
		} else {
			return null;
		}
	}
}
