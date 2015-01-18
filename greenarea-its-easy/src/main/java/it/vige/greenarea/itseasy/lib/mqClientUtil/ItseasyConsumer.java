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
