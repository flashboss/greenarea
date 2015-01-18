package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Parametro;

import java.util.Collection;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;

public class CancellaParametriSelezionati implements JavaDelegate {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info("Cancella Parametri Selezionati");
		@SuppressWarnings("unchecked")
		List<Parametro> parametriaggiuntiselezionati = (List<Parametro>) execution
				.getVariable("parametriaggiuntiselezionati");
		@SuppressWarnings("unchecked")
		Collection<Parametro> parametriaggiunti = (Collection<Parametro>) execution
				.getVariable("parametriaggiunti");
		parametriaggiunti.removeAll(parametriaggiuntiselezionati);
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(NESSUNERRORE);
	}
}
