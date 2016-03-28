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

import static it.vige.greenarea.Constants.ITALY;
import static it.vige.greenarea.geo.CAP.getProvincia;
import static it.vige.greenarea.sgrl.webservices.SGRLServiceException.SGRLServiceError.INVALID_DESTINATION_ADDRESS;
import static it.vige.greenarea.sgrl.webservices.SGRLServiceException.SGRLServiceError.INVALID_PARAMETERS;
import static it.vige.greenarea.sgrl.webservices.SGRLServiceException.SGRLServiceError.INVALID_SOURCE_ADDRESS;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;

import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.GeoLocationInterface;
import it.vige.greenarea.geo.GisService;
import it.vige.greenarea.ln.routing.LNGraphAnalysis;
import it.vige.greenarea.ln.routing.LNPath;
import it.vige.greenarea.sgrl.LogisticNetworkManager;

@WebService(serviceName = "LogisticNetworkRouting")
public class LogisticNetworkRouting {

	private Logger logger = getLogger(getClass());

	@Inject
	private LogisticNetworkManager logisticNetworkManager;

	GisService gis = null;

	/**
	 * Web service operation getRoutes restituisce un elenco di stringhe
	 * ciascuna delle quali contiene la lista dei nomi delle tappe intermedie e
	 * dei vettori usati per una possibile route **** Serve solo per test
	 */
	@WebMethod(operationName = "getRoutes")
	public java.lang.String[] getRoutes(@WebParam(name = "source") final GeoLocation source,
			@WebParam(name = "destination") final GeoLocation destination,
			@WebParam(name = "options") final String options) throws SGRLServiceException {
		LNGraphAnalysis lng = new LNGraphAnalysis();
		GeoLocationInterface sourceI, destinationI;
		if (gis == null) {
			gis = logisticNetworkManager.getGisService();
		}
		if (source == null || destination == null) {
			return new String[] { "source o destination null" };
		}
		sourceI = validate(source);
		if (sourceI == null) {
			throw new SGRLServiceException(INVALID_SOURCE_ADDRESS);
		}
		destinationI = validate(destination);
		if (destinationI == null) {
			throw new SGRLServiceException(INVALID_DESTINATION_ADDRESS);
		}
		ArrayList<LNPath> paths = lng.getLogisticPaths(logisticNetworkManager.getActiveNetwork(), sourceI,
				destinationI);
		String[] result = new String[paths.size()];
		for (int i = 0; i < paths.size(); i++) {
			_printLNPath(i, paths.get(i));
			result[i] = paths.get(i).toString();
		}
		return result;
	}

	/**
	 * Web service operation getLNPaths restituisce un array di
	 * SGRLLogisticPaths ciascuno dei quali ?????? una sequenza di ExchangeStop
	 * ed il primo e l'ultimo sono geoFrom, ovvero l'origine della spedizione e
	 * geoTo, ovvero la destinazione
	 */
	@WebMethod(operationName = "getLNPaths")
	public SgrlLogisticPath[] getLNPaths(@WebParam(name = "source") final GeoLocation source,
			@WebParam(name = "destination") final GeoLocation destination, @WebParam(name = "options") String options)
					throws SGRLServiceException {
		LNGraphAnalysis lng = new LNGraphAnalysis();
		GeoLocationInterface sourceI, destinationI;
		if (gis == null) {
			gis = logisticNetworkManager.getGisService();
		}
		if (source == null || destination == null) {
			throw new SGRLServiceException(INVALID_PARAMETERS);
		}
		sourceI = validate(source);
		if (sourceI == null) {
			throw new SGRLServiceException(INVALID_SOURCE_ADDRESS);
		}
		destinationI = validate(destination);
		if (destinationI == null) {
			throw new SGRLServiceException(INVALID_DESTINATION_ADDRESS);
		}
		ArrayList<SgrlLogisticPath> result = new ArrayList<SgrlLogisticPath>();
		ArrayList<LNPath> paths = lng.getLogisticPaths(logisticNetworkManager.getActiveNetwork(), sourceI,
				destinationI);
		int i = 0;
		for (LNPath p : paths) {
			_printLNPath(i++, p);
			result.add(SgrlLogisticPath.code(p));
		}
		return result.toArray(new SgrlLogisticPath[0]);
	}

