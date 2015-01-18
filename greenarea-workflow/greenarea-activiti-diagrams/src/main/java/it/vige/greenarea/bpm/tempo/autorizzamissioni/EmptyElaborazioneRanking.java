package it.vige.greenarea.bpm.tempo.autorizzamissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;

public class EmptyElaborazioneRanking implements JavaDelegate {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info("Elaborazione Ranking");
		@SuppressWarnings("unchecked")
		List<OperatoreLogistico> operatoriLogistici = (List<OperatoreLogistico>) execution
				.getVariableLocal("operatorilogistici");
		for (OperatoreLogistico operatoreLogistico : operatoriLogistici)
			operatoreLogistico.setRitiri(new ArrayList<Richiesta>());
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(NESSUNERRORE);
	}

}
