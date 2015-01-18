package it.vige.greenarea.bpm.pa.service.gestisciparametri;

import static it.vige.greenarea.dto.Peso.BASSO;
import static it.vige.greenarea.dto.Peso.NESSUNO;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static it.vige.greenarea.dto.TipologiaParametro.COSTO;
import it.vige.greenarea.bpm.pa.gestisciparametri.EmptyRecuperaParametri;
import it.vige.greenarea.dto.Parametro;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaParametriPopolati extends EmptyRecuperaParametri {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Parametro> parametri = (List<Parametro>) execution
				.getVariable("parametri");
		parametri.add(new Parametro("nome1", "descrizione1", "unitaMisura1",
				BENEFICIO.name(), true, 4.1, 9.5, NESSUNO.name()));
		parametri.add(new Parametro("nome2", "descrizione2", "unitaMisura2",
				COSTO.name(), true, 2.8, 6.8, BASSO.name()));
	}
}
