package it.vige.greenarea.bpm.societaditrasporto.service.visualizzamissioniautorizzate;

import static it.vige.greenarea.dto.StatoMissione.COMPLETED;
import static it.vige.greenarea.dto.StatoMissione.STARTED;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

import it.vige.greenarea.bpm.societaditrasporto.visualizzamissioniautorizzate.EmptyRecuperoDatiMissioni;
import it.vige.greenarea.dto.Missione;

public class RecuperoDatiMissioniPopolate extends EmptyRecuperoDatiMissioni {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) execution
				.getVariable("missioni");
		missioni.add(new Missione("missione1", COMPLETED));
		missioni.add(new Missione("missione2", STARTED));
	}

}
