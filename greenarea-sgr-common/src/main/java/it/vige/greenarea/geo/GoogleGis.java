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

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.StringCharacterIterator;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.slf4j.Logger;

/**
 * 
 * TODO questa versione sta per essere riadattata per
 *         usare google. Una volta completato il riadattamento verra' modificato
 *         ilnome.
 */
public class GoogleGis extends GisService {

	private Logger logger = getLogger(getClass());

	static String googleKey = "abcdef";
	private String locale = "en";

	public GoogleGis(Locale l) {
		locale = l.getCountry();
	}

	/**
	 * 
	 * @param address
	 * @return address with gps valorized if found; null otherwise USA GOOGLE!
	 */
	@Override
	public GeoLocationInterface geoCode(GeoLocationInterface location)
			throws GeoCodingException {
		return geoCode(location.getNumber(), location.getStreet(),
				location.getCity(), location.getAdminAreaLevel1(),
				location.getAdminAreaLevel2(), location.getZipCode(),
				location.getCountry());
	}

	@Override
	public GeoLocationInterface geoCode(String number, String street,
			String city, String adminAreaLevel1, String adminAreaLevel2,
			String zipCode, String country) throws GeoCodingException {
		StringBuilder googleRequest = new StringBuilder("address=");
		if (number != null && !number.isEmpty())
			googleRequest.append(encodeString(number)).append(",+");
		if (street != null && !street.isEmpty())
			googleRequest.append(encodeString(street)).append(",+");
		if (city != null && !city.isEmpty())
			googleRequest.append(encodeString(city)).append(",+");
		if (adminAreaLevel1 != null && !adminAreaLevel1.isEmpty())
			googleRequest.append(encodeString(adminAreaLevel1)).append(",+");
		if (adminAreaLevel2 != null && !adminAreaLevel2.isEmpty())
			googleRequest.append(encodeString(adminAreaLevel2)).append(",+");
		if (zipCode != null && !zipCode.isEmpty())
			googleRequest.append(encodeString(zipCode)).append(",+");
		if (country != null && !country.isEmpty())
			googleRequest.append(encodeString(country)).append(",+");
		if (googleRequest.length() < 10)
			throw new GeoCodingException(GeoCodingError.NO_ADDRESS);
		googleRequest.setLength(googleRequest.length() - 2); // delete last ",+"
		googleRequest.append("&sensor=false&language=").append(locale);
		return googleGisOperation(googleRequest.toString());
	}

	/**
	 * Completa con l'indirizzo partendo dalle coordinate gps
	 * 
	 * @param address
	 * @return false se non e` riuscito a recuperarli
	 */
	@Override
	public GeoLocationInterface reverseGeoCode(GeoLocationInterface location)
			throws GeoCodingException {
		return reverseGeoCode(location.getLatitude(), location.getLongitude());
	}

	@Override
	public GeoLocationInterface reverseGeoCode(double latitude, double longitude)
			throws GeoCodingException {
		String googleRequest = "latlng=" + Double.toString(latitude) + ","
				+ Double.toString(longitude) + "&sensor=false&language="
				+ locale;
		return googleGisOperation(googleRequest);
	}

	private GeoLocationInterface googleGisOperation(String jsonCommandString)
			throws GeoCodingException {
		GeoLocation gl = null;
		String geocodeResponse = null;
		JSONObject json = null;

		String googleURLstring = "http://maps.googleapis.com/maps/api/geocode/json?";
		String googleRequest = googleURLstring + jsonCommandString;
		HttpURLConnection googleConn = null;

		try {
			googleRequest = removeSpaces(googleRequest);
			URL googleUrl;
			googleUrl = new URL(googleRequest);
			googleConn = (HttpURLConnection) googleUrl.openConnection();
			googleConn.setRequestMethod("GET");
			googleConn.connect();
			InputStream is = googleConn.getInputStream();
			geocodeResponse = org.apache.commons.io.IOUtils.toString(is);
		} catch (MalformedURLException ex) {
			logger.error("errore sgr common", ex);
			throw new GeoCodingException(GeoCodingError.INVALID_URL);
		} catch (IOException ex) {
			logger.error("errore sgr common", ex);
			throw new GeoCodingException(GeoCodingError.GIS_SERVICE_UNAVAILABLE);
		}

		try {
			json = (JSONObject) JSONSerializer.toJSON(geocodeResponse);
		} catch (Exception ex) {
			logger.error("errore sgr common", ex);
			throw new GeoCodingException(GeoCodingError.PROTOCOL_ERROR);
		}

		if (json.getString("status").equals("OK")) {
			gl = new GeoLocation(0., 0.);
			json = json.getJSONArray("results").getJSONObject(0);
			JSONArray addressComponents = json
					.getJSONArray("address_components");
			for (Object j : addressComponents) {
				if (j instanceof JSONObject) {
					JSONArray types = ((JSONObject) j).getJSONArray("types");
					for (Object jj : types) {
						if (jj instanceof String) {
							if (((String) jj).equals("street_number"))
								gl.setNumber(((JSONObject) j)
										.getString("long_name"));
							else if (((String) jj).equals("route"))
								gl.setStreet(((JSONObject) j)
										.getString("long_name"));
							else if (((String) jj).equals("locality"))
								gl.setCity(((JSONObject) j)
										.getString("long_name"));
							else if (((String) jj)
									.equals("administrative_area_level_1"))
								gl.setAdminAreaLevel1(((JSONObject) j)
										.getString("long_name"));
							else if (((String) jj)
									.equals("administrative_area_level_2"))
								gl.setAdminAreaLevel2(((JSONObject) j)
										.getString("long_name"));
							else if (((String) jj).equals("country"))
								gl.setCountry(((JSONObject) j)
										.getString("long_name"));
							else if (((String) jj).equals("postal_code"))
								gl.setZipCode(((JSONObject) j)
										.getString("long_name"));
							break;
						}
					}
				}
			}

			gl.setLatitude(((JSONObject) json.getJSONObject("geometry").get(
					"location")).getDouble("lat"));
			gl.setLongitude(((JSONObject) json.getJSONObject("geometry").get(
					"location")).getDouble("lng"));

		} else
			throw new GeoCodingException(GeoCodingError.NO_ADDRESS);

		return gl;

	}

