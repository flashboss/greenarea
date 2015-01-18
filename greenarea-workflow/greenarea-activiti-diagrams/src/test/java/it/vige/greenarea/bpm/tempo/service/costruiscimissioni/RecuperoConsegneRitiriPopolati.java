package it.vige.greenarea.bpm.tempo.service.costruiscimissioni;

import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDelleConsegneEDeiRitiri;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperoConsegneRitiriPopolati extends
		EmptyRecuperoDelleConsegneEDeiRitiri {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Richiesta> richieste = (List<Richiesta>) execution
				.getVariable("richieste");
		richieste.add(new Richiesta(CONSEGNA.name()));
		richieste.add(new Richiesta(RITIRO.name()));
	}

}
