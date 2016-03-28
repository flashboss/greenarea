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
package it.vige.greenarea.gtg.mission.servlet;

import static it.vige.greenarea.Constants.BASE_URI_TS;
import static it.vige.greenarea.Conversioni.convertiTimeSlotToFasciaOraria;
import static it.vige.greenarea.Conversioni.convertiTransportsToRichieste;
import static it.vige.greenarea.Conversioni.convertiVehicleToVeicolo;
import static it.vige.greenarea.Utilities.aggiungiValoriAMissione;
import static it.vige.greenarea.Utilities.associaFasciaOrariaARichiesta;
import static it.vige.greenarea.Utilities.setDettaglio;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.assigned;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.slf4j.Logger;

import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.TransportServiceClass;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.cl.sessions.ValueMissionFacade;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.gtg.db.facades.ExchangeStopFacade;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TransportServiceClassFacade;
import it.vige.greenarea.gtg.ejb.MissionBuilderBean;

public class BuildMissions extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1135140470951208680L;
	@EJB
	private ExchangeStopFacade exchangeStopFacade;
	@EJB
	private FreightFacade freightFacade;
	@EJB
	private MissionBuilderBean missionBuilderBean;
	@EJB
	private MissionFacade missionFacade;
	@EJB
	private ValueMissionFacade valueMissionFacade;
	@EJB
	private TransportServiceClassFacade transportServiceClassFacade;

	private Logger logger = getLogger(getClass());

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Date date = new Date();
		List<String> roundCodes = asList(new String[] { "01", "02", "06" });
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			/* TODO output your page here. You may use following sample code. */
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet BuildMissions</title>");
			out.println("</head>");
			out.println("<body>");
			for (String roundCode : roundCodes) {
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
					List<Transport> transportsToBeDone = missionBuilderBean.getTrasportiDaEseguire(trServiceClass, date,
							roundCode);
					String operatoreLogistico = "";
					if (!transportsToBeDone.isEmpty())
						operatoreLogistico = transportsToBeDone.get(0).getOperatoreLogistico();
					Set<Vehicle> idleTrucks = missionBuilderBean.getVeicoliDisponibili(trServiceClass, roundCode,
							operatoreLogistico);
					Map<Transport, List<Freight>> allTransports = freightFacade.findAll(transportsToBeDone);
					for (Transport t : transportsToBeDone) {
						t.setFreightItems(allTransports.get(t));
					}
					List<Richiesta> richieste = convertiTransportsToRichieste(transportsToBeDone);
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
						List<ValueMission> valuesMission = missione.getValuesMission();
						missione.setValuesMission(null);
						if (valuesMission != null)
							for (ValueMission vm : valuesMission) {
								vm.setMission(missione);
								valueMissionFacade.create(vm);
							}
						missionFacade.create(missione);
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
			out.println("<h1>Servlet BuildMissions at " + request.getContextPath() + " DONE!!!!</h1>");
			out.println("</body>");
			out.println("</html>");
		} finally {
			out.close();
		}
	}

	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
