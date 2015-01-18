package it.vige.greenarea.bpm.tempo.service.autorizzamissioni;

import static it.vige.greenarea.dto.TipoRichiesta.RITIRO;
import it.vige.greenarea.bpm.tempo.autorizzamissioni.EmptyElaborazioneRanking;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class ElaborazioneRankingPopolati extends EmptyElaborazioneRanking {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<OperatoreLogistico> operatoriLogistici = (List<OperatoreLogistico>) execution
				.getVariableLocal("operatorilogistici");
		for (OperatoreLogistico operatoreLogistico : operatoriLogistici) {
			List<Richiesta> ritiri = operatoreLogistico.getRitiri();
			ritiri.add(new Richiesta(RITIRO.name()));
			ritiri.add(new Richiesta(RITIRO.name()));
		}
	}

}
