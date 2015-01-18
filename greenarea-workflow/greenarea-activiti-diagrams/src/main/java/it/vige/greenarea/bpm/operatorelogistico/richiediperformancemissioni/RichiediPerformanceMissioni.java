package it.vige.greenarea.bpm.operatorelogistico.richiediperformancemissioni;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RichiediPerformanceMissioni extends
		EmptyRichiediPerformanceMissioni {
	private Logger logger = getLogger(getClass());

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("CDI Richiedi Performance Missioni");
		try {
			Date dal = (Date) execution.getVariable("dal");
			Date al = (Date) execution.getVariable("al");

			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
					APPLICATION_JSON);
			RichiestaMissioni richiesta = new RichiestaMissioni();
			richiesta.setDataFine(al);
			List<Missione> missioni = bldr.post(
					entity(richiesta, APPLICATION_JSON),
					new GenericType<List<Missione>>() {
					});
			Map<String, List<Missione>> elencoMissioni = new HashMap<String, List<Missione>>();
			Map<String, Double> mappaCrediti = new HashMap<String, Double>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			if (missioni != null)
				for (Missione missione : missioni) {
					String dataInizio = dateFormat.format(missione
							.getDataInizio());
					String di = df.format(missione.getDataInizio());
					if (elencoMissioni.get(di) == null)
						elencoMissioni.put(di, new ArrayList<Missione>());
					elencoMissioni.get(di).add(missione);
					if (missione.getDataInizio().compareTo(dal) >= 0) {
						double credito = missione.getCreditoMobilita();
						double creditoIniziale = mappaCrediti.get(dataInizio) == null ? 0.0
								: mappaCrediti.get(dataInizio);
						mappaCrediti.put(dataInizio, creditoIniziale + credito);
					}
				}
			execution.setVariable("dal", df.format(dal));
			execution.setVariable("al", df.format(al));

			// TODO Mock data da sostituire con i valori della TAP
			Map<String, Number> reportData = new LinkedHashMap<String, Number>();

			Calendar ss = Calendar.getInstance();
			Calendar ee = Calendar.getInstance();

			ss.setTime(dal);
			ee.setTime(al);
			ee.add(Calendar.DATE, 1);// just incrementing end date by 1

			String day = "";

			while (!ss.equals(ee)) {
				day = df.format(ss.getTime());
				String data = dateFormat.format(ss.getTime());
				Double value = mappaCrediti.get(data);
				if (value != null)
					reportData.put(day, value);

				ss.add(Calendar.DATE, 1);
			}
			execution.setVariable("reportData", reportData);
			execution.setVariable("elencoMissioni", elencoMissioni);

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorerichiestaperformancemissioni");
		}
	}
}
