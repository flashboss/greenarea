package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATINONCORRETTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class CancellaFasciaOraria extends EmptyCancellaFasciaOraria {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		TimeSlot timeSlot = null;
		try {
			super.execute(execution);
			logger.info("CDI Cancella Fascia Oraria");
			FasciaOraria fasciaOraria = (FasciaOraria) execution
					.getVariable("fasciaoraria");

			Client client = newClient();

			Builder bldr = client.target(
					BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
					APPLICATION_JSON);
			RichiestaMissioni richiesta = new RichiestaMissioni();
			richiesta.setFasciaOraria(fasciaOraria);
			List<Missione> response = bldr.post(
					entity(richiesta, APPLICATION_JSON),
					new GenericType<List<Missione>>() {
					});
			if (response.size() > 0) {
				Messaggio messaggio = (Messaggio) execution
						.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERROREDATINONCORRETTI);
				execution.setVariable("messaggio", messaggio);
			} else {
				bldr = client.target(BASE_URI_TS + "/deleteTimeSlot").request(
						APPLICATION_JSON);
				timeSlot = bldr.post(
						entity(new TimeSlot(fasciaOraria.getId()),
								APPLICATION_JSON), TimeSlot.class);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorecancellazionefasciaoraria");
		}
		if (timeSlot != null) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("errorecancellazionefasciaoraria");
		}
	}
}
