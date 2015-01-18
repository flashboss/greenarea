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
package it.vige.greenarea.itseasy.lib.mqData;

import java.io.Serializable;

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
