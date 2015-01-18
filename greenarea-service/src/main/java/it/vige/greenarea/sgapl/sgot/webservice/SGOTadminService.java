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
package it.vige.greenarea.sgapl.sgot.webservice;

import static it.vige.greenarea.gtg.constants.ConversioniGTG.convertShippingOrderToShippingOrderDetails;
import static it.vige.greenarea.gtg.constants.ConversioniGTG.convertTransportToTransportInfo;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.sgapl.sgot.business.exception.GATException;
import it.vige.greenarea.sgapl.sgot.facade.ShippingItemFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.ShippingOrderDetails;
import it.vige.greenarea.sgapl.sgot.webservice.wsdata.TransportInfo;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName = "SGOTadminService")
public class SGOTadminService {
	@EJB
	private TransportFacade transportFacade;
	@EJB
	private ShippingOrderFacade shippingOrderFacade;
	@EJB
	private ShippingItemFacade shippingItemFacade;

	@WebMethod(operationName = "showOrderDetails")
	public ShippingOrderDetails showOrderDetails(
			@WebParam(name = "id") String id) {

		return getShippingOrderDetails(id);
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "loadOrderIDS")
	public List<ShippingOrderDetails> loadOrderIDS() {
		return getShippingOrderList();
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "getTransportInfo")
	public TransportInfo getTransportInfo(@WebParam(name = "trId") String trId)
			throws GATException {
		if (trId == null) {
			throw new GATException(
					GATException.GATerrorCodes.UNKNOWN_TRANSPORT_ID,
					new ArrayList<String>());
		}
		Transport t = transportFacade.find(trId);
		TransportInfo ti = convertTransportToTransportInfo(t);
		return ti;
	}

	private ShippingOrderDetails getShippingOrderDetails(String shID) {
		if (shID == null) {
			return null;
		}
		ShippingOrder shippingOrder = shippingOrderFacade.find(shID);
		shippingOrder.setShippingItems(shippingItemFacade
				.findAll(shippingOrder));
		return convertShippingOrderToShippingOrderDetails(shippingOrder);
	}

	private List<ShippingOrderDetails> getShippingOrderList() {
		List<ShippingOrder> listSO = shippingOrderFacade.findAll();
		List<ShippingOrderDetails> listSD = new ArrayList<ShippingOrderDetails>();
		for (ShippingOrder so : listSO) {
			List<ShippingItem> shippingItems = shippingItemFacade.findAll(so);
			so.setShippingItems(shippingItems);
			listSD.add(convertShippingOrderToShippingOrderDetails(so));
		}
		return listSD;
	}
}
