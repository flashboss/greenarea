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
package it.vige.greenarea.cl.admin.bean;

import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.RequestParameter;
import it.vige.greenarea.dto.Sched;
import it.vige.greenarea.cl.admin.entity.RequestView;
import it.vige.greenarea.cl.admin.rest.TimeSlotRestClient;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Class: DaProcessareBean
 * </P>
 * <p>
 * Description: Contiene tutti i meotodi Relativi alla pagina web relativa alle
 * Fasce Orarie da Processare
 * </P>
 */

public class DaProcessareBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4451851428174847011L;
	private TimeSlotRestClient rcDaProcessareBean = new TimeSlotRestClient();
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private ArrayList<RequestView> listaDaProcessare_RichiesteView;
	private List<Request> listaDaProcessare_Simulazione;
	private List<Sched> listaAllDaProcessare;
	private List<Request> listaDaProcessare_Richieste;
	private List<RequestParameter> listaDaProcessare_Richieste_Dettaglio;
	private Request richiestaTrovata;

	public DaProcessareBean() {
	}

	/**
	 * <p>
	 * Method: getDaProcessare()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista di Richieste da Processare
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Sched> getDaProcessare() throws Exception {
		listaAllDaProcessare = new ArrayList<Sched>();
		listaAllDaProcessare = rcDaProcessareBean.getAllSchedules();
		return listaAllDaProcessare;
	}

	/**
	 * <p>
	 * Method: daProcessare_BottoneRichieste(String data, String IdTs)
	 * </P>
	 * <p>
	 * Description: Viene eseguita quando clicco il bottone Richieste dalla
	 * pagina DaProcessare e crea la lista di richieste associate alla fascia
	 * oraria
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public ArrayList<RequestView> daProcessare_BottoneRichieste(String data,
			String IdTs) throws Exception {

		String[] soloData = data.split(" ");
		Date dataNuova = dateFormat.parse(soloData[0]);
		TimeSlotRestClient rcDaProcessare_Richieste = new TimeSlotRestClient();
		listaDaProcessare_Richieste = rcDaProcessare_Richieste.selectRequests(
				dataNuova, IdTs, "5");
		listaDaProcessare_RichiesteView = new ArrayList<RequestView>();
		for (int i = 0; i < listaDaProcessare_Richieste.size(); i++) {
			RequestView requestView = new RequestView();
			requestView.setCarPlate(listaDaProcessare_Richieste.get(i)
					.getCarPlate());
			requestView.setColor(listaDaProcessare_Richieste.get(i).getColor());
			requestView.setCompany(listaDaProcessare_Richieste.get(i)
					.getCompany());
			requestView.setDateMiss(listaDaProcessare_Richieste.get(i)
					.getDateMiss());
			requestView.setIdMission(listaDaProcessare_Richieste.get(i)
					.getIdMission());
			requestView.setIdTimeSlot(listaDaProcessare_Richieste.get(i)
					.getIdTimeSlot());
			requestView.setPrice(listaDaProcessare_Richieste.get(i).getPrice());
			requestView.setQu(listaDaProcessare_Richieste.get(i).getQu());
			requestView.setReqParList(listaDaProcessare_Richieste.get(i)
					.getReqParList());
			requestView.setUserName(listaDaProcessare_Richieste.get(i)
					.getUserName());
			listaDaProcessare_RichiesteView.add(requestView);
		}
		return listaDaProcessare_RichiesteView;
	}

	/**
	 * <p>
	 * Method: daProcessare_Richieste()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista delle Richieste per una certa fascia oraria
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public ArrayList<RequestView> daProcessare_Richieste() {
		return listaDaProcessare_RichiesteView;
	}

	/**
	 * <p>
	 * Method: daProcessare_BottoneSimulazione(String data, String IdTs)
	 * </P>
	 * <p>
	 * Description: Viene eseguita quando clicco il bottone Simulazione dalla
	 * pagina DaProcessare e simula la schedulazione delle richieste per una
	 * certa fascia oraria
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Request> daProcessare_BottoneSimulazione(String data,
			String IdTs) throws Exception {

		String[] soloData = data.split(" ");
		Date dataNuova = dateFormat.parse(soloData[0]);
		TimeSlotRestClient rcDaProcessare_Richieste = new TimeSlotRestClient();
		listaDaProcessare_Simulazione = rcDaProcessare_Richieste.simulRank(
				IdTs, dataNuova);
		return listaDaProcessare_Simulazione;
	}

	/**
	 * <p>
	 * Method: daProcessare_Simulazione()
	 * </P>
	 * <p>
	 * Description: Ritorna il risultato della simulazione
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Request> daProcessare_Simulazione() {
		return listaDaProcessare_Simulazione;
	}

	/**
	 * <p>
	 * Method: daProcessare_Richieste_BottoneDettaglio(int idMission)
	 * </P>
	 * <p>
	 * Description: Viene eseguita quando clicco il bottone Dettaglio dalla
	 * pagina DaProcessare_Richieste
	 * </P>
	 * 
	 * @param
	 * @return
	 */

	public List<RequestParameter> daProcessare_Richieste_BottoneDettaglio(
			int idMission) {
		listaDaProcessare_Richieste_Dettaglio = new ArrayList<RequestParameter>();
		richiestaTrovata = new Request();
		for (int i = 0; i < listaDaProcessare_Richieste.size(); i++) {
			if (listaDaProcessare_Richieste.get(i).getIdMission() == idMission) {
				richiestaTrovata = listaDaProcessare_Richieste.get(i);
			}
		}
		for (int i = 0; i < richiestaTrovata.getReqParList().size(); i++) {
			listaDaProcessare_Richieste_Dettaglio.add(richiestaTrovata
					.getReqParList().get(i));
		}
		return listaDaProcessare_Richieste_Dettaglio;
	}

	/**
	 * <p>
	 * Method: daProcessare_Richieste_Dettaglio()
	 * </P>
	 * <p>
	 * Description: Ritorna il Dettaglio di una certa Richiesta
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<RequestParameter> daProcessare_Richieste_Dettaglio() {
		return listaDaProcessare_Richieste_Dettaglio;
	}

}
