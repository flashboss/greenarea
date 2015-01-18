package it.vige.greenarea.bpm.societaditrasporto.service.visualizzamissioniautorizzate;

import static it.vige.greenarea.dto.AperturaRichieste._12_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._4_ORE_PRIMA;
import static it.vige.greenarea.dto.Ripetizione.MAI;
import static it.vige.greenarea.dto.Ripetizione.TUTTI_I_GIORNI;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_GLOBALE;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_LOCALE;
import static it.vige.greenarea.dto.Tolleranza._20_PER_CENTO;
import static java.util.Arrays.asList;
import it.vige.greenarea.bpm.societaditrasporto.visualizzamissioniautorizzate.EmptyRecuperoDatiPolicy;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Prezzo;
import it.vige.greenarea.dto.GreenareaUser;

import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

public class RecuperoDatiPolicyPopolate extends EmptyRecuperoDatiPolicy {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		super.execute(execution);
		@SuppressWarnings("unchecked")
		List<FasciaOraria> policy = (List<FasciaOraria>) execution
				.getVariable("policy");
		policy.add(new FasciaOraria("06", new Date(), new Date(), new Date(),
				new Date(), _12_GIORNI_PRIMA.name(), _4_ORE_PRIMA.name(),
				TUTTI_I_GIORNI.name(), _20_PER_CENTO.name(),
				PREMIA_RISPOSTA_LOCALE.name(), new GreenareaUser("patorino"),
				asList(new Parametro[] {}), asList(new Prezzo[] {})));
		policy.add(new FasciaOraria("02", new Date(), new Date(), new Date(),
				new Date(), _12_GIORNI_PRIMA.name(), _4_ORE_PRIMA.name(), MAI
						.name(), _20_PER_CENTO.name(), PREMIA_RISPOSTA_GLOBALE
						.name(), new GreenareaUser("pamilano"),
				asList(new Parametro[] {}), asList(new Prezzo[] {})));
	}
}
