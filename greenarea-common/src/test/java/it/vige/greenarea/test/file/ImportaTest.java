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
package it.vige.greenarea.test.file;

import static it.vige.greenarea.Constants.ITALY;
import static it.vige.greenarea.Utilities.yyyyMMdd;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import it.vige.greenarea.dto.Filtro;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.file.ImportaCSVFile;
import it.vige.greenarea.file.ImportaFile;
import it.vige.greenarea.file.ImportaXLSFile;
import it.vige.greenarea.vo.RichiestaXML;

public class ImportaTest {

	@Test
	public void testParseXlsx_120306_0602Corretto() throws Exception {
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_120306_0602.xlsx");
		ImportaFile importaFile = new ImportaXLSFile(getOperatoreLogistico());
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 87);

		RichiestaXML richiestaXML = richiesteXML.get(0);
		assertEquals(richiestaXML.getShipmentId(), "176190450");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 1);
		assertEquals(richiestaXML.getWeight(), 1.55, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.006, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataLatestPu(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataEarlestDelivery(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataLatestDelivery(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getTimeFromPu(), 0.3333333333321207, 0);
		assertEquals(richiestaXML.getTimeToPu(), 0.5833333333321207, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 0.375, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 0.7083333333321207, 0);
		assertEquals(richiestaXML.getHandlingType(), "PM");
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "FABIO ROSSINI");
		assertEquals(richiestaXML.getAddress(), "VIA CALANDRA FRATELLI 5");
		assertEquals(richiestaXML.getZipCode(), 10123);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "N");
		assertEquals(richiestaXML.getDomesticLdv(), "MT35105108");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertNull(richiestaXML.getTel());
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10123-VIA CALANDRA FRATELLI 5-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);

		richiestaXML = richiesteXML.get(57);
		assertEquals(richiestaXML.getShipmentId(), "181035160");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 2);
		assertEquals(richiestaXML.getWeight(), 21.5, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.169, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataLatestPu(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataEarlestDelivery(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataLatestDelivery(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getTimeFromPu(), 0.3333333333321207, 0);
		assertEquals(richiestaXML.getTimeToPu(), 0.5833333333321207, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 0.375, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 0.7083333333321207, 0);
		assertEquals(richiestaXML.getHandlingType(), "PM");
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "ARDUINO");
		assertEquals(richiestaXML.getAddress(), "VIA MAZZINI GIUSEPPE 7");
		assertEquals(richiestaXML.getZipCode(), 10123);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "N");
		assertEquals(richiestaXML.getDomesticLdv(), "QS11746012");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertNull(richiestaXML.getTel());
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10123-VIA MAZZINI GIUSEPPE 7-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);

		richiestaXML = richiesteXML.get(86);
		assertEquals(richiestaXML.getShipmentId(), "181698780");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 1);
		assertEquals(richiestaXML.getWeight(), 2.0, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.02, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataLatestPu(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataEarlestDelivery(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getDataLatestDelivery(), yyyyMMdd.parse("20120306"));
		assertEquals(richiestaXML.getTimeFromPu(), 0.3333333333321207, 0);
		assertEquals(richiestaXML.getTimeToPu(), 0.5833333333321207, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 0.375, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 0.7083333333321207, 0);
		assertNull(richiestaXML.getHandlingType());
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "COIN TORINO - 548");
		assertEquals(richiestaXML.getAddress(), "VIA LAGRANGE GIUSEPPE LUIGI 47");
		assertEquals(richiestaXML.getZipCode(), 10123);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "Y");
		assertEquals(richiestaXML.getDomesticLdv(), "AS32628208");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "041-2398000");
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10123-VIA LAGRANGE GIUSEPPE LUIGI 47-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
	}

	@Test
	public void testParseXlsx_120306_0602Errato() throws Exception {
		/*
		 * Il file contiene un errore sul weight alla riga 11 poich?????? in
		 * formato stringa anzich?????? double
		 */
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ERR_120306_0602.xlsx");
		ImportaFile importaFile = new ImportaXLSFile(getOperatoreLogistico());
		assertTrue(importaFile.getDirectory().exists());
		try {
			importaFile.prelevaDati(inputStream,
					asList(new Filtro[] { new Filtro("01", "tnt"), new Filtro("02", "tnt"), new Filtro("06", "tnt") }));
			fail();
		} catch (IllegalStateException ex) {

		}
	}

	@Test
	public void testParseCsv140930_1011Corretto() throws Exception {
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_140930_1011.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), null);
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 81);

		RichiestaXML richiestaXML = richiesteXML.get(0);
		assertEquals(richiestaXML.getShipmentId(), "272013040");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 2);
		assertEquals(richiestaXML.getWeight(), 2.45, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.01, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataLatestPu(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataEarlestDelivery(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataLatestDelivery(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getTimeFromPu(), 8.0, 0);
		assertEquals(richiestaXML.getTimeToPu(), 14.0, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 9.0, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 17.0, 0);
		assertEquals(richiestaXML.getHandlingType(), "PM");
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "NEGOZIO SMOOKE");
		assertEquals(richiestaXML.getAddress(), "VIA CARLO ALBERTO 24");
		assertEquals(richiestaXML.getZipCode(), 10100);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "N");
		assertEquals(richiestaXML.getDomesticLdv(), "MY40208750");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "393488907073");
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10100-VIA CARLO ALBERTO 24-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
		assertEquals(richiestaXML.getLatitude(), 45.066110, 0.0);
		assertEquals(richiestaXML.getLongitude(), 7.684473, 0.0);

		richiestaXML = richiesteXML.get(57);
		assertEquals(richiestaXML.getShipmentId(), "272897800");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 1);
		assertEquals(richiestaXML.getWeight(), 0.2, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.001, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataLatestPu(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataEarlestDelivery(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataLatestDelivery(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getTimeFromPu(), 8.0, 0);
		assertEquals(richiestaXML.getTimeToPu(), 14.0, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 9.0, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 17.0, 0);
		assertEquals(richiestaXML.getHandlingType(), "PM");
		assertEquals(richiestaXML.getPackageType(), 'B');
		assertEquals(richiestaXML.getCustomer(), "PROJET EXPORT SERVICES SNC");
		assertEquals(richiestaXML.getAddress(), "VIA ANDREA DORIA 15");
		assertEquals(richiestaXML.getZipCode(), 10100);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "N");
		assertEquals(richiestaXML.getDomesticLdv(), "MY40210385");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "393488907073");
		assertEquals(richiestaXML.getEmail(), "milano@projetexport.it");
		assertEquals(richiestaXML.getHandlingClass(), 1);
		assertEquals(richiestaXML.getLocExtId(), "10100-VIA ANDREA DORIA 15-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
		assertEquals(richiestaXML.getLatitude(), 45.063974, 0.0);
		assertEquals(richiestaXML.getLongitude(), 7.684381, 0.0);

		richiestaXML = richiesteXML.get(80);
		assertEquals(richiestaXML.getShipmentId(), "273414090");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 1);
		assertEquals(richiestaXML.getWeight(), 0.66, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.01, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataLatestPu(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataEarlestDelivery(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getDataLatestDelivery(), yyyyMMdd.parse("20140930"));
		assertEquals(richiestaXML.getTimeFromPu(), 8.0, 0);
		assertEquals(richiestaXML.getTimeToPu(), 14.0, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 9.0, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 17.0, 0);
		assertNull(richiestaXML.getHandlingType());
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "STILELIBRI SNC C/O IL NOSTRO TEMPO SS");
		assertEquals(richiestaXML.getAddress(), "VIA S. FRANCESCO DA PAOLA");
		assertEquals(richiestaXML.getZipCode(), 10123);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "Y");
		assertEquals(richiestaXML.getDomesticLdv(), "AS44712919");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "393488907073");
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10123-VIA S. FRANCESCO DA PAOLA-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
		assertEquals(richiestaXML.getLatitude(), 45.061035, 0.0);
		assertEquals(richiestaXML.getLongitude(), 7.683869, 0.0);
	}

	@Test
	public void testParseCsv140930_1011CorrettoConDataOdierna() throws Exception {
		Date date = new Date();
		Date newDate = yyyyMMdd.parse(yyyyMMdd.format(date));
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_140930_1011.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), newDate);
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 81);

		RichiestaXML richiestaXML = richiesteXML.get(0);
		assertEquals(richiestaXML.getShipmentId(), "272013040");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 2);
		assertEquals(richiestaXML.getWeight(), 2.45, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.01, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), newDate);
		assertEquals(richiestaXML.getDataLatestPu(), newDate);
		assertEquals(richiestaXML.getDataEarlestDelivery(), newDate);
		assertEquals(richiestaXML.getDataLatestDelivery(), newDate);
		assertEquals(richiestaXML.getTimeFromPu(), 8.0, 0);
		assertEquals(richiestaXML.getTimeToPu(), 14.0, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 9.0, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 17.0, 0);
		assertEquals(richiestaXML.getHandlingType(), "PM");
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "NEGOZIO SMOOKE");
		assertEquals(richiestaXML.getAddress(), "VIA CARLO ALBERTO 24");
		assertEquals(richiestaXML.getZipCode(), 10100);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "N");
		assertEquals(richiestaXML.getDomesticLdv(), "MY40208750");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "393488907073");
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10100-VIA CARLO ALBERTO 24-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
		assertEquals(richiestaXML.getLatitude(), 45.066110, 0.0);
		assertEquals(richiestaXML.getLongitude(), 7.684473, 0.0);

		richiestaXML = richiesteXML.get(57);
		assertEquals(richiestaXML.getShipmentId(), "272897800");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 1);
		assertEquals(richiestaXML.getWeight(), 0.2, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.001, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), newDate);
		assertEquals(richiestaXML.getDataLatestPu(), newDate);
		assertEquals(richiestaXML.getDataEarlestDelivery(), newDate);
		assertEquals(richiestaXML.getDataLatestDelivery(), newDate);
		assertEquals(richiestaXML.getTimeFromPu(), 8.0, 0);
		assertEquals(richiestaXML.getTimeToPu(), 14.0, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 9.0, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 17.0, 0);
		assertEquals(richiestaXML.getHandlingType(), "PM");
		assertEquals(richiestaXML.getPackageType(), 'B');
		assertEquals(richiestaXML.getCustomer(), "PROJET EXPORT SERVICES SNC");
		assertEquals(richiestaXML.getAddress(), "VIA ANDREA DORIA 15");
		assertEquals(richiestaXML.getZipCode(), 10100);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "N");
		assertEquals(richiestaXML.getDomesticLdv(), "MY40210385");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "393488907073");
		assertEquals(richiestaXML.getEmail(), "milano@projetexport.it");
		assertEquals(richiestaXML.getHandlingClass(), 1);
		assertEquals(richiestaXML.getLocExtId(), "10100-VIA ANDREA DORIA 15-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
		assertEquals(richiestaXML.getLatitude(), 45.063974, 0.0);
		assertEquals(richiestaXML.getLongitude(), 7.684381, 0.0);

		richiestaXML = richiesteXML.get(80);
		assertEquals(richiestaXML.getShipmentId(), "273414090");
		assertNull(richiestaXML.getIdStop());
		assertEquals(richiestaXML.getDepot(), "TO1");
		assertEquals(richiestaXML.getPieces(), 1);
		assertEquals(richiestaXML.getWeight(), 0.66, 0.0);
		assertEquals(richiestaXML.getVolume(), 0.01, 0.0);
		assertEquals(richiestaXML.getDataEarlestPu(), newDate);
		assertEquals(richiestaXML.getDataLatestPu(), newDate);
		assertEquals(richiestaXML.getDataEarlestDelivery(), newDate);
		assertEquals(richiestaXML.getDataLatestDelivery(), newDate);
		assertEquals(richiestaXML.getTimeFromPu(), 8.0, 0);
		assertEquals(richiestaXML.getTimeToPu(), 14.0, 0);
		assertEquals(richiestaXML.getTimeFromDelivery(), 9.0, 0);
		assertEquals(richiestaXML.getTimeToDelivery(), 17.0, 0);
		assertNull(richiestaXML.getHandlingType());
		assertEquals(richiestaXML.getPackageType(), 'C');
		assertEquals(richiestaXML.getCustomer(), "STILELIBRI SNC C/O IL NOSTRO TEMPO SS");
		assertEquals(richiestaXML.getAddress(), "VIA S. FRANCESCO DA PAOLA");
		assertEquals(richiestaXML.getZipCode(), 10123);
		assertEquals(richiestaXML.getProvince(), "TO");
		assertEquals(richiestaXML.getCity(), "TORINO");
		assertEquals(richiestaXML.getCountry(), ITALY);
		assertEquals(richiestaXML.getShoppingCentre(), 0);
		assertEquals(richiestaXML.getRoundCode(), "01");
		assertEquals(richiestaXML.getTntType(), "D");
		assertEquals(richiestaXML.getFlagInternational(), "Y");
		assertEquals(richiestaXML.getDomesticLdv(), "AS44712919");
		assertEquals(richiestaXML.getTaskField(), "Torino");
		assertEquals(richiestaXML.getTel(), "393488907073");
		assertNull(richiestaXML.getEmail());
		assertEquals(richiestaXML.getHandlingClass(), 2);
		assertEquals(richiestaXML.getLocExtId(), "10123-VIA S. FRANCESCO DA PAOLA-TORINO");
		assertEquals(richiestaXML.getStatus(), 1);
		assertEquals(richiestaXML.getLatitude(), 45.061035, 0.0);
		assertEquals(richiestaXML.getLongitude(), 7.683869, 0.0);
	}

	@Test
	public void testParseCsv140930_1011Errato() throws Exception {
		/*
		 * Il file contiene un errore sul weight dello shipping order 272021710
		 * poich?? in formato stringa anzich?? double
		 */
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ERR_140930_1011.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), null);
		assertTrue(importaFile.getDirectory().exists());
		try {
			importaFile.prelevaDati(inputStream,
					asList(new Filtro[] { new Filtro("01", "tnt"), new Filtro("02", "tnt"), new Filtro("06", "tnt") }));
			fail();
		} catch (NumberFormatException ex) {

		}
	}

	@Test
	public void testParseCsv141020_0900Corretto() throws Exception {
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_141020_0900.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), null);
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 16);
	}

	@Test
	public void testParseCsv141020_0904Corretto() throws Exception {
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_141020_0904.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), null);
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 74);
	}

	@Test
	public void testParseCsv141021_0900Corretto() throws Exception {
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_141021_0900.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), null);
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 11);
	}

	@Test
	public void testParseCsv141021_0904Corretto() throws Exception {
		InputStream inputStream = currentThread().getContextClassLoader()
				.getResourceAsStream("it/vige/greenarea/file/TO1_ORD_141021_0904.CSV");
		ImportaFile importaFile = new ImportaCSVFile(getOperatoreLogistico(), null);
		assertTrue(importaFile.getDirectory().exists());
		List<RichiestaXML> richiesteXML = importaFile.prelevaDati(inputStream,
				asList(new Filtro[] { new Filtro("01", "tnt") }));
		assertEquals(richiesteXML.size(), 104);
	}

	private OperatoreLogistico getOperatoreLogistico() {
		GreenareaUser greenareaUser = new GreenareaUser();
		greenareaUser.setId("tnt");
		OperatoreLogistico operatoreLogistico = new OperatoreLogistico(greenareaUser);
		return operatoreLogistico;
	}
}
