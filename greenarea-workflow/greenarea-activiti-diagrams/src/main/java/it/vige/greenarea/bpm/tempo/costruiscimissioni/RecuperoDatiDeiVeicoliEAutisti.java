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
package it.vige.greenarea.bpm.tempo.costruiscimissioni;

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static it.vige.greenarea.bpm.risultato.Categoria.ERROREGRAVE;
import static it.vige.greenarea.bpm.risultato.Categoria.ERRORELIEVE;
import static it.vige.greenarea.bpm.risultato.Tipo.ERROREDATIMANCANTI;
import static it.vige.greenarea.bpm.risultato.Tipo.ERRORESISTEMA;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;

import it.vige.greenarea.bpm.risultato.Messaggio;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.ValoriVeicolo;
import it.vige.greenarea.dto.Veicolo;

public class RecuperoDatiDeiVeicoliEAutisti extends EmptyRecuperoDatiDeiVeicoliEAutisti {

	private Logger logger = getLogger(getClass());

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			super.execute(execution);
			logger.info("CDI Recupero Dati dei Veicoli e Autisti");
			IdentityService identityService = execution.getEngineServices().getIdentityService();
			Client client = newClient();
			@SuppressWarnings("unchecked")
			List<Veicolo> veicoli = (List<Veicolo>) execution.getVariable("veicoli");
			OperatoreLogistico operatoreLogistico = (OperatoreLogistico) execution.getVariable("operatorelogistico");
			try {
				Builder bldr = client.target(BASE_URI_USER + "/getVehiclesForOP/" + operatoreLogistico.getId())
						.request(APPLICATION_JSON);
				List<Vehicle> response = bldr.get(new GenericType<List<Vehicle>>() {
				});
				if (response != null && response.size() > 0) {
					for (Vehicle vehicle : response) {
						User userAutista = identityService.createUserQuery().userId(vehicle.getAutista())
								.singleResult();
						GreenareaUser autista = convertToGreenareaUser(userAutista);
						User userSocietaDiTrasporto = identityService.createUserQuery()
								.userId(vehicle.getSocietaDiTrasporto()).singleResult();
						GreenareaUser societaDiTrasporto = convertToGreenareaUser(userSocietaDiTrasporto);
						ValoriVeicolo parametri = new ValoriVeicolo(vehicle.getServiceClass());
						veicoli.add(new Veicolo(vehicle.getState().name(), vehicle.getPlateNumber(), autista,
								societaDiTrasporto, operatoreLogistico, parametri));
					}
				} else {
					Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
					messaggio.setCategoria(ERRORELIEVE);
					messaggio.setTipo(ERROREDATIMANCANTI);
				}
				logger.info(response + "");
			} catch (Exception ex) {
				Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
				messaggio.setCategoria(ERRORELIEVE);
				messaggio.setTipo(ERRORESISTEMA);
			}
		} catch (Exception ex) {
			Messaggio messaggio = (Messaggio) execution.getVariable("messaggio");
			messaggio.setCategoria(ERROREGRAVE);
			messaggio.setTipo(ERRORESISTEMA);
			throw new BpmnError("erroregraverecuperoautistiveicoli");
		}
	}

}
