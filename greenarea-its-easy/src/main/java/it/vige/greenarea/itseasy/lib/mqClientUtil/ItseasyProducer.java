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
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSProducer;

import org.slf4j.Logger;

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
