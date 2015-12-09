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

import java.io.Serializable;

import it.vige.greenarea.cl.library.entities.OrderStatus.StateValue;

public class GetShippingStatusResponseData implements Serializable {

	private static final long serialVersionUID = -3393353730359979255L;
	private ResultOperationResponse result;
	private StateValue shippingStatus;

	public GetShippingStatusResponseData() {
	}

	public GetShippingStatusResponseData(ResultOperationResponse result) {
		this.result = result;
		this.shippingStatus = StateValue.unknown;
	}

	public ResultOperationResponse getResult() {
		return result;
	}

	public void setResult(ResultOperationResponse result) {
		this.result = result;
	}

	public StateValue getShippingStatus() {
		return shippingStatus;
	}

	public void setShippingStatus(StateValue shippingStatus) {
		this.shippingStatus = shippingStatus;
	}
}
