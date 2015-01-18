/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.ln.model;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

import org.w3c.dom.Element;

/**
 * 
 * @author 00917308
 */
public abstract class LNNode extends LNCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6655883149700094014L;

	public enum Status {
		Enabled, Disabled
	};

	public enum Cathegory {
		UNDEFINED, STORE, TERMINAL, DISTRIBUTED
	};

	public enum Domain {
		UNDEFINED, PUBLIC, PRIVATE
	};

	private Status status;
	private Cathegory cathegory;
	private Domain domain;
	private String description;

	public Cathegory getCathegory() {
		return cathegory;
	}

	public void setCathegory(Cathegory cathegory) {
		this.cathegory = cathegory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LNNode() {
		super();
		status = Status.Disabled;
		cathegory = Cathegory.UNDEFINED;
		domain = Domain.UNDEFINED;
		description = "";

	}

	@Override
	public void loadElement(Element el) {
		super.loadElement(el);
		status = Status.valueOf(el.getAttribute("status"));
		cathegory = Cathegory.valueOf(el.getAttribute("cathegory"));
		domain = Domain.valueOf(el.getAttribute("domain"));
		description = el.getAttribute("description");
	}

	@Override
	public Element toElement() {
		Element nodeDescriptor = super.toElement();
		nodeDescriptor.setAttribute("status", status.toString());
		nodeDescriptor.setAttribute("cathegory", cathegory.toString());
		nodeDescriptor.setAttribute("domain", domain.toString());
		nodeDescriptor.setAttribute("description", description);
		return nodeDescriptor;
	}

	public abstract boolean includes(GeoLocationInterface location);

	public abstract GeoLocation locate();

}
