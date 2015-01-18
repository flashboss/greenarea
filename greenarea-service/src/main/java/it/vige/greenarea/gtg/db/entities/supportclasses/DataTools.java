/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.gtg.db.entities.supportclasses;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.ExchangeStop;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.dto.GeoLocationInterface;

/**
 * 
 * @author 00917308
 */
public class DataTools {
	public static ExchangeStop newExchangeStop(GeoLocationInterface gli) {
		if (gli == null)
			throw new IllegalArgumentException();
		ExchangeStop es = new ExchangeStop();
		es.setLocation(new DBGeoLocation(gli));
		return es;
	}

	public static Mission newMission(ExchangeStop beginEs, ExchangeStop endEs,
			Vehicle truck, Mission m) {
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
