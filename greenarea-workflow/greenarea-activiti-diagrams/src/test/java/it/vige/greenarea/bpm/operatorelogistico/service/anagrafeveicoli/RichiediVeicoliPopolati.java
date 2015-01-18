package it.vige.greenarea.bpm.operatorelogistico.service.anagrafeveicoli;

import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import static it.vige.greenarea.dto.StatoVeicolo.IDLE;
import it.vige.greenarea.bpm.operatorelogistico.anagrafeveicoli.EmptyRichiediVeicoli;
import it.vige.greenarea.dto.Veicolo;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RichiediVeicoliPopolati extends EmptyRichiediVeicoli {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) execution
				.getVariableLocal("veicoli");
		veicoli.add(new Veicolo(DELIVERING.name(), "targa1"));
		veicoli.add(new Veicolo(IDLE.name(), "targa2"));
	}

}
