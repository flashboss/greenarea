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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceRef;

import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService;
import it.vige.greenarea.sgapl.sgot.webservice.SGOTadminService_Service;
import it.vige.greenarea.sgapl.sgot.webservice.TransportInfo;

@ManagedBean
@RequestScoped
public class TrackingOrderBean {

	@WebServiceRef(wsdlLocation = "http://localhost:8080/greenarea-service/SGOTadminService?wsdl")
	// @WebServiceRef(wsdlLocation =
	// "WEB-INF/wsdl/163.162.24.76/SGOTserver/SGOTadminService.wsdl")
	private SGOTadminService_Service service;
	private TransportInfo info;

	/**
	 * Creates a new instance of TrackingOrderBean
	 */
	public TrackingOrderBean() {
	}

	public String locateOrder(String currentOrderId) {
		info = null;
		try {
			// devo rimuovere il trasporto di transportList:
			info = getTransportInfo(currentOrderId);
		} catch (Exception ex) {
			FacesMessage msg;
			FacesContext context = FacesContext.getCurrentInstance();

			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Locate Ordine: ",
					"Impossibile localizzare Ordine " + currentOrderId);
			context.addMessage(null, msg);
			return "/OrderTrackErr";
		}
		if (info == null) {
			FacesMessage msg;
			FacesContext context = FacesContext.getCurrentInstance();
			msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Locate Ordine: ",
					"Impossibile localizzare Ordine " + currentOrderId);
			context.addMessage(null, msg);
			return "OrderTrackErr";
		}

		return "/OrderTracking";
	}

	private TransportInfo getTransportInfo(String trId) throws Exception {
		SGOTadminService sGOTadminService = service.getSGOTadminServicePort();
		return sGOTadminService.getTransportInfo(trId);
	}

	public TransportInfo getInfo() {
		return info;
	}

	public void setInfo(TransportInfo info) {
		this.info = info;
	}
}