	private static String removeSpaces(String inputString) throws IOException {
		char ch;
		StringReader legge = new StringReader(inputString);
		String risultato = "";
		if (inputString == null) {
			return null;
		}
		while ((ch = (char) legge.read()) != StringCharacterIterator.DONE) {
			if (ch == ' ') {
				risultato += "%20";
			} else {
				risultato += ch;
			}
		}
		return risultato;
	}

	private static String encodeString(String inputString) {

		if (inputString == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(inputString);
/*****************************************************************
         ** Caratteri dannosi in Http :
         ** Character Code      Points(Hex) Code
         ** Dollar ("$")                24
         ** Ampersand ("&")             26
         ** Plus ("+")                  2B
         ** Comma (",")                 2C
         ** Forward slash/Virgule ("/") 2F
         ** Colon (":")                 3A
         ** Semi-colon (";")            3B
         ** Equals ("=")                3D
         ** Question mark ("?")         3F
         ** 'At' symbol ("@")           40
         ***------------ Altri caratteri potenzialmente dannosi----------
         ** Space                       20
         ** Quotation marks             22
         ** 'Less Than' symbol ("<")    3C
         ** 'Greater Than' symbol (">") 3E
         ** 'Pound' character ("#")     23
         ** Percent character ("%")     25
         ** Left Curly Brace ("{")      7B
         ** Right Curly Brace ("}")     7D
         ** Vertical Bar/Pipe ("|")     7C
         ** Backslash ("\")             5C
         ** Caret ("^")                 5E
         ** Left Square Bracket ("[")   5B
         ** Right Square Bracket ("]")  5D
         ** Grave Accent ("`")          60
         *****************************************************************/
		StringReader legge = new StringReader(sb.toString());
		char ch;
		String result = "";
		try {
			while ((ch = (char) legge.read()) != StringCharacterIterator.DONE) {
				switch (ch) {
				case ' ':
					result += "%20";
					break;
				case '$':
					result += "%24";
					break;
				case '&':
					result += "%26";
					break;
				case '+':
					result += "%2B";
					break;
				case ',':
					result += "%2C";
					break;
				case '/':
					result += "%2F";
					break;
				case ':':
					result += "%3A";
					break;
				case ';':
					result += "%3B";
					break;
				case '=':
					result += "%3D";
					break;
				case '?':
					result += "%3F";
					break;
				case '@':
					result += "%40";
					break;
				case '"':
					result += "%22";
					break;
				case '<':
					result += "%3C";
					break;
				case '>':
					result += "%3E";
					break;
				case '#':
					result += "%23";
					break;
				case '%':
					result += "%25";
					break;
				case '{':
					result += "%7B";
					break;
				case '}':
					result += "%7D";
					break;
				case '|':
					result += "%7C";
					break;
				case '\\':
					result += "%5C";
					break;
				case '^':
					result += "%5E";
					break;
				case '~':
					result += "%7E";
					break;
				case '[':
					result += "%5B";
					break;
				case ']':
					result += "%5D";
					break;
				case '`':
					result += "%60";
					break;
				// tolgo le lettere accentate
				case 'à':
					result += "a";
					break;
				case 'è':
				case 'é':
					result += "e";
					break;
				case 'ì':
					result += "i";
					break;
				case 'ò':
					result += "o";
					break;
				case 'ù':
					result += "u";
					break;
				default:
					result += ch;
				}
			}

		} catch (IOException ex) {
			return null;
		}
		return result;
	}

}
