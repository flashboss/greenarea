package it.vige.greenarea.bpm.tempo.service.assegnanuoviritiri;

import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import it.vige.greenarea.bpm.tempo.assegnanuoviritiri.EmptyRecuperoNuoviRitiri;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperoNuoviRitiriPopolati extends EmptyRecuperoNuoviRitiri {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Richiesta> ritiri = (List<Richiesta>) execution
				.getVariableLocal("ritiri");
		ritiri.add(new Richiesta(RITIRO.name()));
		ritiri.add(new Richiesta(RITIRO.name()));
	}

}
