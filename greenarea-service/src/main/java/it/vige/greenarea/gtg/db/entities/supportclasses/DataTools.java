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
package it.vige.greenarea.gtg.db.entities.supportclasses;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.GeoLocationInterface;

public class DataTools {
	public static ExchangeStop newExchangeStop(GeoLocationInterface gli) {
		if (gli == null)
			throw new IllegalArgumentException();
		ExchangeStop es = new ExchangeStop();
		es.setLocation(new DBGeoLocation(gli));
		return es;
	}

	public static Mission newMission(ExchangeStop beginEs, ExchangeStop endEs, Vehicle truck, Mission m) {
		if (beginEs == null || endEs == null || truck == null)
			throw new IllegalArgumentException();
		m.getExchangeStops().add(beginEs);
		m.getExchangeStops().add(endEs);
		m.setTruck(truck);
		return (m);
	}

	public static void mergeExchangeStop(ExchangeStop toXs, ExchangeStop xs) {
		toXs.getCollectingList().addAll(xs.getCollectingList());
		toXs.getDeliveryList().addAll(xs.getDeliveryList());
	}

}
