package it.vige.greenarea.bpm.pa.simulazionemissioni;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.addDays;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class Simulazione extends EmptySimulazione {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Simulazione");
		Client client = null;
		List<Request> requests = new ArrayList<Request>();
		client = newClient();
		Date dataInizio = addDays(new Date(), 1);
		Date dataFine = (Date) execution.getVariable("al");
		int days = (int) ((dataFine.getTime() - dataInizio.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		Date date = dataInizio;
		FasciaOraria fasciaOraria = (FasciaOraria) execution
				.getVariable("policydetail");
		if (fasciaOraria != null) {
			for (int i = 0; i < days + 1; i++) {
				Builder bldr = client.target(
						BASE_URI_TS + "/simulRank/" + fasciaOraria.getId()
								+ "/" + date).request(APPLICATION_JSON);
				requests.addAll(bldr.get(new GenericType<List<Request>>() {
				}));
				date = addDays(date, 1);
			}
			@SuppressWarnings("unchecked")
			List<Missione> simulazioni = (List<Missione>) execution
					.getVariable("simulazioni");
			@SuppressWarnings("unchecked")
			List<Missione> missioni = (List<Missione>) execution
					.getVariable("missioni");
			for (Request request : requests) {
				for (Missione missione : missioni) {
					String nome = missione.getNome();
					if (nome != null
							&& request.getIdMission() == new Integer(nome)) {
						missione.setRanking(request.getColor());
						missione.setCreditoMobilita(request.getPrice());
						simulazioni.add(missione);
					}
				}
			}
			execution.setVariable("simulazioni", simulazioni);
		}
	}

}
