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
package it.vige.greenarea.bpm.pa.gestiscifasceorarie;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiFasciaOrariaToTimeSlot;
import static it.vige.greenarea.Conversioni.convertiParametriToParameterTSs;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Parametro;
import it.vige.greenarea.dto.Prezzo;

public class ModificaFasciaOraria extends EmptyModificaFasciaOraria {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		boolean error = false;
		try {
			super.execute(execution);
			logger.info("CDI Modifica Fascia Oraria");
			FasciaOraria fasciaOraria = (FasciaOraria) execution.getVariable("fasciaoraria");
			Prezzo giallo = (Prezzo) execution.getVariable("giallo");
			Prezzo rosso = (Prezzo) execution.getVariable("rosso");
			Prezzo verde = (Prezzo) execution.getVariable("verde");
			@SuppressWarnings("unchecked")
			List<Parametro> parametri = (List<Parametro>) execution.getVariable("parametriaggiunti");
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TS + "/updateTimeSlot").request(APPLICATION_JSON);
			TimeSlot rsTimeSlot = bldr.post(entity(convertiFasciaOrariaToTimeSlot(fasciaOraria), APPLICATION_JSON),
					TimeSlot.class);
			if (rsTimeSlot == null)
				error = true;
			List<ParameterTS> parameterTss = convertiParametriToParameterTSs(parametri, null);
			bldr = client.target(BASE_URI_TS + "/removeParametersTS").request(APPLICATION_JSON);
			bldr.post(entity(rsTimeSlot, APPLICATION_JSON), TimeSlot.class);
			bldr = client.target(BASE_URI_TS + "/configParameterTS").request(APPLICATION_JSON);
			for (ParameterTS parameterTS : parameterTss) {
				parameterTS.setTs(rsTimeSlot);
				ParameterTS result = bldr.post(entity(parameterTS, APPLICATION_JSON), ParameterTS.class);
				if (result == null)
					error = true;
			}
			Price priceGreen = new Price();
			priceGreen.setColor(VERDE);
			Object accessoVeicoliVerdi = verde.getTypeEntry();
			if (accessoVeicoliVerdi != null)
				priceGreen.setTypeEntry(AccessoVeicoli.valueOf(accessoVeicoliVerdi + ""));
			Object prezzoFissoVerdi = verde.getFixPrice();
			if (prezzoFissoVerdi != null)
				priceGreen.setFixPrice((double) prezzoFissoVerdi);
			priceGreen.setTs(rsTimeSlot);
			Price priceRed = new Price();
			priceRed.setColor(ROSSO);
			Object accessoVeicoliRossi = rosso.getTypeEntry();
			if (accessoVeicoliRossi != null)
				priceRed.setTypeEntry(AccessoVeicoli.valueOf(accessoVeicoliRossi + ""));
			Object prezzoFissoRossi = rosso.getFixPrice();
			if (prezzoFissoRossi != null)
				priceRed.setFixPrice((double) prezzoFissoRossi);
			Object prezzoMassimoRossi = rosso.getMaxPrice();
			if (prezzoMassimoRossi != null)
				priceRed.setMaxPrice((double) prezzoMassimoRossi);
			Object prezzoMinimoRossi = rosso.getMinPrice();
			if (prezzoMinimoRossi != null)
				priceRed.setMinPrice((double) prezzoMinimoRossi);
			priceRed.setTs(rsTimeSlot);
			Price priceYellow = new Price();
			priceYellow.setColor(GIALLO);
			Object accessoVeicoliGialli = giallo.getTypeEntry();
			if (accessoVeicoliGialli != null)
				priceYellow.setTypeEntry(AccessoVeicoli.valueOf(accessoVeicoliGialli + ""));
			Object prezzoMassimoGialli = giallo.getMaxPrice();
			if (prezzoMassimoGialli != null)
				priceYellow.setMaxPrice((double) prezzoMassimoGialli);
			Object prezzoMinimoGialli = giallo.getMinPrice();
			if (prezzoMinimoGialli != null)
				priceYellow.setMinPrice((double) prezzoMinimoGialli);
			priceYellow.setTs(rsTimeSlot);
			bldr = client.target(BASE_URI_TS + "/updatePrice").request(APPLICATION_JSON);
			Price rsPriceYellow = bldr.post(entity(priceYellow, APPLICATION_JSON), Price.class);
			if (rsPriceYellow == null)
				error = true;
			Price rsPriceRed = bldr.post(entity(priceRed, APPLICATION_JSON), Price.class);
			if (rsPriceRed == null)
				error = true;
			Price rsPriceGreen = bldr.post(entity(priceGreen, APPLICATION_JSON), Price.class);
			if (rsPriceGreen == null)
				error = true;

		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroremodificafasciaoraria");
		}
		if (error) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERRORELIEVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroremodificafasciaoraria");
		}
	}
}
