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
package it.vige.greenarea.ln.model;

import it.vige.greenarea.costmodels.ConstantCost;

import org.w3c.dom.Element;

/**
 *         rappresenta il Vettore che prende in carico un trasporto
 */
public class LNSimpleCarrier extends LNEdge {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1633069683678180248L;
	private String fromNodeName;
	private String toNodeName;
	private boolean biDirectional;

	public enum CarrierType {
		Driver, Airfreight, SeaFreight, RailFreight, DeliveryCo
	};

	// private static final String[] carrierTypes =
	// I18N.getStrings(CarrierType.values());
	private CarrierType type = CarrierType.Driver;

	public LNSimpleCarrier(String name) {
		this();
		super.setName(name);
	}

	public LNSimpleCarrier() {
		super();
		fromNodeName = "";
		toNodeName = "";
		biDirectional = false;
		super.setCostModel(ConstantCost.class.getSimpleName());
	}

	public CarrierType getType() {
		return type;
	}

	public boolean isBiDirectional() {
		return biDirectional;
	}

	public void setBiDirectional(boolean biDirectional) {
		this.biDirectional = biDirectional;
	}

	public String getFromNodeName() {
		return fromNodeName;
	}

	public void setFromNodeName(String fromNodeName) {
		this.fromNodeName = fromNodeName;
	}

	public void setToNodeName(String toNodeName) {
		this.toNodeName = toNodeName;
	}

	public String getToNodeName() {
		return toNodeName;
	}

	@Override
	public Element toElement() {
		Element edgeDescriptor = super.toElement();
		edgeDescriptor.setAttribute("biDirectional",
				Boolean.toString(biDirectional));
		return edgeDescriptor;
	}

	@Override
	public void loadElement(Element elt) {
		super.loadElement(elt);
		biDirectional = Boolean.parseBoolean(elt.getAttribute("biDirectional"));
	}

}
