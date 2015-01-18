/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import it.vige.greenarea.cl.library.entities.OrderStatus.StateValue;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 */
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
