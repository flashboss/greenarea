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
package it.vige.greenarea.gtg.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.ConnectionFactory;
import javax.jms.Topic;

import it.vige.greenarea.gtg.mqHandler.GTGmessageHandler;
import it.vige.greenarea.itseasy.lib.configurationData.MqConstants;
import it.vige.greenarea.itseasy.lib.mqClientUtil.ItseasyConsumer;

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
		ItseasyConsumer consumer = GTGmessageHandler.getGTGmessageHandler().getConsumer(source);
		GTGmessageHandler.getGTGmessageHandler().putConsumer(source, consumer);
	}
}
