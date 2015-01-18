package it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static it.vige.greenarea.dto.StatoMissione.COMPLETED;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.operatorelogistico.verificastatoconsegneeritiri.EmptySimulazione;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class SimulazionePopolata extends EmptySimulazione {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("Popolamento Simulazione");
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) execution
				.getVariable("missioni");
		missioni.add(new Missione("missione1", COMPLETED));
		missioni.add(new Missione("missione2", STARTED));
		Messaggio messaggio = (Messaggio) execution
				.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(NESSUNERRORE);
	}

}
