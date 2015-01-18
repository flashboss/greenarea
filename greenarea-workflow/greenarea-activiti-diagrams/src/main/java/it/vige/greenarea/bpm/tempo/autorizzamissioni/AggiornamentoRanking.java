package it.vige.greenarea.bpm.tempo.autorizzamissioni;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class AggiornamentoRanking extends EmptyAggiornamentoRanking {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Aggiornamento Ranking");
		Client client = newClient();
		@SuppressWarnings("unchecked")
		List<Request> allRequests = (List<Request>) execution
				.getVariable("richieste");
		for (Request request : allRequests) {
			Builder bldr = client.target(
					BASE_URI_TS + "/updateVikor/" + request.getDateMiss() + "/"
							+ request.getIdTimeSlot())
					.request(APPLICATION_JSON);
			bldr.get(new GenericType<List<Request>>() {
			});
		}
	}

}
