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

import static it.vige.greenarea.dto.AccessoVeicoli.NEGATO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_FISSO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_VARIABILE;
import static it.vige.greenarea.dto.AperturaRichieste._2_GIORNI_PRIMA;
import static it.vige.greenarea.dto.AperturaRichieste._3_GIORNI_PRIMA;
import static it.vige.greenarea.dto.ChiusuraRichieste._1_GIORNO_PRIMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static it.vige.greenarea.dto.Peso.CRITICO;
import static it.vige.greenarea.dto.Peso.MEDIO;
import static it.vige.greenarea.dto.Ripetizione.TUTTI_I_GIORNI;
import static it.vige.greenarea.dto.TipoParametro.DA_DECIDERE;
import static it.vige.greenarea.dto.TipologiaClassifica.PREMIA_RISPOSTA_GLOBALE;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static it.vige.greenarea.dto.TipologiaParametro.COSTO;
import static it.vige.greenarea.dto.Tolleranza._20_PER_CENTO;
import static it.vige.greenarea.dto.Tolleranza._40_PER_CENTO;
import static java.util.Arrays.asList;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import it.vige.greenarea.cl.control.TimeSlotControl;
import it.vige.greenarea.cl.control.UserControl;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;

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
	DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

	@EJB
	private TimeSlotControl tsc;
	@EJB
	private UserControl uc;

	@GET
	@Path("/populateDB")
	@Produces(MediaType.TEXT_PLAIN)
	public String pupulateDB() {
		// Creo una fascia oraria

		int[] idTs = new int[3];
		TimeSlot st = tsc.addSlotTime(_2_GIORNI_PRIMA, _1_GIORNO_PRIMA, _1_GIORNO_PRIMA, _20_PER_CENTO, TUTTI_I_GIORNI,
				"9:30", "11:30", "1-01", "31-12", PREMIA_RISPOSTA_GLOBALE);
		idTs[0] = st.getIdTS();
		TimeSlot st1 = tsc.addSlotTime(_3_GIORNI_PRIMA, _1_GIORNO_PRIMA, _1_GIORNO_PRIMA, _40_PER_CENTO, TUTTI_I_GIORNI,
				"11:30", "13:30", "1-01", "31-12", PREMIA_RISPOSTA_GLOBALE);
		idTs[1] = st1.getIdTS();
		ParameterGen pg;
		int[] idAr = new int[5];
		// Creo 6 parametri generali
		pg = tsc.addParameterGen("lunghezza", COSTO, "m", true, "Lunghezza veicolo da libretto");

		idAr[0] = pg.getId();
		pg = tsc.addParameterGen("carico", BENEFICIO, "Numero", true, "Rapporto da bolla di accompagnamento");
		idAr[1] = pg.getId();

		pg = tsc.addParameterGen("tappe", BENEFICIO, "Numero", true, "Tappe nel ciclo di consegna");
		idAr[2] = pg.getId();

		pg = tsc.addParameterGen("peso", COSTO, "kg", true, "Peso del veicolo da libretto");
		idAr[3] = pg.getId();

		pg = tsc.addParameterGen("euro", COSTO, "", true, "Categoria Euro da libretto");
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
	@Produces(MediaType.TEXT_PLAIN)
	public String retHello() {
		return "Hello!";
	}

	@GET
	@Path("/addMission")
	@Produces(MediaType.TEXT_PLAIN)
	public String addMission() throws ParseException {
		Mission m = new Mission();
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("Mario Consegne");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX123DE"));
		m.setCompany("ElisFast");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("Vachecisiamo");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
		m.setName("Domenico");
		valueMission1 = new ValueMission();
		valueMission1.setIdParameter(3);
		valueMission1.setValuePar(8);
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
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
		m.setTimeSlot(new TimeSlot(101));
		m.setStartTime(new Timestamp(dateFormat.parse("2/3/2012").getTime()));
		m.setTruck(new Vehicle("XX000ZZ"));
		m.setCompany("ElisFast");
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
}
