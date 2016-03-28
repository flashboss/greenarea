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
package it.vige.greenarea.sgaplconsole.messages;

import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import it.vige.greenarea.itseasy.lib.mqClientUtil.MqUtility;
import it.vige.greenarea.sgaplconsole.controllers.MonitorController;
import it.vige.greenarea.sgaplconsole.data.MonitorMsg;

// Add business logic below. (Right-click in editor and choose
// "Insert Code > Add Business Method")
@MessageDriven(mappedName = "java:/jms/topic/GatTopic", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/topic/GatTopic"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") })
public class SgotListener implements MessageListener {

	@Inject
	private MonitorController monitorController;

	@Override
	public void onMessage(Message message) {
		String s = "Errore";
		String p = "???";
		String m = "???";
		if (message != null) {

			s = MqUtility.getDestination(message);
			StringBuilder sb = new StringBuilder();
			Map<String, String> prop;
			try {
				prop = MqUtility.getMessageProperties(message);

				for (String k : prop.keySet()) {

					sb.append(k).append(" -> ").append(prop.get(k)).append(",  ");
				}
				p = sb.toString();
			} catch (JMSException ex) {
				p = "";
			}
			// m = MqUtility.jmsMsgBodyAsString(message);
			if (message instanceof TextMessage) {
				try {
					m = ((TextMessage) message).getText();
				} catch (JMSException ex) {
					m = ex.toString();
				}

			} else if (message instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage) message;
				Object obj = null;
				try {
					obj = msg.getObject();

					if (obj != null) {
						m = obj.getClass().getName();
					}

				} catch (Exception ex) {
					m = ex.toString();
				}

			} else {
				p = "";
				m = "Messaggio = null";
			}
			monitorController.getSbMonitor().add(new MonitorMsg(s, p, m));
		}
	}
}
