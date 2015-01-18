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
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import static it.vige.greenarea.cl.library.entities.Transport.TransportState.unknown;
import it.vige.greenarea.cl.library.entities.Transport.TransportState;

import java.io.Serializable;

public class LocateShippingResponseData implements Serializable {

	private static final long serialVersionUID = 1L;
	private ResultOperationResponse result;

	private TransportState transportState;
	private String exchangeSiteName;
	private String address;

	public LocateShippingResponseData() {

		this.transportState = unknown;
		this.exchangeSiteName = null;
		this.address = null;
	}

	public LocateShippingResponseData(ResultOperationResponse result) {
		this.result = result;
		this.transportState = unknown;
		this.exchangeSiteName = null;
		this.address = null;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getExchangeSiteName() {
		return exchangeSiteName;
	}

	public void setExchangeSiteName(String exchangeSiteName) {
		this.exchangeSiteName = exchangeSiteName;
	}

	public TransportState getTransportState() {
		return transportState;
	}

	public void setTransportState(TransportState transportState) {
		this.transportState = transportState;
	}

	public ResultOperationResponse getResult() {
		return result;
	}

	public void setResult(ResultOperationResponse result) {
		this.result = result;
	}

}
