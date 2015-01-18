package it.vige.greenarea.bpm.tempo.service.costruiscimissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDatiDeiVeicoliEAutisti;
import it.vige.greenarea.dto.Veicolo;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class NotificaErroreReperimentoDatiAutistiEVeicoli extends
		EmptyRecuperoDatiDeiVeicoliEAutisti {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		String eseguito = (String) execution
				.getVariable("test_notifica_errore_reperimento_dati_autisti_e_veicoli_eseguito");
		if (eseguito == null) {
			logger.info("Notifica Errore Reperimento Dati Veicoli e Autisti");
			execution
					.setVariable(
							"test_notifica_errore_reperimento_dati_autisti_e_veicoli_eseguito",
							"OK");
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
		} else {
			@SuppressWarnings("unchecked")
			List<Veicolo> veicoli = (List<Veicolo>) execution
					.getVariable("veicoli");
			veicoli.add(new Veicolo(DELIVERING.name(), "targa5"));
		}
	}

}
