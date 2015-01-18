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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import it.vige.greenarea.cl.control.TAPControl;
import it.vige.greenarea.cl.library.entities.TapGroupData;
import it.vige.greenarea.cl.library.entities.TapOutData;
import it.vige.greenarea.cl.library.entities.TapParamData;
import it.vige.greenarea.dto.AccessiInGA;
import it.vige.greenarea.dto.RichiestaAccesso;
import it.vige.greenarea.dto.RichiestaPosizioneVeicolo;
import it.vige.greenarea.gtg.db.demoData.InitDemoData;
import it.vige.greenarea.tap.facades.TapGroupDataFacade;
import it.vige.greenarea.tap.facades.TapOutDataFacade;
import it.vige.greenarea.tap.facades.TapParamDataFacade;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * <p>
 * Class: UserRESTService
 * </p>
 * <p>
 * Description: Classe che gestisce l'utilizzo parte utente
 * </p>
 * 
 */
@Path("/Tap")
@Stateless
public class TAPRESTService {

	@EJB
	private TapOutDataFacade tapOutDataFacade;
	@EJB
	private TapGroupDataFacade tapGroupDataFacade;
	@EJB
	private TapParamDataFacade tapParamDataFacade;
	@EJB
	private TAPControl tapControl;
	@Inject
	private InitDemoData initDemoData;

	/**
	 * <p>
	 * Method: getTapParamDatas
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i tap Params utilizzabili da un
	 * gruppo
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/getTapParamDatas")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<TapParamData> getTapParamDatas(TapGroupData tapGroupData) {
		return tapParamDataFacade.findAll(tapGroupData);
	}

	/**
	 * <p>
	 * Method: getTapOutDatas
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce i tap Outs utilizzabili con un vin
	 * specificato
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getTapOutDatas/{vin}")
	@Produces(APPLICATION_JSON)
	public List<TapOutData> getTapOutDatas(@PathParam("vin") String vin) {
		return tapOutDataFacade.findAll(vin);
	}

	/**
	 * <p>
	 * Method: getAllTapGroups
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutti i gruppi TAP
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getAllTapGroups")
	@Produces(APPLICATION_JSON)
	public List<TapGroupData> getAllTapGroups() {
		return tapGroupDataFacade.findAll();
	}

	/**
	 * <p>
	 * Method: getAllTapParams
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutti i parametri TAP
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getAllTapParams")
	@Produces(APPLICATION_JSON)
	public List<TapParamData> getAllTapParams() {
		return tapParamDataFacade.findAll();
	}

	/**
	 * <p>
	 * Method: getAllTapOuts
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutti i out TAP
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/getAllTapOuts")
	@Produces(APPLICATION_JSON)
	public List<TapOutData> getAllTapOuts() {
		return tapOutDataFacade.findAll();
	}

	/**
	 * <p>
	 * Method: veicoliInGA
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutti i out TAP
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/veicoliInGA")
	@Produces(APPLICATION_JSON)
	public int getVeicoliInGA() {
		return tapControl.getVeicoliInGA();
	}

	/**
	 * <p>
	 * Method: getLastPosition
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutti i out TAP
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/getLastPosition")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String richiediPosizioneVeicolo(
			RichiestaPosizioneVeicolo richiestaPosizioneVeicolo) {
		return tapControl.richiediPosizioneVeicolo(richiestaPosizioneVeicolo);
	}

	/**
	 * <p>
	 * Method: getAllTapOuts
	 * </p>
	 * <p>
	 * Description: Questo metodo restituisce tutti i out TAP
	 * </p>
	 * 
	 * @return
	 */
	@POST
	@Path("/storicoAccessiInGA")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Map<Date, AccessiInGA> getStoricoAccessiInGA(
			RichiestaAccesso richiestaAccesso) {
		return tapControl.getStoricoAccessiInGA(richiestaAccesso);
	}

	/**
	 * <p>
	 * Method: spostamento1
	 * </p>
	 * <p>
	 * Description: Esegue uno spostamento di un veicolo con dati precaricati
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/spostamento1")
	@Produces(APPLICATION_JSON)
	public void spostamento1() {
		initDemoData.spostamento1();
	}

	/**
	 * <p>
	 * Method: spostamento2
	 * </p>
	 * <p>
	 * Description: Esegue uno spostamento di un veicolo con dati precaricati
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/spostamento2")
	@Produces(APPLICATION_JSON)
	public void spostamento2() {
		initDemoData.spostamento2();
	}

	/**
	 * <p>
	 * Method: cancellaDatiTap
	 * </p>
	 * <p>
	 * Description: Ripulisce il db del tap
	 * </p>
	 * 
	 * @return
	 */
	@GET
	@Path("/cancellaDatiTap")
	@Produces(APPLICATION_JSON)
	public void cancellaDatiTap() {
		initDemoData.cancellaDatiTap();
	}
}
