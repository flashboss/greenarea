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
package it.vige.greenarea.geo;

import it.vige.greenarea.dto.GeoLocationInterface;

public abstract class GisService {
	private static final double equator = 40075 * 1000;
	private static final double meridian = 40000 * 1000;
	private static final double equatorialRadius = 6378137;
	private static final double polarRadius = 6356752.314;

	public static double getNorthwardLatitude(double latitude, long metersToNorth) {
		double delta = 360 * metersToNorth / meridian;
		return (latitude + delta);
	}

	public static double getSouthwardLatitude(double latitude, long metersToSouth) {
		double delta = 360 * metersToSouth / meridian;
		return (latitude - delta);
	}

	public static double getEastwardLongitude(double latitude, double longitude, long metersToEast) {
		double delta = 360 * metersToEast / (equator * Math.cos(Math.toRadians(latitude)));
		return (longitude - delta);
	}

	public static double getWestwardLongitude(double latitude, double longitude, long metersToWest) {
		double delta = 360 * metersToWest / (equator * Math.cos(Math.toRadians(latitude)));
		return (longitude + delta);
	}

	public static int getDistance(double lat1, double lon1, double lat2, double lon2) { // ritorna
																						// la
																						// distanza
																						// in
																						// metri
		// per ora ?? moooolto approssimata: uso la media aritmetica del raggio
		// terrestre
		// e approssimo la distanza come se la superficie terrestre fosse piatta
		double meanRadius = (equatorialRadius + polarRadius) / 2;
		double deltaX = Math.toRadians(lon1 - lon2) * meanRadius;
		double deltaY = Math.toRadians(lat1 - lat2) * meanRadius;
		return (int) Math.round(Math.sqrt(deltaY * deltaY + deltaX * deltaX));
	}

	public enum GeoCodingError {
		GIS_SERVICE_UNAVAILABLE, INVALID_URL, PROTOCOL_ERROR, NO_ADDRESS, INVALID_GEOPOINT, NO_GEOPOINT
	};

	public class GeoCodingException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6699117521732487320L;
		private GeoCodingError err;

		public GeoCodingException(GeoCodingError err) {
			this.err = err;
		}

		@Override
		public String toString() {
			return (new StringBuilder(this.getClass().getSimpleName())).append(" ").append(err.toString()).toString();
		}
	}

	public abstract GeoLocationInterface reverseGeoCode(GeoLocationInterface location) throws GeoCodingException;

	public abstract GeoLocationInterface geoCode(GeoLocationInterface location) throws GeoCodingException;

	public abstract GeoLocationInterface reverseGeoCode(double latitude, double longitude) throws GeoCodingException;

	public abstract GeoLocationInterface geoCode(String number, String street, String city, String adminAreaLevel1,
			String adminAreaLevel2, String zipCpde, String country) throws GeoCodingException;
}
