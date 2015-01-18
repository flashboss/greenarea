/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.itseasy.lib.mqData;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class ShippingOrderDataResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long shippingOrderID;
    private double totalCost;
    private Long maxTimeShipment;
    private Long maxTimeValidity;

    public ShippingOrderDataResponse(Long shippingOrderID, double totalCost, Long maxTimeShipment, Long maxTimeValidity) {
        this.shippingOrderID = shippingOrderID;
        this.totalCost = totalCost;
        this.maxTimeShipment = maxTimeShipment;
        this.maxTimeValidity = maxTimeValidity;
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
    
    public Long getShippingOrderID() {
        return shippingOrderID;
    }

    public void setShippingOrderID(Long shippingOrderID) {
        this.shippingOrderID = shippingOrderID;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
