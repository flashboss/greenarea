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
package it.vige.greenarea.sgaplconsole.controllers;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.sgrl.webservices.GeoLocation;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting;
import it.vige.greenarea.sgrl.webservices.LogisticNetworkRouting_Service;
import it.vige.greenarea.sgrl.webservices.SGRLServiceException_Exception;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceRef;

import org.slf4j.Logger;

@ManagedBean
@SessionScoped
public class TestSGRLController implements Serializable {

	private static final long serialVersionUID = 8104967824978177875L;

	private Logger logger = getLogger(getClass());

	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-sgr/LogisticNetworkRouting?wsdl")
	private LogisticNetworkRouting_Service service;
	// @WebServiceRef(wsdlLocation =
	// "WEB-INF/wsdl/163.162.24.76/SGRLserver/LogisticNetworkRouting.wsdl")
	// private LogisticNetworkRouting_Service service;

	GeoLocation fromLoc;
	GeoLocation toLoc;

	/**
	 * Creates a new instance of NewOrderConrtoller
	 */
	public TestSGRLController() {
		fromLoc = new GeoLocation();
		toLoc = new GeoLocation();
	}

	public void resetForm() {
		fromLoc = new GeoLocation();
		toLoc = new GeoLocation();
	}

	public String testSGRL() {
		FacesMessage msg;
		FacesContext context = FacesContext.getCurrentInstance();
		List<String> routes = null;
		try {
			// invio richesta di calcolo catena logistica da fromLoc a toLoc....
			routes = getSGRLRoutes(fromLoc, toLoc, null);
		} catch (Exception ex) {
			logger.error("errore gat", ex);
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"SGRL exception ", ex.getMessage());
		}
		if ((routes == null) || (routes.isEmpty())) {
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Catena Logistica ", "*** nessun percorso disponibile ***");
		} else {
			StringBuilder sb = new StringBuilder();
			for (String node : routes) {
				sb.append(node).append(" >>> ");
			}
			msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Catena Logistica ", sb.toString());
		}
		context.addMessage(null, msg);
		return null;
	}

	public GeoLocation getFromLoc() {
		return fromLoc;
	}

	public void setFromLoc(GeoLocation fromLoc) {
		this.fromLoc = fromLoc;
	}

	public GeoLocation getToLoc() {
		return toLoc;
	}

	public void setToLoc(GeoLocation toLoc) {
		this.toLoc = toLoc;
	}

	private List<String> getSGRLRoutes(GeoLocation source,
			GeoLocation destination, String options)
			throws SGRLServiceException_Exception {
		// Note that the injected javax.xml.ws.Service reference as well as port
		// objects are not thread safe.
		// If the calling of port operations may lead to race condition some
		// synchronization is required.
		LogisticNetworkRouting logisticNetworkRouting = service
				.getLogisticNetworkRoutingPort();
		return logisticNetworkRouting.getRoutes(source, destination, options);
	}

}
