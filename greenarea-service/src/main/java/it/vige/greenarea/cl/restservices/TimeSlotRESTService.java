/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.greenarea.cl.restservices;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.Conversioni.convertiMissionsToMissioni;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.Conversioni.convertiTransportsToRichieste;
import static it.vige.greenarea.Conversioni.convertiTransportsToScheds;
import static it.vige.greenarea.Conversioni.convertiVehicleToVeicolo;
import static it.vige.greenarea.Utilities.aggiungiValoriAMissione;
import static it.vige.greenarea.Utilities.associaFasciaOrariaARichiesta;
import static it.vige.greenarea.Utilities.prelevaDateMissioni;
import static it.vige.greenarea.Utilities.prelevaRoundCodesMissioni;
import static it.vige.greenarea.Utilities.setDettaglio;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Locale.US;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jboss.resteasy.util.FindAnnotation.findAnnotation;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.slf4j.Logger;

import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.TimeSlotInfo;
import it.vige.greenarea.cl.control.TimeSlotControl;
import it.vige.greenarea.cl.control.VehicleControl;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.TsStat;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.cl.scheduling.Scheduler;
import it.vige.greenarea.cl.sessions.ParameterGenFacade;
import it.vige.greenarea.cl.sessions.ParameterTSFacade;
import it.vige.greenarea.cl.sessions.ValueMissionFacade;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.Sched;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.gtg.db.facades.ExchangeStopFacade;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.gtg.db.facades.TransportServiceClassFacade;
import it.vige.greenarea.gtg.ejb.MissionBuilderBean;
import it.vige.greenarea.sgapl.sgot.business.SGOTbean;

/**
 * <p>
 * Class: TimeSlotRESTService
 * </p>
 * <p>
 * Description: Classe che gestisce l'utilizzo parte Amministrazione
 * </p>
 * 
 */
@Path("/TimeSlot")
@Stateless
public class TimeSlotRESTService {

	private Logger logger = getLogger(getClass());

	@Retention(RetentionPolicy.RUNTIME)
	@StringParameterUnmarshallerBinder(DateFormatter.class)
	public @interface DateFormat {
		String value();
	}

	public static class DateFormatter implements StringParameterUnmarshaller<Date> {
		private SimpleDateFormat formatter;

		public void setAnnotations(Annotation[] annotations) {
			DateFormat format = findAnnotation(annotations, DateFormat.class);
			formatter = new SimpleDateFormat(format.value(), US);
		}

