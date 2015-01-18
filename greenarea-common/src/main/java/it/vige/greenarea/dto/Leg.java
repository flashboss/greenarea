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
package it.vige.greenarea.dto;

import java.io.Serializable;

public class Leg implements Serializable {
	
	private static final long serialVersionUID = -4869422424441467989L;
	private ExchangeSite source;
	private ExchangeSite destination;
	private String vector;

	public Leg() {
	}

	public Leg(ExchangeSite source, String vector, ExchangeSite destination) {
		this.source = source;
		this.vector = vector;
		this.destination = destination;
	}
	
	public ExchangeSite getDestination() {
		return destination;
	}

	public void setDestination(ExchangeSite destination) {
		this.destination = destination;
	}

	public ExchangeSite getSource() {
		return source;
	}

	public void setSource(ExchangeSite source) {
		this.source = source;
	}

	public String getVector() {
		return vector;
	}

	public void setVector(String vector) {
		this.vector = vector;
	}

	@Override
	public String toString() {
		return /* id + " - "+ */vector + ": " + source.getName() + " --> "
				+ destination.getName();
	}

}