	private GeoLocationInterface validate(GeoLocation gl) {
		if (gl == null)
			return null;
		// se non ho country code e non ho coordinate metto IT come default
		if ((gl.getCountry() == null || gl.getCountry().isEmpty()) && (gl.getLatitude() == 0.)
				&& (gl.getLongitude() == 0.))
			gl.setCountry(ITALY);
		// se mi manca indirizzo o coordinate gl e' non valido
		if ((gl.getCity() == null || gl.getCity().isEmpty())
				&& (gl.getAdminAreaLevel1() == null || gl.getAdminAreaLevel1().isEmpty())
				&& (gl.getAdminAreaLevel2() == null || gl.getAdminAreaLevel2().isEmpty())
				&& (gl.getZipCode() == null || gl.getZipCode().isEmpty()) && (gl.getLatitude() == 0.)
				&& (gl.getLongitude() == 0.)) {
			return null;
		}
		GeoLocationInterface result = null;
		if ((gl.getCity() == null || gl.getCity().isEmpty())
				&& (gl.getAdminAreaLevel1() == null || gl.getAdminAreaLevel1().isEmpty())
				&& (gl.getAdminAreaLevel2() == null || gl.getAdminAreaLevel2().isEmpty())
				&& (gl.getZipCode() == null || gl.getZipCode().isEmpty())
				&& (gl.getCountry() == null || gl.getCountry().isEmpty()) && gl.getLatitude() != 0.
				&& gl.getLongitude() != 0.) {
			try {
				result = gis.reverseGeoCode(gl);
			} catch (GisService.GeoCodingException ex) {
				logger.warn("chiamata gis", ex);
			}
		} else {
			try {
				// verifico che cap esista, se non esiste lo metto a null
				if (getProvincia(gl.getZipCode()) == null) {
					gl.setZipCode(null);
				}
				if (gl.getCountry().toUpperCase().startsWith(ITALY) && (gl.getZipCode() == null)) {
					// vediamo se c'e' almeno la citta' o la provincia
					if ((gl.getCity() == null || gl.getCity().isEmpty())
							&& (gl.getAdminAreaLevel2() == null || gl.getAdminAreaLevel2().isEmpty())) {
						// non ci sono percio' non posso calcolare indirizzo
						return null;
					}
				}

				result = gis.geoCode(gl);

			} catch (GisService.GeoCodingException ex) {
				logger.warn("validazione gis", ex);
			}
		}
		return result;
	}

	private void _printLNPath(int i, LNPath p) {
		StringBuilder sb = new StringBuilder();
		sb.append("route ").append(Integer.toString(i)).append(": ").append(p.toString()).append("\n");
		logger.info(sb.toString());
	}

	/**
	 * Web service operation
	 */
	@WebMethod(operationName = "getSGRLRoutes")
	public SgrlRoute[] getSGRLRoutes(@WebParam(name = "source") final GeoLocation source,
			@WebParam(name = "destination") final GeoLocation destination, @WebParam(name = "options") String options)
					throws SGRLServiceException {
		LNGraphAnalysis lng = new LNGraphAnalysis();
		GeoLocationInterface sourceI, destinationI;
		if (gis == null) {
			gis = logisticNetworkManager.getGisService();
		}
		if (source == null || destination == null) {
			throw new SGRLServiceException(INVALID_PARAMETERS);
		}
		sourceI = validate(source);
		if (sourceI == null) {
			throw new SGRLServiceException(INVALID_SOURCE_ADDRESS);
		}
		destinationI = validate(destination);
		if (destinationI == null) {
			throw new SGRLServiceException(INVALID_DESTINATION_ADDRESS);
		}
		ArrayList<SgrlRoute> result = new ArrayList<SgrlRoute>();
		ArrayList<LNPath> paths = lng.getLogisticPaths(logisticNetworkManager.getActiveNetwork(), sourceI,
				destinationI);
		int i = 0;
		for (LNPath p : paths) {
			_printLNPath(i++, p);
			result.add(SgrlRoute.code(p));
		}
		return sortRoutes(result).toArray(new SgrlRoute[0]);
	}

	private ArrayList<SgrlRoute> sortRoutes(ArrayList<SgrlRoute> rl) {
		boolean swapped = true;
		while (swapped) {
			swapped = false;
			for (int i = 0; i < rl.size(); i++) {
				if (i + 1 < rl.size() && rl.get(i).getCost() > rl.get(i + 1).getCost()) {
					swapped = true;
					rl.set(i, rl.get(i + 1));
				}
			}
		}
		return rl;
	}
}
