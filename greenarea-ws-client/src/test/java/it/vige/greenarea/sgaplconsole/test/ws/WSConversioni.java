package it.vige.greenarea.sgaplconsole.test.ws;

import it.vige.greenarea.sgrl.webservices.GeoLocation;

public class WSConversioni {

	public static GeoLocation convertiGeoLocationToWS(it.vige.greenarea.dto.GeoLocation geoLocation) {
		GeoLocation wsGeoLocation = new GeoLocation();
		wsGeoLocation.setCity(geoLocation.getCity());
		wsGeoLocation.setAdminAreaLevel1(geoLocation.getAdminAreaLevel1());
		wsGeoLocation.setAdminAreaLevel2(geoLocation.getAdminAreaLevel2());
		wsGeoLocation.setCountry(geoLocation.getCountry());
		wsGeoLocation.setLatitude(geoLocation.getLatitude());
		wsGeoLocation.setLongitude(geoLocation.getLongitude());
		wsGeoLocation.setNumber(geoLocation.getNumber());
		wsGeoLocation.setRadius(geoLocation.getRadius());
		wsGeoLocation.setStreet(geoLocation.getStreet());
		wsGeoLocation.setZipCode(geoLocation.getZipCode());
		wsGeoLocation.setCity(geoLocation.getCity());
		return wsGeoLocation;
	}

}