		public Date fromString(String str) {
			try {
				return formatter.parse(str);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@EJB
	private TimeSlotControl tsc;
	@EJB
	private ParameterGenFacade pgf;
	@EJB
	private ParameterTSFacade ptsf;
	@EJB
	private Scheduler sc;
	@EJB
	private MissionBuilderBean missionBean;
	@EJB
	private SGOTbean sgotBean;
	@EJB
	private TransportServiceClassFacade tscf;
	@EJB
	private ValueMissionFacade valueMissionFacade;
	@EJB
	private MissionFacade missionFacade;
	@EJB
	private ExchangeStopFacade exchangeStopFacade;
	@EJB
	private TransportFacade transportFacade;
	@EJB
	private FreightFacade freightFacade;
	@EJB
	private VehicleControl vc;
	@EJB
	private MissionBuilderBean missionBuilderBean;
	@EJB
	private TransportServiceClassFacade transportServiceClassFacade;

	/**
	 * <p>
	 * Method: addTimeSlot
	 * </p>
	 * <p>
	 * Description: Questo metodo inserisce un TimeSlot nel sistema
	 * </p>
	 * 
	 * @param TimeSlot
	 *            tsToAdd
	 * @return TimeSlot
	 */
	@POST
	@Path("/addTimeSlot")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public TimeSlot addTimeSlot(TimeSlot tsToAdd) {
		return tsc.addSlotTime(tsToAdd);

	}

	/**
	 * <p>
	 * Method: updateTimeSlot
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiorna un TimeSlot nel sistema
	 * </p>
	 * 
	 * @param FasciaOraria
	 *            tsToUpdate
	 * @return FasciaOraria
	 */
	@POST
	@Path("/updateTimeSlot")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public TimeSlot updateTimeSlot(TimeSlot tsToUpdate) {
		return tsc.updateSlotTime(tsToUpdate);

	}

	/**
	 * <p>
	 * Method: deleteTimeSlot
	 * </p>
	 * <p>
	 * Description: Questo metodo cancella un TimeSlot dal sistema
	 * </p>
	 * 
	 * @param TimeSlot
	 *            tsToDelete
	 * @return TimeSlot
	 */
	@POST
	@Path("/deleteTimeSlot")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public TimeSlot deleteTimeSlot(TimeSlot tsToDelete) {
		return tsc.deleteSlotTime(tsToDelete);

	}

	/**
	 * <p>
	 * Method: deleteTimeSlot
	 * </p>
	 * <p>
	 * Description: Questo metodo cancella un TimeSlot dal sistema
	 * </p>
	 * 
	 * @param TimeSlot
	 *            tsToDelete
	 * @return TimeSlot
	 */
	@GET
	@Path("/deleteMissions")
	@Produces(APPLICATION_JSON)
	public List<Mission> deleteMissions() {
		List<Mission> missions = missionFacade.findAll();
		for (Mission mission : missions) {
			missionFacade.remove(mission);
		}
		return new ArrayList<Mission>();
	}

	/**
	 * <p>
	 * Method: addParameterGen
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge un parametro di tipo generale al
	 * sistema
	 * </p>
	 * 
	 * @param ParameterGen
	 *            pg
	 * @return ParameterGen
	 */
	@POST
	@Path("/addParameterGen")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public ParameterGen addParameterGen(ParameterGen pg) {
		return tsc.addParameterGen(pg);
	}

	/**
	 * <p>
	 * Method: updateParameterGen
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiorna un parametro di tipo generale al
	 * sistema
	 * </p>
	 * 
	 * @param ParameterGen
	 *            pg
	 * @return ParameterGen
	 */
	@POST
	@Path("/updateParameterGen")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public ParameterGen updateParameterGen(ParameterGen pg) {
		tsc.updateParameterGen(pg);
		return pgf.find(pg.getId());
	}

	/**
	 * <p>
	 * Method: deleteParameterGen
	 * </p>
	 * <p>
	 * Description: Questo metodo cancella un parametro di tipo generale al
	 * sistema
	 * </p>
	 * 
	 * @param ParameterGen
	 *            pg
	 * @return ParameterGen
	 */
	@POST
	@Path("/deleteParameterGen")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public ParameterGen deleteParameterGen(ParameterGen pg) {
		return tsc.deleteParameterGen(pg);
	}

	/**
	 * <p>
	 * Method: addPrice
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge un price al sistema associandolo a
	 * una fascia oraria
	 * </p>
	 * 
	 * @param Price
	 *            price
	 * @return Price
	 */
	@POST
	@Path("/addPrice")
	@Consumes(APPLICATION_JSON)
	public Price addPrices(Price price) {
		tsc.addPriceToTimeSlot(price);
		return price;
	}

	/**
	 * <p>
	 * Method: deletePrice
	 * </p>
	 * <p>
	 * Description: Questo metodo cancella un price al sistema associandolo a
	 * una fascia oraria
	 * </p>
	 * 
	 * @param Price
	 *            price
	 * @return Price
	 */
	@POST
	@Path("/deletePrice")
	@Consumes(APPLICATION_JSON)
	public Price deletePrices(Price price) {
		tsc.deletePrice(price);
		return price;
	}

	/**
	 * <p>
	 * Method: updatePrice
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiorna un price al sistema associandolo a
	 * una fascia oraria
	 * </p>
	 * 
	 * @param Price
	 *            price
	 * @return Price
	 */
	@POST
	@Path("/updatePrice")
	@Consumes(APPLICATION_JSON)
	public Price updatePrices(Price price) {
		tsc.updatePriceToTimeSlot(price);
		return price;
	}

	/**
	 * <p>
	 * Method: findTimeSlot
	 * </p>
	 * <p>
	 * Description : Questo metodo cerca un TimeSlot e nel caso di esito
	 * positivo restituisce un TimeSlot
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return TimeSlot
	 */
	@GET
	@Path("/findTimeSlot/{idTimeSlot}")
	@Produces(APPLICATION_JSON)
	public TimeSlot findTS(@PathParam("idTimeSlot") int idTimeSlot) {
		return tsc.findTimeSlot(idTimeSlot);
	}

	/**
	 * <p>
	 * Method: findAllTimeSlot
	 * </p>
	 * <p>
	 * Description : Questo medoto restituisce tutte le fasce orarie registrare
	 * nel sistema
	 * </p>
	 * 
	 * @return List<TimeSlot>
	 */
	@GET
	@Path("/findAllTimeSlot")
	@Produces({ "application/json" })
	public List<TimeSlot> findAllTimeSlots() {
		return tsc.findAllTimeSlots();
	}

	/**
	 * <p>
	 * Method: findAllTimeSlot
	 * </p>
	 * <p>
	 * Description : Questo metodo restituisce i parametri generali
	 * configurabili per una pa
	 * </p>
	 * 
	 * @return List<TimeSlot>
	 */
	@GET
	@Path("/findAllTimeSlot/{userId}")
	@Produces({ "application/json" })
	public List<TimeSlot> findAllTimeSlots(@PathParam("userId") String userId) {
		return tsc.findAllTimeSlots(userId);
	}

	/**
	 * <p>
	 * Method: findAllParameterGenAvailable
	 * </p>
	 * <p>
	 * Description : Questo metodo restituisce i parametri generali
	 * configurabili per una fascia oraria
	 * </p>
	 * 
	 * @return List<ParameterGen>
	 */
	@GET
	@Path("/findAllParameterGenAvailable")
	@Produces({ "application/json" })
	public List<ParameterGen> findAllParameterGenAvailable() {
		return tsc.findParameterGenAvailable();
	}

	/**
	 * <p>
	 * Method: findAllParameterGen
	 * </p>
	 * <p>
	 * Description : Questo metodo restituisce tutti i parametri generali nel
	 * sistema
	 * </p>
	 * 
	 * @return List<ParameterGen>
	 */
	@GET
	@Path("/findAllParameterGen")
	@Produces(APPLICATION_JSON)
	public List<ParameterGen> findAllParameterGen() {
		return tsc.findAllParameterGen();
	}

	/**
	 * <p>
	 * Method: findParameterOfTimeSlot
	 * </p>
	 * <p>
	 * Description : Questo metodo restituisce i parametri settati per una
	 * fascia oraria
	 * </p>
	 * 
	 * @param idTimeSlot
	 * @return
	 */
	@GET
	@Path("/findParameterOfTimeSlot/{idTimeSlot}")
	@Produces(APPLICATION_JSON)
	public List<ParameterTS> findParameterOfTimeSlot(@PathParam("idTimeSlot") int idTimeSlot) {
		return tsc.getParameterForRank(idTimeSlot);
	}

	/**
	 * <p>
	 * Method: configParameterTsToTimeSlot
	 * </p>
	 * <p>
	 * Description : Questo metodo aggiunge un parametroTS associandolo a una
	 * fascia oraria
	 * </p>
	 * 
	 * @param ParameterTS
	 *            pts
	 * @return ParameterTS
	 */
	@POST
	@Path("/configParameterTS")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public ParameterTS configParameterTsToTimeSlot(ParameterTS pts) {
		return tsc.configParameterToTimeSlot(pts);
	}

	/**
	 * <p>
	 * Method: removeParametersTsToTimeSlot
	 * </p>
	 * <p>
	 * Description : Questo metodo rimuove un elenco di parametri TS
	 * </p>
	 * 
	 * @param ParameterTS
	 *            pts
	 * @return ParameterTS
	 */
	@POST
	@Path("/removeParametersTS")
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public TimeSlot removeParametersTsToTimeSlot(TimeSlot timeSlot) {
		List<ParameterTS> pts = ptsf.findAll(timeSlot);
		for (ParameterTS parameterTS : pts)
			ptsf.remove(parameterTS);
		return timeSlot;
	}

	/**
	 * <p>
	 * Method: requests
	 * </p>
	 * <p>
	 * Description : Questo metodo seleziona tutte le richieste per una fascia
	 * oraria e una data typePg inserire 5
	 * </p>
	 * 
	 * @param dateMiss
	 * @param idTimeSlot
	 * @param typePg
	 * @return List<Request>
	 */
	@GET
	@Path("/requests/{dateMiss}/{idTimeSlot}/{typePG}")
	@Produces(APPLICATION_JSON)
	public List<Request> selectRequests(@PathParam("dateMiss") @DateFormat("EEE MMM d HH:mm:ss z yyyy") Date dateMiss,
			@PathParam("idTimeSlot") int idTimeSlot, @PathParam("typePG") int typePg) {
		logger.info("Dentro select");
		return tsc.selectMission(dateMiss, idTimeSlot, typePg);

	}

	/**
	 * <p>
	 * Method: simulRank
	 * </p>
	 * <p>
	 * Description : Questo metodo simula il ranking per le richieste di una
	 * certa fascia oraria e data
	 * </p>
	 * 
	 * @param dateMiss
	 * @param idTimeSlot
	 * @return List<Request>
	 */
	@GET
	@Path("/simulRank/{idTimeSlot}/{dateMiss}")
	@Produces(APPLICATION_JSON)
	public List<Request> simulRank(@PathParam("dateMiss") @DateFormat("EEE MMM d HH:mm:ss z yyyy") Date dateMiss,
			@PathParam("idTimeSlot") int idTimeSlot) {
		List<Request> c = tsc.simulRank(dateMiss, idTimeSlot);
		return c;
	}

	/**
	 * <p>
	 * Method: getParameterForRank
	 * </p>
	 * <p>
	 * Description : Questo metodo restituisce i parametri che vengono usati per
	 * analizzare una fascia oraria
	 * </p>
	 * 
	 * @param idTimeSlot
	 * @return List<ParameterTS>
	 */
	@GET
	@Path("/getParameterForRank/{idTimeSlot}")
	@Produces(APPLICATION_JSON)
	public List<ParameterTS> getParameterOfTimeSlot(@PathParam("idTimeSlot") int idTimeSlot) {
		return tsc.getParameterForRank(idTimeSlot);
	}

	/**
	 * <p>
	 * Method: getInfoTimeSlot
	 * </p>
	 * <p>
	 * Description: Metodo che restituisce tutte le informazioni di una Fascia
	 * Oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return TimeSlotInfo
	 */
	@GET
	@Path("/getInfoTimeSlot/{idTimeSlot}")
	@Produces(APPLICATION_JSON)
	public TimeSlotInfo getInfoTimeSlot(@PathParam("idTimeSlot") int idTimeSlot) {
		return tsc.getInfoTimeSlot(idTimeSlot);
	}

	/**
	 * <p>
	 * Method: startScheduler
	 * </p>
	 * <p>
	 * Description: Programma la schedulazione per una fascia oraria per i
	 * prossimi days giorni
	 * </p>
	 * 
	 * @param int
	 *            days
	 * @param int
	 *            idTimeSlot
	 * @return
	 */
	@POST
	@Path("/startScheduler")
	public String simulSch(Transport transport) {
		transport.setDateMiss(addDays(transport.getDateMiss(), 1));
		sc.addSchedule(transport);
		sc.editSchedule(transport);
		transportFacade.edit(transport);
		return "Scheduler OK";
	}

	/**
	 * <p>
	 * Method: getAllSchedules
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce la schedualazione di una fascia
	 * oraria disponibile
	 * </p>
	 * 
	 * @return List<Sched>
	 */
	@GET
	@Path("/getAllSchedules")
	@Produces(APPLICATION_JSON)
	public List<Sched> getAllSchedules() {
		List<Transport> transports = sc.getAllSchedule();
		return convertiTransportsToScheds(transports);
	}

	@GET
	@Path("/getSchedules/{idTimeSlot}")
	@Produces(APPLICATION_JSON)
	public List<Sched> getAllSchedules(@PathParam("idTimeSlot") Integer idTimeslot) {
		List<Transport> transports = sc.getSchedules(idTimeslot);
		return convertiTransportsToScheds(transports);
	}

	/**
	 * <p>
	 * Method: getRank
	 * </p>
	 * <p>
	 * Description: Avvia la schedulazione delle richieste per una fascia oraria
	 * a una data stabilita
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @param int
	 *            dateMiss es gmaaaa
	 * @return List<Request>
	 */
	@GET
	@Path("/getRank/{idTimeSlot}/{dateMiss}")
	@Produces(APPLICATION_JSON)
	public List<Request> getrRank(@PathParam("idTimeSlot") int idTimeSlot,
			@PathParam("dateMiss") @DateFormat("EEE MMM d HH:mm:ss z yyyy") Date dateMiss) {
		return tsc.getRank(dateMiss, idTimeSlot);
	}

	/**
	 * <p>
	 * Method: updateVikor
	 * </p>
	 * <p>
	 * Description: Avvia la schedulazione delle richieste per una fascia oraria
	 * a una data stabilita
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @param int
	 *            dateMiss es gmaaaa
	 * @param int
	 *            List<Request>
	 * @return List<Request>
	 */
	@GET
	@Path("/updateVikor/{idTimeSlot}/{dateMiss}")
	@Produces(APPLICATION_JSON)
	public List<Request> updateVikor(@PathParam("idTimeSlot") int idTimeSlot,
			@PathParam("dateMiss") @DateFormat("EEE MMM d HH:mm:ss z yyyy") Date dateMiss) {
		return tsc.updateVikor(null, dateMiss, idTimeSlot);
	}

	/**
	 * <p>
	 * Method: getPriceOfTimeSlot
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce le tariffe associate a una fascia
	 * oraria
	 * </p>
	 * 
	 * @param int
	 *            idTimeSlot
	 * @return List<Price>
	 */
	@GET
	@Path("/getPriceOfTimeSlot/{idTimeSlot}")
	@Produces(APPLICATION_JSON)
	public List<Price> getPriceOfTimeSlot(@PathParam("idTimeSlot") int idTimeSlot) {
		return tsc.getPriceOfTimeSlot(idTimeSlot);
	}

	/**
	 * <p>
	 * Method: getAllTsStats
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce lo storico delle fasce orarie
	 * schedulate
	 * </p>
	 * 
	 * @return List<TsStat
	 */
	@GET
	@Path("/getAllTsStats")
	@Produces(APPLICATION_JSON)
	public List<TsStat> getAllTsStats() {
		return tsc.getAllStats();
	}

	/**
	 * <p>
	 * Method: getStoryBoard
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce lo storico di una fascia oraria
	 * processata in una certa data
	 * </p>
	 * 
	 * @param idTimeSlot
	 * @param dateMiss
	 * @return List<Request>
	 */
	@GET
	@Path("/getStoryBoard/{idTimeSlot}/{dateMiss}")
	@Produces(APPLICATION_JSON)
	public List<Request> getStoryBoard(@PathParam("idTimeSlot") int idTimeSlot,
			@PathParam("dateMiss") @DateFormat("EEE MMM d HH:mm:ss z yyyy") Date dateMiss) {
		return tsc.getStoryBoard(idTimeSlot, dateMiss);
	}

	/**
	 * <p>
	 * Method:
	 * </p>
	 * <p>
	 * Description: Come simulRank
	 * </p>
	 * 
	 * @param dateMiss
	 * @param idTimeSlot
	 * @return
	 */
	@GET
	@Path("/simul/{idTimeSlot}/{dateMiss}")
	@Produces(APPLICATION_JSON)
	public List<Request> simul(@PathParam("dateMiss") @DateFormat("EEE MMM d HH:mm:ss z yyyy") Date dateMiss,
			@PathParam("idTimeSlot") int idTimeSlot) {

		List<Request> c = tsc.simulRank(dateMiss, idTimeSlot);
		return c;
	}

	/**
	 * <p>
	 * Method: buildCityLogisticsMission
	 * </p>
	 * <p>
	 * Description: Questo metodo costruisce le missioni
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/buildCityLogisticsMission")
	@Produces(APPLICATION_JSON)
	public Mission buildCityLogisticsMission(Missione missione) {
		Mission mission = new Mission();
		Date date = new Date();
		Timestamp timestamp = missionBean.puliziaMissioni(date);
		missionBean.buildCityLogisticsMission(missione, mission, timestamp);
		return mission;
	}

	/**
	 * <p>
	 * Method: simulaCostruzioneMissioni
	 * </p>
	 * <p>
	 * Description: Questo metodo costruisce le missioni
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/simulaCostruzioneMissioni")
	@Produces(APPLICATION_JSON)
	public List<Missione> simulaCostruzioneMissioni(Missione richiestaMissione) {
		List<Richiesta> richiesteDaMissione = richiestaMissione.getRichieste();
		List<Mission> missioniCostruite = new ArrayList<Mission>();
		List<Date> allDates = prelevaDateMissioni(richiesteDaMissione);
		List<String> allRoundCodes = prelevaRoundCodesMissioni(richiesteDaMissione);
		for (Date date : allDates) {
			for (String roundCode : allRoundCodes) {
				List<TransportServiceClass> transportSvcClassList = transportServiceClassFacade.findAll();
				List<Mission> result = new ArrayList<Mission>();
				Client client = newClient();
				Builder bldr = client.target(BASE_URI_TS + "/findAllTimeSlot").request(APPLICATION_JSON);
				List<TimeSlot> timeSlots = bldr.get(new GenericType<List<TimeSlot>>() {
				});
				List<FasciaOraria> fasceOrarie = new ArrayList<FasciaOraria>();
				try {
					for (TimeSlot timeSlot : timeSlots) {
						FasciaOraria fasciaOraria = convertiTimeSlotToFasciaOraria(timeSlot,
								asList(new ParameterTS[] {}), asList(new ParameterGen[] {}), asList(new Price[] {}));
						setDettaglio(timeSlot, client, fasciaOraria);
						fasceOrarie.add(fasciaOraria);
					}
				} catch (Exception e) {
					logger.error("errore nel recupero del dettaglio");
				}
				for (TransportServiceClass trServiceClass : transportSvcClassList) {
					List<Transport> transportsToBeDone = missionBuilderBean.getTrasportiDaEseguire(trServiceClass,
							richiesteDaMissione, date, roundCode);
					String operatoreLogistico = "";
					if (!transportsToBeDone.isEmpty())
						operatoreLogistico = transportsToBeDone.get(0).getOperatoreLogistico();
					Set<Vehicle> idleTrucks = missionBuilderBean.getVeicoliDisponibili(trServiceClass, roundCode,
							operatoreLogistico);
					for (Transport t : transportsToBeDone) {
						t.setFreightItems(freightFacade.findAll(t));
					}
					List<Richiesta> richieste = convertiTransportsToRichieste(transportsToBeDone);
					logger.debug("inizio costruzione della missione sgapl");
					while (!idleTrucks.isEmpty() && !richieste.isEmpty()) {
						Vehicle truck = idleTrucks.iterator().next();
						Missione missioneEntry = new Missione();
						Veicolo veicolo = convertiVehicleToVeicolo(truck);
						missioneEntry.setVeicolo(veicolo);
						missioneEntry.setRichieste(richieste);
						Map<Richiesta, FasciaOraria> richiestePerFasciaOraria = associaFasciaOrariaARichiesta(richieste,
								fasceOrarie, veicolo);
						Mission missione = new Mission();
						aggiungiValoriAMissione(missioneEntry, richiestePerFasciaOraria);
						Timestamp timestamp = new Timestamp(date.getTime());
						missionBuilderBean.buildSgaplMission(missioneEntry, missione, timestamp);
						missionBuilderBean.buildCityLogisticsMission(missioneEntry, missione, timestamp);

						logger.debug(format("->Assigned %s for \"%s\" service class\n", missione,
								trServiceClass.getDescription()));
						List<ValueMission> valuesMission = missione.getValuesMission();
						missione.setValuesMission(null);
						if (valuesMission != null)
							for (ValueMission vm : valuesMission) {
								vm.setMission(missione);
							}
						missione.setTimeSlot(tsc.findTimeSlot(missione.getTimeSlot().getIdTS()));
						missione.setName(new Random().nextInt(1000) + "");
						for (ExchangeStop exchangeStop : missione.getExchangeStops())
							exchangeStop.setId(new Long(new Random().nextInt(1000)));
						missioniCostruite.add(missione);
						result.add(missione);
						idleTrucks.remove(truck);
						HashSet<Richiesta> trSet = new HashSet<Richiesta>();
						for (Richiesta t : richieste) {
							if (t.getStato().equals(assigned.name())) {
								trSet.add(t);
							}
						}
						richieste.removeAll(trSet);
					}
				}
			}
		}
		List<Missione> missioni = convertiMissionsToMissioni(missioniCostruite);
		for (Missione missione : missioni) {
			List<Request> c = tsc.simulRank(missione.getDataInizio(), missione.getFasciaOraria().getId());
			if (c != null && c.size() > 0) {
				missione.setRanking(c.get(0).getColor());
				missione.setCreditoMobilita(c.get(0).getPrice());
			}
		}
		return missioni;
	}

	/**
	 * <p>
	 * Method: buildMission
	 * </p>
	 * <p>
	 * Description: Questo metodo costruisce le missioni
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/buildMission")
	@Produces(APPLICATION_JSON)
	public Mission buildMission(Missione missione) {
		Mission mission = new Mission();
		Timestamp dateTs = missionBean.puliziaMissioni(new Date());
		missionBean.buildSgaplMission(missione, mission, dateTs);
		missionBean.buildCityLogisticsMission(missione, mission, dateTs);
		List<ValueMission> valuesMission = mission.getValuesMission();
		mission.setValuesMission(null);
		if (valuesMission != null)
			for (ValueMission vm : valuesMission) {
				vm.setMission(mission);
				valueMissionFacade.create(vm);
			}
		missionFacade.create(mission);
		return mission;
	}

	/**
	 * <p>
	 * Method: addShipping
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge uno shipping order al sistema
	 * </p>
	 * 
	 * @param ShippingOrder
	 *            shippingOrder
	 * @return ShippingOrder
	 */
	@POST
	@Path("/addShipping")
	@Consumes(APPLICATION_JSON)
	public ShippingOrder addShipping(ShippingOrder shippingOrder) {
		sgotBean.addShipping(shippingOrder);
		return shippingOrder;
	}

	/**
	 * <p>
	 * Method: deleteShipping
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge uno shipping order al sistema
	 * </p>
	 * 
	 * @param ShippingOrder
	 *            shippingOrder
	 * @return ShippingOrder
	 */
	@POST
	@Path("/deleteShipping")
	@Consumes(APPLICATION_JSON)
	public ShippingOrder deleteShipping(ShippingOrder shippingOrder) {
		sgotBean.deleteShipping(shippingOrder);
		return shippingOrder;
	}

	/**
	 * <p>
	 * Method: addShippingItem
	 * </p>
	 * <p>
	 * Description: Questo metodo aggiunge uno shipping item al sistema
	 * </p>
	 * 
	 * @param ShippingOrder
	 *            shippingOrder
	 * @return ShippingOrder
	 */
	@POST
	@Path("/addShippingItem")
	@Consumes(APPLICATION_JSON)
	public ShippingItem addShippingItem(ShippingItem shippingItem) {
		sgotBean.addShippingItem(shippingItem);
		return shippingItem;
	}

	/**
	 * <p>
	 * Method: findTransportServiceClass
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i tipi di veicoli utilizzabili
	 * nelle missioni
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/findTransportServiceClass/{description}")
	@Produces(APPLICATION_JSON)
	public List<TransportServiceClass> findTransportServiceClass(@PathParam("description") String description) {
		return tscf.findBySelection(description);
	}
}