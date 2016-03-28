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

import java.util.regex.Pattern;

import org.w3c.dom.Element;

import it.vige.greenarea.I18N.I18N;
import it.vige.greenarea.dto.GeoLocation;

public abstract class LNEdge extends LNCell {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7553031000238848701L;
	public static final Pattern EdgeNamePattern = Pattern.compile("\\w{8,20}+");

	public enum Status {
		Enabled, Disabled
	};

	public enum Cathegory {
		UNDEFINED, Driver, Airfreight, SeaFreight, RailFreight, DeliveryCo
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

	public LNEdge(String cellName) {
		this();
		super.setName(cellName != null ? cellName : I18N.getString("UNDEFINED"));
	}

	public LNEdge() {
		status = Status.Disabled;
		cathegory = Cathegory.UNDEFINED;
		domain = Domain.UNDEFINED;
		description = "";
	}

	@Override
	public void loadElement(Element el) {
		// if( el.getLocalName().equals(this.getClass().getSimpleName())){
		super.loadElement(el);
		status = Status.valueOf(el.getAttribute("status"));
		cathegory = Cathegory.valueOf(el.getAttribute("cathegory"));
		domain = Domain.valueOf(el.getAttribute("domain"));
		description = el.getAttribute("description");
		// }
	}

	@Override
	public Element toElement() {
		Element edgeDescriptor = super.toElement();
		edgeDescriptor.setAttribute(LNCell.LNCELLTYPE, this.getClass().getSimpleName());
		edgeDescriptor.setAttribute("status", status.toString());
		edgeDescriptor.setAttribute("cathegory", cathegory.toString());
		edgeDescriptor.setAttribute("domain", domain.toString());
		edgeDescriptor.setAttribute("description", description);

		return edgeDescriptor;
	}

	public boolean includes(GeoLocation location) {
		return false;
	}
}
