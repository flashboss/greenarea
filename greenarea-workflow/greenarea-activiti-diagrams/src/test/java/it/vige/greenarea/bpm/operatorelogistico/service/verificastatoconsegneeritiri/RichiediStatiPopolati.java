package it.vige.greenarea.bpm.operatorelogistico.service.verificastatoconsegneeritiri;

import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.operatorelogistico.verificastatoconsegneeritiri.EmptyRichiediStati;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class RichiediStatiPopolati extends EmptyRichiediStati {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		logger.info("Richiedi stati popolati");
		@SuppressWarnings("unchecked")
		List<Richiesta> stati = (List<Richiesta>) execution
				.getVariableLocal("stati");
		stati.add(new Richiesta(CONSEGNA.name()));
		stati.add(new Richiesta(RITIRO.name()));
	}

}
