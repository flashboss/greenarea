/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.ejb;

import it.vige.greenarea.gtg.mqHandler.GTGmessageHandler;
import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyConsumer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;

/**
 * 
 * @author 00917377
 */
@Startup
@Singleton
public class StartBean {
	@Resource(mappedName = "java:/jms/topic/VectorTopic")
	private Topic vectorTopic;

	@Resource
	private ConnectionFactory connectionFactory;

	// Add business logic below. (Right-click in editor and choose
	// "Insert Code > Add Business Method")
	@PostConstruct
	void startListner() {
		String source = MqConstants.gat_to_vector;
		ItseasyConsumer consumer = GTGmessageHandler.getGTGmessageHandler()
				.getConsumer(source);
		GTGmessageHandler.getGTGmessageHandler().putConsumer(source, consumer);
	}
}
