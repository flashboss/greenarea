package it.vige.greenarea.bpm.amministratore.service.gestiscifiltri;

import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.amministratore.gestiscifiltri.EmptyRecuperaFiltri;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RecuperaFiltriConNotificaErroreLieve extends EmptyRecuperaFiltri {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("Recupera Filtri con Notifica Errore");
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(ERRORELIEVE);
		messaggio.setTipo(ERROREDATIMANCANTI);
		throw new BpmnError("errorerecuperofiltri");
	}

}
