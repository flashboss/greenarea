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
package it.vige.greenarea.bpm.tempo.autorizzamissioni;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.dto.Missione;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class ElaborazioneRanking extends EmptyElaborazioneRanking {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Elaborazione Ranking");
		Client client = newClient();
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) execution
				.getVariable("missioni");
		List<Request> allRequests = new ArrayList<Request>();
		for (Missione missione : missioni) {
			Builder bldr = client.target(
					BASE_URI_TS + "/simul/" + missione.getDataInizio() + "/"
							+ missione.getFasciaOraria().getId()).request(
					APPLICATION_JSON);
			List<Request> response = bldr.get(new GenericType<List<Request>>() {
			});
			allRequests.addAll(response);
		}
	}

}
