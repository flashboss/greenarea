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
