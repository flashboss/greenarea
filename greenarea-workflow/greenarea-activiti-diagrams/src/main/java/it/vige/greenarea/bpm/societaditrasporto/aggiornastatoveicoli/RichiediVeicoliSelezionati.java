package it.vige.greenarea.bpm.societaditrasporto.aggiornastatoveicoli;

import it.vige.greenarea.dto.Veicolo;

import java.util.Collection;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class RichiediVeicoliSelezionati implements TaskListener {

	private static final long serialVersionUID = 7049543378297226582L;

	@Override
	@SuppressWarnings("unchecked")
	public void notify(DelegateTask delegateTask) {
		Collection<Veicolo> veicoli = (Collection<Veicolo>) delegateTask
				.getVariable("veicoli");
		delegateTask.setVariable("veicoliselezionati", veicoli);
	}

}
