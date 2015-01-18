/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea;

import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.dto.GeoLocation;

/**
 * 
 * @author 00917308
 */

public class GTGsystem {

	public static final String TAGLATITUDINE = "GTG.base.latitude";
	public static final String TAGLONGITUDINE = "GTG.base.longitude";
	public static final String INITIALDURATION = "GTG.timerInitialDuration";
	public static final String INTERVALDURATION = "GTG.timerIntervalDuration";
	public static final String CLIPROMPT = "GTG.cliPrompt";
	public static final String CLIHELPHEADER = "GTG.cliHelpHeader";
	public static final GeoLocation olivetti = new GeoLocation("via Olivetti",
			"6", "10148", "Torino", "Torino", "Piemonte", "IT", 45.113322,
			7.670571);
	public static final GeoLocation reiss = new GeoLocation(
			"via G. Reiss Romoli", "274", "Torino", 45.1109585, 7.6680484);
	public static final GeoLocation giulioCesare = new GeoLocation(
			"corso G. Cesare", "127", "Torino", 45.09383, 7.694635);
	public static final GeoLocation stradella = new GeoLocation(
			"via Stradella", "57", "Torino", 45.093482, 7.67557);
	public static final GeoLocation venaria = new GeoLocation("via Venaria",
			"5", "Torino", 45.106349, 7.662674);
	public static final DBGeoLocation marco = new DBGeoLocation("Marco",
			"Marasco", null, "3357669004", "m.marasco@multiitay.com", olivetti);
	public static final DBGeoLocation luigi = new DBGeoLocation("Luigi",
			"Grossi", null, "3357669002", "l.grossi@multiitay.com", venaria);
	public static final DBGeoLocation enzo = new DBGeoLocation("Enzo", "Conni",
			"01122152", "3316001220", "enzo.conni@vige.it", giulioCesare);
	public static final DBGeoLocation vige = new DBGeoLocation("TLAB",
			"Telelia", null, "3357669000", "tlab@multiitay.com", reiss);
	public static final DBGeoLocation lella = new DBGeoLocation("Raffaella",
			"Lavolo", null, "3357669745", "r.lavolo@gmail.com", stradella);

	public double getBaseLatitude() {
		return olivetti.getLatitude();
	}

	public double getBaseLongitude() {
		return olivetti.getLongitude();
	}
}
