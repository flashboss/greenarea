/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.messages;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.VikorResult;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

/**
 * 
 * @author MacRed
 */
@MessageDriven(mappedName = "java:/jms/queue/mesVikorResult", activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/mesVikorResult"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class MessageVikorResult implements MessageListener {

	private Logger logger = getLogger(getClass());

	public MessageVikorResult() {
	}

	@Resource
	private MessageDrivenContext messageDrivenContext;
	@PersistenceContext
	private EntityManager em;

	@Override
	public void onMessage(Message message) {
		try {
			ObjectMessage objmess = (ObjectMessage) message;
			VikorResult miss = (VikorResult) objmess.getObject();
			save(miss);
		} catch (JMSException ex) {
			logger.error("Rollback", ex);
			messageDrivenContext.setRollbackOnly();
		}
	}

	public void save(Object obj) {
		em.persist(obj);
	}
}
