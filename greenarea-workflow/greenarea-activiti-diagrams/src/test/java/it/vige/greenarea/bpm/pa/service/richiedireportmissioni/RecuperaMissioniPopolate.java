package it.vige.greenarea.bpm.pa.service.richiedireportmissioni;

import it.vige.greenarea.bpm.pa.richiedireportmissioni.EmptyRecuperaMissioni;
import it.vige.greenarea.dto.DettaglioMissione;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaMissioniPopolate extends EmptyRecuperaMissioni {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<DettaglioMissione> missioni = (List<DettaglioMissione>) execution
				.getVariable("missioni");
		missioni.add(new DettaglioMissione("prova1"));
		missioni.add(new DettaglioMissione("prova2"));
	}

}
