package it.vige.greenarea.bpm.trasportatoreautonomo.service.aggiornastatoveicolo;

import static it.vige.greenarea.dto.StatoVeicolo.DELIVERING;
import it.vige.greenarea.bpm.trasportatoreautonomo.aggiornastatoveicolo.EmptyRichiediVeicolo;
import it.vige.greenarea.dto.Veicolo;

import org.activiti.engine.delegate.DelegateExecution;

public class RichiediVeicoloPopolato extends EmptyRichiediVeicolo {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		Veicolo veicolo = (Veicolo) execution.getVariableLocal("veicolo");
		veicolo.setStato(DELIVERING.name());
		veicolo.setTarga("targa1");
	}

}
