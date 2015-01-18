package it.vige.greenarea.bpm.autista.gestioneconsegne;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;

public class EmptyAggiornaStatoInCarico implements TaskListener {

	private static final long serialVersionUID = -7736374196753860784L;

	private Logger logger = getLogger(getClass());

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.info("Aggiorna Stato in Carico");
		Messaggio messaggio = (Messaggio) delegateTask.getExecution()
				.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(NESSUNERRORE);
	}

}
