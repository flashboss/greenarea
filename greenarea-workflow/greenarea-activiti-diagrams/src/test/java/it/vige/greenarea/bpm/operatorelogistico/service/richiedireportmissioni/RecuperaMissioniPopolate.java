package it.vige.greenarea.bpm.operatorelogistico.service.richiedireportmissioni;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import static it.vige.greenarea.dto.StatoMissione.WAITING;
import it.vige.greenarea.bpm.operatorelogistico.richiedireportmissioni.EmptyRecuperaMissioni;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperaMissioniPopolate extends EmptyRecuperaMissioni {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<Missione> missioni = (List<Missione>) execution
				.getVariable("missioni");
		missioni.add(new Missione("missione1", STARTED));
		missioni.add(new Missione("missione2", WAITING));
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(ERRORESISTEMA);
		messaggio.setTipo(NESSUNERRORE);
	}

}
