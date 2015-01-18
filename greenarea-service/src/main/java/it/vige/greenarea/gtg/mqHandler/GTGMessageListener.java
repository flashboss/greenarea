/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.mqHandler;

import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_SHIPPING_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TIMESTAMP;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_TRANSPORT_ID;
import static it.vige.greenarea.itseasy.lib.configurationData.MqConstants.MQ_KEY_VECTOR_NAME;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.itseasy.lib.mqClientUtil.MqUtility;
import it.vige.greenarea.itseasy.lib.mqData.MqShippingData;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;

/**
 * 
 * @author Administrator
 */
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
			logger.debug("Messaggio di tipo errato: "
					+ MqUtility.messageType(message));
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
