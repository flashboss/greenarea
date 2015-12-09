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

import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_SHIPPING_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TRANSPORT_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_VECTOR_NAME;
import static org.slf4j.LoggerFactory.getLogger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;

import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.itseasy.lib.mqClientUtil.MqUtility;
import it.vige.greenarea.itseasy.lib.mqData.MqShippingData;

@MessageDriven(mappedName = "java:/jms/topic/VectorTopic", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/topic/VectorTopic"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") })
public class GTGMessageListener implements MessageListener {

	private Logger logger = getLogger(getClass());

	@Inject
	private TransportFacade transportFacade;

	@Override
	public void onMessage(Message message) {

		// verifico che il messaggio sia di tipo object:

		try {
			message.getStringProperty(MQ_KEY_TRANSPORT_ID);
		} catch (JMSException ex) {
			logger.error("jms", ex);
			return; // devo segnalare che manca il trID (ma a me serve????)
		}
		try {
			message.getStringProperty(MQ_KEY_SHIPPING_ID);
		} catch (JMSException ex) {
			logger.error("jms", ex);
			return; // devo segnalare che manca lo shID
		}
		try {
			message.getStringProperty(MQ_KEY_VECTOR_NAME);
		} catch (JMSException ex) {
			logger.error("jms", ex);
			return;
		}
		if (!(message instanceof ObjectMessage)) {
			// segnalo problema perche' il messaggio non contiene un oggetto
			logger.debug("Messaggio di tipo errato: " + MqUtility.messageType(message));
			return;
		}
		ObjectMessage objmsg = (ObjectMessage) message;
		MqShippingData sod;
		try {
			sod = (MqShippingData) objmsg.getObject();
		} catch (JMSException ex) {
			logger.error("jms", ex);
			logger.error("Casting messaggio errato: ", ex);
			return;
		}
		// devo creare le entity
		transportFacade.loadOrder(sod);

	}
}
