package it.vige.greenarea.bpm.operatorelogistico.service.accessiga;

import static it.vige.greenarea.bpm.risultato.Categoria.OK;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.bpm.risultato.Tipo.NESSUNERRORE;
import static it.vige.greenarea.dto.StatoMissione.STARTED;
import it.vige.greenarea.bpm.pa.richiediaccessiga.EmptyRichiediAccessiGa;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.dto.Missione;

import org.activiti.engine.delegate.DelegateExecution;

public class RichiediAccessiGaPopolata extends EmptyRichiediAccessiGa {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		execution.setVariable("missione", new Missione("missione1", STARTED));
		Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
		messaggio.setCategoria(OK);
		messaggio.setTipo(ERRORESISTEMA);
		messaggio.setTipo(NESSUNERRORE);
	}

}
