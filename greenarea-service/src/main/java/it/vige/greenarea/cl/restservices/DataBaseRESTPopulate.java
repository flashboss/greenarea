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

import static it.vige.greenarea.Utilities.dMyyyy;
import static it.vige.greenarea.dto.AccessoVeicoli.NEGATO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_FISSO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_VARIABILE;
import static it.vige.greenarea.dto.AperturaRichieste._2_GIORNI_PRIMA;
import static it.vige.greenarea.dto.AperturaRichieste._3_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._12_ORE_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._1_GIORNO_PRIMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static it.vige.greenarea.dto.Peso.CRITICO;
import static it.vige.greenarea.dto.Peso.MEDIO;
import static it.vige.greenarea.dto.Ripetizione.TUTTI_I_GIORNI;
import static it.vige.greenarea.dto.TipoParametro.DA_DECIDERE;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_GLOBALE;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_LOCALE;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static it.vige.greenarea.dto.TipologiaParametro.COSTO;
import static it.vige.greenarea.dto.Tolleranza._20_PER_CENTO;
import static it.vige.greenarea.dto.Tolleranza._40_PER_CENTO;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import it.vige.greenarea.cl.control.TimeSlotControl;
import it.vige.greenarea.cl.control.UserControl;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.gtg.db.facades.MissionFacade;

/**
 * <p>
 * Class: DataBaseRESTPopulate
 * </p>
 * 
 * 
 */
@Path("/DataBase")
@Stateless
public class DataBaseRESTPopulate {

	@EJB
	private TimeSlotControl tsc;
	@EJB
	private UserControl uc;
	@EJB
	private MissionFacade mf;

	@GET
	@Path("/removeDB")
	@Produces(TEXT_PLAIN)
	public String removeDB() {

		TimeSlot ts1 = new TimeSlot();
		ts1.setTimeToAcceptRequest(_2_GIORNI_PRIMA);
		ts1.setTimeToStopRequest(_1_GIORNO_PRIMA);
		ts1.setTimeToRun(_1_GIORNO_PRIMA);
		ts1.setTollerance(_20_PER_CENTO);
		ts1.setStartTS("9:30");
		ts1.setFinishTS("11:30");
		ts1.setDayStart("1-01");
		ts1.setDayFinish("31-12");
		ts1.setVikInd(PREMIA_RISPOSTA_GLOBALE);
		ts1.setWmy(TUTTI_I_GIORNI);

		TimeSlot ts2 = new TimeSlot();
		ts2.setTimeToAcceptRequest(_3_GIORNI_PRIMA);
		ts2.setTimeToStopRequest(_1_GIORNO_PRIMA);
		ts2.setTimeToRun(_1_GIORNO_PRIMA);
		ts2.setTollerance(_40_PER_CENTO);
		ts2.setStartTS("11:30");
		ts2.setFinishTS("13:30");
		ts2.setDayStart("1-01");
		ts2.setDayFinish("31-12");
		ts2.setVikInd(PREMIA_RISPOSTA_GLOBALE);
		ts2.setWmy(TUTTI_I_GIORNI);

		TimeSlot ts3 = new TimeSlot();
		ts3.setTimeToAcceptRequest(_2_GIORNI_PRIMA);
		ts3.setTimeToStopRequest(_12_ORE_PRIMA);
		ts3.setTimeToRun(_1_GIORNO_PRIMA);
		ts3.setTollerance(_20_PER_CENTO);
		ts3.setWmy(TUTTI_I_GIORNI);
		ts3.setStartTS("9:30");
		ts3.setFinishTS("11:30");
		ts3.setDayStart("1-01");
		ts3.setDayFinish("31-12");
		ts3.setVikInd(PREMIA_RISPOSTA_LOCALE);
		ts3.setWmy(TUTTI_I_GIORNI);

		tsc.deleteSlotTime(tsc.findTimeSlot(ts1));
		tsc.deleteSlotTime(tsc.findTimeSlot(ts2));
		tsc.deleteSlotTime(tsc.findTimeSlot(ts3));
		ParameterGen pg1 = new ParameterGen();
		pg1.setMeasureUnit("m");
		pg1.setNamePG("lunghezza");
		pg1.setTypePG(COSTO);
		pg1.setUseType(true);
		pg1.setDescription("Lunghezza veicolo per libretto");
		ParameterGen pg2 = new ParameterGen();
		pg2.setMeasureUnit("Numero");
		pg2.setNamePG("carico");
		pg2.setTypePG(BENEFICIO);
		pg2.setUseType(true);
		pg2.setDescription("Rapporto da bolla con accompagnamento");
		ParameterGen pg3 = new ParameterGen();
		pg3.setMeasureUnit("Numero");
		pg3.setNamePG("tappe");
		pg3.setTypePG(BENEFICIO);
		pg3.setUseType(true);
		pg3.setDescription("Tappe sul ciclo di consegna");
		ParameterGen pg4 = new ParameterGen();
		pg4.setMeasureUnit("kg");
		pg4.setNamePG("peso");
		pg4.setTypePG(COSTO);
		pg4.setUseType(true);
		pg4.setDescription("Peso del veicolo nel libretto");
		ParameterGen pg5 = new ParameterGen();
		pg5.setMeasureUnit("");
		pg5.setNamePG("euro");
		pg5.setTypePG(COSTO);
		pg5.setUseType(true);
		pg5.setDescription("Categoria Euro con libretto");
		tsc.deleteParameterGen(tsc.findParameterGen(pg1).get(0));
		tsc.deleteParameterGen(tsc.findParameterGen(pg2).get(0));
		tsc.deleteParameterGen(tsc.findParameterGen(pg3).get(0));
		tsc.deleteParameterGen(tsc.findParameterGen(pg4).get(0));
		tsc.deleteParameterGen(tsc.findParameterGen(pg5).get(0));
		return "ok";
	}

