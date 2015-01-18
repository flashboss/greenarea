/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 * 
 * @author 00917377
 */
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
