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

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

import org.w3c.dom.Element;

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
