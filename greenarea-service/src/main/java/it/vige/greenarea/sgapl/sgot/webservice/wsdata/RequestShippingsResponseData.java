/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.sgapl.sgot.webservice.wsdata;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Administrator
 */
public class RequestShippingsResponseData implements Serializable {

	private static final long serialVersionUID = 6466184748534553365L;
	private ResultOperationResponse result;
	private List<ShippingOrderData> shippings;
	private String totalCost;
	private Long maxTimeShipment;
	private Long maxTimeValidity;

	public RequestShippingsResponseData() {
		result = null;
		shippings = null;
		totalCost = null;
		maxTimeShipment = null;
		maxTimeValidity = null;
	}

	public RequestShippingsResponseData(ResultOperationResponse result) {
		this();
		this.result = result;
	}

	public Long getMaxTimeShipment() {
		return maxTimeShipment;
	}

	public void setMaxTimeShipment(Long maxTimeShipment) {
		this.maxTimeShipment = maxTimeShipment;
	}

	public Long getMaxTimeValidity() {
		return maxTimeValidity;
	}

	public void setMaxTimeValidity(Long maxTimeValidity) {
		this.maxTimeValidity = maxTimeValidity;
	}

	public ResultOperationResponse getResult() {
		return result;
	}

	public void setResult(ResultOperationResponse result) {
		this.result = result;
	}

	public List<ShippingOrderData> getShippings() {
		return shippings;
	}

	public void setShippings(List<ShippingOrderData> shippings) {
		this.shippings = shippings;
	}

	public String getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(String totalCost) {
		this.totalCost = totalCost;
	}

}
