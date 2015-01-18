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
package it.vige.greenarea.cl.library.entities;

public class SimpleVanModel implements TruckModelInterface {
	private float grossCapacity;
	private int targetPayload;
	private static final float TOLERANCE = 0.1f;

	public SimpleVanModel(float grossCapacity, int targetPayload) {
		if (grossCapacity <= 0 || targetPayload <= 0)
			throw new IllegalArgumentException();
		this.grossCapacity = grossCapacity;
		this.targetPayload = targetPayload;
	}

	public double getLoadLevel(Double volume) {
		return (double) (100 * volume / grossCapacity);
	}

	@Override
	public double load(TruckLoadDescriptor tld, Freight p) {
		double eval = (double) (100 * (tld.getVolume() + p.getVolume()) / grossCapacity);
		if (eval > targetPayload)
			return -eval;
		tld.setVolume(tld.getVolume() + p.getVolume());
		return eval;
	}

	@Override
	public double unload(TruckLoadDescriptor tld, Freight p) {
		tld.setVolume(tld.getVolume() - p.getVolume());
		if (tld.getVolume() < TOLERANCE)
			tld.setVolume(0D);
		return getLoadLevel(tld.getVolume());
	}

	@Override
	public boolean canload(TruckLoadDescriptor tld, Freight p) {
		int eval = (int) (100 * (tld.getVolume() + p.getVolume()) / grossCapacity);
		return (eval <= targetPayload);
	}
}
