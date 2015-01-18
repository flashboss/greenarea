/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import static it.vige.greenarea.cl.library.entities.Transport.TransportState.unknown;
import it.vige.greenarea.cl.library.entities.Transport.TransportState;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 */
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
