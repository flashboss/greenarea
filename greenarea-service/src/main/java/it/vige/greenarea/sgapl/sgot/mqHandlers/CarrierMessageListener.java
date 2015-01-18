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
package it.vige.greenarea.sgapl.sgot.mqHandlers;

import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_CAUSE;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TRANSPORT_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants.DONE_STATUS;
import static it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants.ON_DELIVERY_STATUS;
import static it.vige.greenarea.itseasy.lib.configurationData.SGAPLconstants.REJECT_STATUS;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgapl.sgot.business.CarrierManagerBean;
import it.vige.greenarea.sgapl.sgot.business.exception.GATException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;

@MessageDriven(mappedName = "java:/jms/topic/GatTopic", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/topic/GatTopic"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") })
public class CarrierMessageListener implements MessageListener {

	private Logger logger = getLogger(getClass());

	@Inject
	private CarrierManagerBean carrierBean;

	@Override
	public void onMessage(Message message) {

		String trID;
		try {
			trID = message.getStringProperty(MQ_KEY_TRANSPORT_ID);
		} catch (JMSException ex) {
			logger.error("errore sgot", ex);
			return;
		}
		String cause;
		try {
			cause = message.getStringProperty(MQ_KEY_CAUSE);
		} catch (JMSException ex) {
			logger.error("errore sgot", ex);
			return;
		}
		try {
			if (cause.equals(ON_DELIVERY_STATUS)) {
				carrierBean.onDelivery(trID);
			} else if (cause.equals(DONE_STATUS)) {
				carrierBean.doneDelivery(trID);
			} else if (cause.equals(REJECT_STATUS)) {
				carrierBean.rejectDelivery(trID);
			}
		} catch (GATException ex) {
			logger.error("errore sgot", ex);
		}
	}

}