	@GET
	@Path("/populateDB")
	@Produces(TEXT_PLAIN)
	public String pupulateDB() {
		// Creo una fascia oraria

		int[] idTs = new int[3];
		TimeSlot st = tsc.addSlotTime(_2_GIORNI_PRIMA, _1_GIORNO_PRIMA, _1_GIORNO_PRIMA, _20_PER_CENTO, TUTTI_I_GIORNI,
				"9:30", "11:30", "1-01", "31-12", PREMIA_RISPOSTA_GLOBALE);
		idTs[0] = st.getIdTS();
		TimeSlot st1 = tsc.addSlotTime(_3_GIORNI_PRIMA, _1_GIORNO_PRIMA, _1_GIORNO_PRIMA, _40_PER_CENTO, TUTTI_I_GIORNI,
				"11:30", "13:30", "1-01", "31-12", PREMIA_RISPOSTA_GLOBALE);
		tsc.addSlotTime(_2_GIORNI_PRIMA, _12_ORE_PRIMA, _1_GIORNO_PRIMA, _20_PER_CENTO, TUTTI_I_GIORNI, "9:30", "11:30",
				"1-01", "31-12", PREMIA_RISPOSTA_LOCALE);
		List<TimeSlot> timeSlots = tsc.findAllTimeSlots();
		for (TimeSlot timeSlot : timeSlots) {
			System.out.println("timeSlot id = " + timeSlot.getIdTS());
			System.out.println("timeSlot getDayFinish = " + timeSlot.getDayFinish());
			System.out.println("timeSlot getDayStart = " + timeSlot.getDayStart());
			System.out.println("timeSlot getStartTS = " + timeSlot.getStartTS());
			System.out.println("timeSlot getTimeToAcceptRequest = " + timeSlot.getTimeToAcceptRequest());
			System.out.println("timeSlot getTimeToStopRequest = " + timeSlot.getTimeToStopRequest());
		}
		idTs[1] = st1.getIdTS();
		ParameterGen pg;
		int[] idAr = new int[5];
		// Creo 6 parametri generali
		pg = tsc.addParameterGen("lunghezza", COSTO, "m", true, "Lunghezza veicolo per libretto");

		idAr[0] = pg.getId();
		pg = tsc.addParameterGen("carico", BENEFICIO, "Numero", true, "Rapporto da bolla con accompagnamento");
		idAr[1] = pg.getId();

		pg = tsc.addParameterGen("tappe", BENEFICIO, "Numero", true, "Tappe sul ciclo di consegna");
		idAr[2] = pg.getId();

		pg = tsc.addParameterGen("peso", COSTO, "kg", true, "Peso del veicolo nel libretto");
		idAr[3] = pg.getId();

		pg = tsc.addParameterGen("euro", COSTO, "", true, "Categoria Euro con libretto");
		idAr[4] = pg.getId();

		// Configuro i parametri
		ParameterTS pts = new ParameterTS();
		// Lunghezza
		pts.setParGen(new ParameterGen(idAr[0]));
		pts.setTs(new TimeSlot(idTs[0]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(7.5);
		pts.setMinValue(0);
		pts.setWeight(CRITICO);
		tsc.configParameterToTimeSlot(pts);
		// % carico
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[1]));
		pts.setTs(new TimeSlot(idTs[0]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(100);
		pts.setMinValue(25);
		pts.setWeight(CRITICO);
		tsc.configParameterToTimeSlot(pts);
		// tappe
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[2]));
		pts.setTs(new TimeSlot(idTs[0]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(35);
		pts.setMinValue(5);
		pts.setWeight(MEDIO);
		tsc.configParameterToTimeSlot(pts);
		// peso
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[3]));
		pts.setTs(new TimeSlot(idTs[0]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(3600);
		pts.setMinValue(0);
		pts.setWeight(MEDIO);
		tsc.configParameterToTimeSlot(pts);
		// euro
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[4]));
		pts.setTs(new TimeSlot(idTs[0]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(3.5);
		pts.setMinValue(0);
		pts.setWeight(CRITICO);
		tsc.configParameterToTimeSlot(pts);

		Price p = new Price();
		p.setColor(VERDE);
		p.setFixPrice(2);
		p.setTypeEntry(PREZZO_FISSO);
		p.setTs(new TimeSlot(idTs[0]));
		tsc.addPriceToTimeSlot(p);

		p = new Price();
		p.setColor(GIALLO);
		p.setMaxPrice(8);
		p.setMinPrice(4);
		p.setTypeEntry(PREZZO_VARIABILE);
		p.setTs(new TimeSlot(idTs[0]));
		tsc.addPriceToTimeSlot(p);

		p = new Price();
		p.setColor(ROSSO);
		p.setFixPrice(200);
		p.setTypeEntry(NEGATO);
		p.setTs(new TimeSlot(idTs[0]));
		tsc.addPriceToTimeSlot(p);

		pts = new ParameterTS();
		// Lunghezza
		pts.setParGen(new ParameterGen(idAr[0]));
		pts.setTs(new TimeSlot(idTs[1]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(7.5);
		pts.setMinValue(0);
		pts.setWeight(CRITICO);
		tsc.configParameterToTimeSlot(pts);
		// % carico
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[1]));
		pts.setTs(new TimeSlot(idTs[1]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(100);
		pts.setMinValue(25);
		pts.setWeight(CRITICO);
		tsc.configParameterToTimeSlot(pts);
		// tappe
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[2]));
		pts.setTs(new TimeSlot(idTs[1]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(35);
		pts.setMinValue(5);
		pts.setWeight(MEDIO);
		tsc.configParameterToTimeSlot(pts);
		// peso
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[3]));
		pts.setTs(new TimeSlot(idTs[1]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(3600);
		pts.setMinValue(0);
		pts.setWeight(MEDIO);
		tsc.configParameterToTimeSlot(pts);
		// euro
		pts = new ParameterTS();
		pts.setParGen(new ParameterGen(idAr[4]));
		pts.setTs(new TimeSlot(idTs[1]));
		pts.setTypePar(DA_DECIDERE);
		pts.setMaxValue(3.5);
		pts.setMinValue(0);
		pts.setWeight(CRITICO);
		tsc.configParameterToTimeSlot(pts);

		p = new Price();
		p.setColor(VERDE);
		p.setFixPrice(2);
		p.setTypeEntry(PREZZO_FISSO);
		p.setTs(new TimeSlot(idTs[1]));
		tsc.addPriceToTimeSlot(p);

		p = new Price();
		p.setColor(GIALLO);
		p.setMaxPrice(8);
		p.setMinPrice(4);
		p.setTypeEntry(PREZZO_VARIABILE);
		p.setTs(new TimeSlot(idTs[1]));
		tsc.addPriceToTimeSlot(p);

		p = new Price();
		p.setColor(ROSSO);
		p.setFixPrice(200);
		p.setTypeEntry(NEGATO);
		p.setTs(new TimeSlot(idTs[1]));
		tsc.addPriceToTimeSlot(p);

		return "ok";
	}

	@GET
	@Path("/hello")
	@Produces(TEXT_PLAIN)
	public String retHello() {
		return "Hello!";
	}

	@GET
	@Path("/addMission")
	@Produces(TEXT_PLAIN)
	public String addMission() throws ParseException {

		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setTimeToAcceptRequest(_2_GIORNI_PRIMA);
		timeSlot.setTimeToStopRequest(_12_ORE_PRIMA);
		timeSlot.setTimeToRun(_1_GIORNO_PRIMA);
		timeSlot.setTollerance(_20_PER_CENTO);
		timeSlot.setWmy(TUTTI_I_GIORNI);
		timeSlot.setStartTS("9:30");
		timeSlot.setFinishTS("11:30");
		timeSlot.setDayStart("1-01");
		timeSlot.setDayFinish("31-12");
		timeSlot.setVikInd(PREMIA_RISPOSTA_LOCALE);
		timeSlot = tsc.findTimeSlot(timeSlot);

		Mission m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Mario");
		ValueMission valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(5);
		ValueMission valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(45);
		ValueMission valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		ValueMission valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(3560);
		ValueMission valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(0.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("555MK"));
		m.setCompany("dhl");
		m.setName("Carlo");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(8);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1000);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Giorgio");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(4);
		valueMission1.setValuePar(7);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(25);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(3500);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Gianfranco");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(8);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Silvio");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(6);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(3500);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Domenico");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(9);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(25);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Fabrizio");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(5);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Francesco");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(4);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Yuri");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(7);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Chiara");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(20);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		uc.addMission(m);
		return "Mission added";
	}

