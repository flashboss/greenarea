/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.model;

import it.vige.greenarea.costmodels.ConstantCost;

import org.w3c.dom.Element;

/**
 * 
 * @author 00917308
 * 
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
