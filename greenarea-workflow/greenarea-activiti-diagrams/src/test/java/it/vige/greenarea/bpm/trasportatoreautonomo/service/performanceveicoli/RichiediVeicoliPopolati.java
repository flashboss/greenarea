package it.vige.greenarea.bpm.trasportatoreautonomo.service.performanceveicoli;

import it.vige.greenarea.bpm.trasportatoreautonomo.performanceveicoli.EmptyRichiediVeicoli;
import it.vige.greenarea.dto.PerformanceVeicoli;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RichiediVeicoliPopolati extends EmptyRichiediVeicoli {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<PerformanceVeicoli> veicoli = (List<PerformanceVeicoli>) execution
				.getVariable("veicoli");
		veicoli.add(new PerformanceVeicoli("performance1"));
		veicoli.add(new PerformanceVeicoli("performance2"));
	}

}
