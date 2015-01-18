package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import static it.vige.greenarea.Constants.BASE_URI_ADMINISTRATOR;
import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiParametriToParameterTSs;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Filter;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.AperturaRichieste;
import it.vige.greenarea.dto.ChiusuraRichieste;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Ripetizione;
import it.vige.greenarea.dto.TipologiaClassifica;
import it.vige.greenarea.dto.Tolleranza;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;

public class InserisciFasciaOraria extends EmptyInserisciFasciaOraria {

	private Logger logger = getLogger(getClass());

	private DateFormat data = new SimpleDateFormat("d-MM-yyyy");
	private DateFormat ora = new SimpleDateFormat("HH:mm");

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		boolean error = false;
		try {
			super.execute(execution);
			logger.info("CDI Inserisci Fascia Oraria");
			TimeSlot timeSlot = new TimeSlot();
			timeSlot.setDayFinish(data.format(execution.getVariable("datafine")));
			timeSlot.setDayStart(data.format(execution
					.getVariable("datainizio")));
			Date orarioFine = (Date) execution.getVariable("orariofine");
			if (orarioFine != null)
				timeSlot.setFinishTS(ora.format(orarioFine));
			Date orarioInizio = (Date) execution.getVariable("orarioinizio");
			if (orarioInizio != null)
				timeSlot.setStartTS(ora.format(orarioInizio));
			timeSlot.setPa(((User) execution.getVariable("initiator")).getId());
			timeSlot.setTimeToAcceptRequest(AperturaRichieste
					.valueOf((String) execution
							.getVariable("aperturarichieste")));
			timeSlot.setTimeToRun(ChiusuraRichieste.valueOf((String) execution
					.getVariable("chiusurarichieste")));
			timeSlot.setTimeToStopRequest(ChiusuraRichieste
					.valueOf((String) execution
							.getVariable("chiusurarichieste")));
			timeSlot.setTollerance(Tolleranza.valueOf((String) execution
					.getVariable("tolleranza")));
			timeSlot.setVikInd(TipologiaClassifica.valueOf((String) execution
					.getVariable("tipologiaclassifica")));
			timeSlot.setWmy(Ripetizione.valueOf((String) execution
					.getVariable("ripetitivitapolicy")));
			Client client = newClient();
			Builder bldr = client
					.target(BASE_URI_ADMINISTRATOR + "/getFilters").request(
							APPLICATION_JSON);
			List<Filter> filters = bldr.get(new GenericType<List<Filter>>() {
			});
			if (filters != null && filters.size() > 0)
				timeSlot.setRoundCode(filters.get(0).getId().getRoundCode());
			bldr = client.target(BASE_URI_TS + "/addTimeSlot").request(
					APPLICATION_JSON);
			TimeSlot rsTimeSlot = bldr.post(entity(timeSlot, APPLICATION_JSON),
					TimeSlot.class);
			if (rsTimeSlot == null)
				error = true;

			@SuppressWarnings("unchecked")
			List<Parametro> tuttiIParametri = (List<Parametro>) execution
					.getVariable("parametrits");
			List<Parametro> parametri = new ArrayList<Parametro>();
			for (Parametro parametro : tuttiIParametri)
				if (parametro.getValoreMinimo() != 0.0
						|| parametro.getValoreMassimo() != 0.0)
					parametri.add(parametro);

			List<ParameterTS> parameterTss = convertiParametriToParameterTSs(
					parametri, null);
			bldr = client.target(BASE_URI_TS + "/configParameterTS").request(
					APPLICATION_JSON);
			for (ParameterTS parameterTS : parameterTss) {
				parameterTS.setTs(rsTimeSlot);
				ParameterTS result = bldr.post(
						entity(parameterTS, APPLICATION_JSON),
						ParameterTS.class);
				if (result == null)
					error = true;
			}

			Price priceGreen = new Price();
			priceGreen.setColor(VERDE);
			Object accessoVeicoliVerdi = execution
					.getVariable("accessoveicoliverdi");
			if (accessoVeicoliVerdi != null)
				priceGreen.setTypeEntry(AccessoVeicoli
						.valueOf(accessoVeicoliVerdi + ""));
			Object prezzoFissoVerdi = execution.getVariable("prezzofissoverdi");
			if (prezzoFissoVerdi != null)
				priceGreen.setFixPrice((double) prezzoFissoVerdi);
			priceGreen.setTs(rsTimeSlot);
			Price priceRed = new Price();
			priceRed.setColor(ROSSO);
			Object accessoVeicoliRossi = execution
					.getVariable("accessoveicolirossi");
			if (accessoVeicoliRossi != null)
				priceRed.setTypeEntry(AccessoVeicoli
						.valueOf(accessoVeicoliRossi + ""));
			Object prezzoFissoRossi = execution.getVariable("prezzofissorossi");
			if (prezzoFissoRossi != null)
				priceRed.setFixPrice((double) prezzoFissoRossi);
			Object prezzoMassimoRossi = execution
					.getVariable("prezzomassimorossi");
			if (prezzoMassimoRossi != null)
				priceRed.setMaxPrice((double) prezzoMassimoRossi);
			Object prezzoMinimoRossi = execution
					.getVariable("prezzominimorossi");
			if (prezzoMinimoRossi != null)
				priceRed.setMinPrice((double) prezzoMinimoRossi);
			priceRed.setTs(rsTimeSlot);
			Price priceYellow = new Price();
			priceYellow.setColor(GIALLO);
			Object accessoVeicoliGialli = execution
					.getVariable("accessoveicoligialli");
			if (accessoVeicoliGialli != null)
				priceYellow.setTypeEntry(AccessoVeicoli
						.valueOf(accessoVeicoliGialli + ""));
			Object prezzoMassimoGialli = execution
					.getVariable("prezzomassimogialli");
			if (prezzoMassimoGialli != null)
				priceYellow.setMaxPrice((double) prezzoMassimoGialli);
			Object prezzoMinimoGialli = execution
					.getVariable("prezzominimogialli");
			if (prezzoMinimoGialli != null)
				priceYellow.setMinPrice((double) prezzoMinimoGialli);
			priceYellow.setTs(rsTimeSlot);

			bldr = client.target(BASE_URI_TS + "/addPrice").request(
					APPLICATION_JSON);
			Price rsPriceYellow = bldr.post(
					entity(priceYellow, APPLICATION_JSON), Price.class);
			if (rsPriceYellow == null)
				error = true;
			Price rsPriceRed = bldr.post(entity(priceRed, APPLICATION_JSON),
					Price.class);
			if (rsPriceRed == null)
				error = true;
			Price rsPriceGreen = bldr.post(
					entity(priceGreen, APPLICATION_JSON), Price.class);
			if (rsPriceGreen == null)
				error = true;

			FasciaOraria fasciaOraria = convertiTimeSlotToFasciaOraria(
					rsTimeSlot, asList(new ParameterTS[0]),
					asList(new ParameterGen[0]), asList(new Price[] { priceRed,
							priceYellow, priceGreen }));
			fasciaOraria.setParametri(parametri);
			execution.setVariable("fasciaoraria", fasciaOraria);

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentofasciaoraria");
		}
		if (error) {
			Messaggio messaggio = (Messaggio) execution
					.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroreinserimentofasciaoraria");
		}
	}
}