	@GET
	@Path("/removeMission/")
	@Produces(TEXT_PLAIN)
	public String removeMission() throws ParseException {

		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setTimeToAcceptRequest(_2_GIORNI_PRIMA);
		timeSlot.setTimeToStopRequest(_12_ORE_PRIMA);
		timeSlot.setTimeToRun(_1_GIORNO_PRIMA);
		timeSlot.setTollerance(_20_PER_CENTO);
		timeSlot.setWmy(TUTTI_I_GIORNI);
		timeSlot.setStartTS("9:30");
		timeSlot.setFinishTS("11:30");
		timeSlot.setDayStart("1-01");
		timeSlot.setDayFinish("31-12");
		timeSlot.setVikInd(PREMIA_RISPOSTA_LOCALE);
		timeSlot = tsc.findTimeSlot(timeSlot);

		Mission m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Mario");
		ValueMission valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(5);
		ValueMission valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(45);
		ValueMission valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		ValueMission valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(3560);
		ValueMission valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(0.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("555MK"));
		m.setCompany("dhl");
		m.setName("Carlo");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(8);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1000);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Giorgio");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(4);
		valueMission1.setValuePar(7);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(25);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(3500);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Gianfranco");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(8);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Silvio");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(6);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(3500);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Domenico");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(9);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(25);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Fabrizio");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(5);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Francesco");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(4);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Yuri");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(7);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		m = new Mission();
		m.setTimeSlot(timeSlot);
		m.setStartTime(new Timestamp(dMyyyy.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("44GU4"));
		m.setCompany("dhl");
		m.setName("Chiara");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(20);
		valueMission2 = new ValueMission();
		valueMission2.setIdParameter(4);
		valueMission2.setValuePar(60);
		valueMission3 = new ValueMission();
		valueMission3.setIdParameter(5);
		valueMission3.setValuePar(12);
		valueMission4 = new ValueMission();
		valueMission4.setIdParameter(6);
		valueMission4.setValuePar(1200);
		valueMission5 = new ValueMission();
		valueMission5.setIdParameter(7);
		valueMission5.setValuePar(1.6);
		m.setValuesMission(new ArrayList<ValueMission>(asList(
				new ValueMission[] { valueMission1, valueMission2, valueMission3, valueMission4, valueMission5 })));
		mf.remove(mf.findMission(m));
		return "Mission removed";
	}
}
