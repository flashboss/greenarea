package it.vige.greenarea.bpm.tempo.importaconsegneeritiri;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;

public class SollecitoEseguito implements JavaDelegate {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info("Sollecito Eseguito");
		execution.setVariableLocal("sollecitoeseguito", true);
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(NESSUNERRORE);
	}
}