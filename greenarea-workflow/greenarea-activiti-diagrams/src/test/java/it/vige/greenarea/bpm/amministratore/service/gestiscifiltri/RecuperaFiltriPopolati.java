package it.vige.greenarea.bpm.amministratore.service.gestiscifiltri;

import it.vige.greenarea.bpm.amministratore.gestiscifiltri.EmptyRecuperaFiltri;
import it.vige.greenarea.dto.Filtro;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaFiltriPopolati extends EmptyRecuperaFiltri {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Filtro> filtri = (List<Filtro>) execution.getVariable("filtri");
		filtri.add(new Filtro("01", "tnt"));
		filtri.add(new Filtro("02", "tnt"));
		filtri.add(new Filtro("06", "tnt"));
	}
}
