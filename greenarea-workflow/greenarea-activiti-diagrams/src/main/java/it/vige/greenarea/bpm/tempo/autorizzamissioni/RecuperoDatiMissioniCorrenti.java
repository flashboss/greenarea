package it.vige.greenarea.bpm.tempo.autorizzamissioni;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.Conversioni.convertiFasciaOrariaToTimeSlot;
import static it.vige.greenarea.Utilities.setDettaglio;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperoDatiMissioniCorrenti extends
		EmptyRecuperoDatiMissioniCorrenti {

	private Logger logger = getLogger(getClass());

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupero Dati Missioni Correnti");
			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
					APPLICATION_JSON);
			RichiestaMissioni richiestaMissioni = new RichiestaMissioni();
			String todayStr = dateFormat.format(new Date());
			Date today = dateFormat.parse(todayStr);
			Date tomorrow = addDays(today, 1);
			richiestaMissioni.setDataInizio(today);
			richiestaMissioni.setDataFine(tomorrow);
			List<Missione> response = bldr.post(
					entity(richiestaMissioni, APPLICATION_JSON),
					new GenericType<List<Missione>>() {
					});
			@SuppressWarnings("unchecked")
			List<Missione> missioni = (List<Missione>) execution
					.getVariable("missioni");
			missioni.addAll(response);
			if (missioni.size() > 0) {
				FasciaOraria fasciaOraria = missioni.get(0).getFasciaOraria();
				execution.setVariable("policydetail", fasciaOraria);
				setDettaglio(convertiFasciaOrariaToTimeSlot(fasciaOraria),
						client, fasciaOraria);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("notificaerrorereperimentodatimissione");
		}
	}

}
