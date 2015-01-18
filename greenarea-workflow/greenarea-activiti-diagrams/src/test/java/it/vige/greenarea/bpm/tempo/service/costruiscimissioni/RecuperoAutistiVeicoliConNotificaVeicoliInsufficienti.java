package it.vige.greenarea.bpm.tempo.service.costruiscimissioni;

import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import it.vige.greenarea.bpm.tempo.costruiscimissioni.EmptyRecuperoDatiDeiVeicoliEAutisti;
import it.vige.greenarea.dto.Veicolo;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperoAutistiVeicoliConNotificaVeicoliInsufficienti extends
		EmptyRecuperoDatiDeiVeicoliEAutisti {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Veicolo> veicoli = (List<Veicolo>) execution
				.getVariable("veicoli");
		veicoli.add(new Veicolo(DELIVERING.name(), "targa1"));
	}

}
