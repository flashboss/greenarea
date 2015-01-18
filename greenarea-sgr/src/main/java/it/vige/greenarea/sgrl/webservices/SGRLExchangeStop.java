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
package it.vige.greenarea.sgrl.webservices;

import it.vige.greenarea.dto.GeoLocation;

public class SGRLExchangeStop extends SgrlNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6277537244364420453L;
	private GeoLocation location;
	private String locationNotes;

	public GeoLocation getLocation() {
		return location;
	}

	public void setLocation(GeoLocation location) {
		this.location = location;
	}

	public String getLocationNotes() {
		return locationNotes;
	}

	public void setLocationNotes(String locationNotes) {
		this.locationNotes = locationNotes;
	}

}
