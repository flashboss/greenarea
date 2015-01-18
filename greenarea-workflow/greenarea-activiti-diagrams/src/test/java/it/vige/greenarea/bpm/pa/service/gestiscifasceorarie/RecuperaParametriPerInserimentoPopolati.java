package it.vige.greenarea.bpm.pa.service.gestiscifasceorarie;

import static it.vige.greenarea.dto.Peso.BASSO;
import static it.vige.greenarea.dto.Peso.NESSUNO;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static it.vige.greenarea.dto.TipologiaParametro.COSTO;
import it.vige.greenarea.bpm.pa.gestiscifasceorarie.EmptyRecuperaParametriPerInserimento;
import it.vige.greenarea.dto.Parametro;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaParametriPerInserimentoPopolati extends EmptyRecuperaParametriPerInserimento {

	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Parametro> parametriTs = (List<Parametro>) execution
				.getVariable("parametrits");
		parametriTs.add(new Parametro("nomets1", "descrizionets1",
				"unitaMisuraTs1", BENEFICIO.name(), true, 0.0, 0.0, NESSUNO
						.name()));
		parametriTs.add(new Parametro("nomets2", "descrizionets2",
				"unitaMisuraTs2", COSTO.name(), true, 0.0, 0.0, BASSO.name()));
	}
}
