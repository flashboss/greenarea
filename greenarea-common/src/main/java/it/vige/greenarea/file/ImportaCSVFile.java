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
package it.vige.greenarea.file;

import static java.lang.System.getenv;
import static org.apache.commons.csv.CSVFormat.newFormat;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.dto.Filtro;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.vo.RichiestaXML;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

public class ImportaCSVFile implements ImportaFile {

	private final static String IMPORT_FOLDER = getenv("HOME") + "/greenarea";

	private List<Filtro> acceptedRoundCodes = new ArrayList<Filtro>();

	private Logger logger = getLogger(getClass());

	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	private OperatoreLogistico operatoreLogistico;

	private Date date;

	public ImportaCSVFile(OperatoreLogistico operatoreLogistico, Date date) {
		this.operatoreLogistico = operatoreLogistico;
		this.date = date;
	}

	@Override
	public File recuperaFile() {
		File importFolder = getDirectory();
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				long today = 0;
				String todayStr = dateFormat.format(new Date());
				try {
					today = dateFormat.parse(todayStr).getTime();
				} catch (ParseException e) {
					logger.error("greenarea common", e);
				}
				return pathname.lastModified() >= today;
			}
		};
		File[] elencoFile = importFolder.listFiles(filter);
		if (elencoFile != null && elencoFile.length > 0)
			return elencoFile[0];
		else
			return null;
	}

	@Override
	public List<Richiesta> convertiARichieste(List<RichiestaXML> richiesteXML,
			OperatoreLogistico operatoreLogistico) {
		List<Richiesta> richieste = new ArrayList<Richiesta>();
		for (RichiestaXML richiestaXML : richiesteXML) {
			Richiesta richiesta = new Richiesta(richiestaXML);
			richiesta.setOperatoreLogistico(operatoreLogistico);
			richieste.add(richiesta);
		}
		return richieste;
	}

	@Override
	public List<RichiestaXML> prelevaDati(InputStream inputStream,
			List<Filtro> filtri) throws Exception {
		if (filtri != null)
			acceptedRoundCodes.addAll(filtri);
		Iterable<CSVRecord> records = newFormat(';').withNullString("")
				.withIgnoreSurroundingSpaces(true)
				.parse(new InputStreamReader(inputStream));
		List<RichiestaXML> richiesteXML = new ArrayList<RichiestaXML>();
		int i = 0;
		for (CSVRecord cell : records) {
			RichiestaXML richiestaXML = new RichiestaXML();
			logger.debug("richiestaXML n: " + i++);
			aggiungiCampiARichiestaXML(richiestaXML, cell);
			String roundCode = richiestaXML.getRoundCode();
			if (acceptRoundCode(roundCode))
				richiesteXML.add(richiestaXML);
		}
		return richiesteXML;
	}

	@Override
	public boolean acceptRoundCode(String roundCode) {
		boolean result = false;
		if (acceptedRoundCodes.size() == 0)
			return true;
		else if (roundCode != null)
			for (Filtro filtro : acceptedRoundCodes) {
				String roundCodeByFilter = filtro.getRoundCode();
				if (roundCodeByFilter.equals(roundCode))
					result = true;
				else if (roundCodeByFilter.startsWith("0")
						&& roundCodeByFilter.substring(1,
								roundCodeByFilter.length()).equals(roundCode))
					result = true;
			}
		return result;
	}

	@Override
	public OperatoreLogistico getOperatoreLogistico() {
		return operatoreLogistico;
	}

	@Override
	public void setOperatoreLogistico(OperatoreLogistico operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	@Override
	public File getDirectory() {
		File importDirectory = new File(IMPORT_FOLDER + "/"
				+ operatoreLogistico.getId());
		if (!importDirectory.exists())
			importDirectory.mkdir();
		return importDirectory;
	}

	private void aggiungiCampiARichiestaXML(RichiestaXML richiestaXML,
			CSVRecord cell) throws Exception {
		richiestaXML.setShipmentId(cell.get(0));
		richiestaXML.setIdStop(cell.get(1));
		richiestaXML.setDepot(cell.get(2));
		richiestaXML.setPieces(new Integer(cell.get(3)));
		richiestaXML.setWeight(new Double(splitPoint(cell.get(4).replace(",",
				"."))));
		richiestaXML.setVolume(new Double(splitPoint(cell.get(5).replace(",",
				"."))));
		long data = new Long(cell.get(6));
		if (date != null)
			data = new Long(dateFormat.format(date.getTime()));
		Date dataFormattata = dateFormat.parse(data + "");
		richiestaXML.setDataEarlestPu(dataFormattata);
		data = new Integer(cell.get(7));
		if (date != null)
			data = new Long(dateFormat.format(date.getTime()));
		dataFormattata = dateFormat.parse(data + "");
		richiestaXML.setDataLatestPu(dataFormattata);
		data = new Integer(cell.get(8));
		if (date != null)
			data = new Long(dateFormat.format(date.getTime()));
		dataFormattata = dateFormat.parse(data + "");
		richiestaXML.setDataEarlestDelivery(dataFormattata);
		data = new Integer(cell.get(9));
		if (date != null)
			data = new Long(dateFormat.format(date.getTime()));
		dataFormattata = dateFormat.parse(data + "");
		richiestaXML.setDataLatestDelivery(dataFormattata);
		richiestaXML.setTimeFromPu(new Double(cell.get(10).substring(0, 5)));
		richiestaXML.setTimeToPu(new Double(cell.get(11).substring(0, 5)));
		richiestaXML.setTimeFromDelivery(new Double(cell.get(12)
				.substring(0, 5)));
		richiestaXML
				.setTimeToDelivery(new Double(cell.get(13).substring(0, 5)));
		richiestaXML.setHandlingType(cell.get(14));
		String packageType = cell.get(15);
		if (packageType != null)
			richiestaXML.setPackageType(packageType != null ? packageType
					.charAt(0) : null);
		richiestaXML.setCustomer(cell.get(16));
		richiestaXML.setAddress(cell.get(17));
		String zipCode = cell.get(18);
		richiestaXML.setZipCode(zipCode != null ? new Integer(zipCode) : null);
		richiestaXML.setProvince(cell.get(19));
		richiestaXML.setCity(cell.get(20));
		richiestaXML.setCountry(cell.get(21));
		richiestaXML.setShoppingCentre(new Integer(cell.get(22)));
		richiestaXML.setRoundCode(cell.get(23));
		richiestaXML.setTntType(cell.get(24));
		richiestaXML.setFlagInternational(cell.get(25));
		richiestaXML.setDomesticLdv(cell.get(26));
		richiestaXML.setTaskField(cell.get(27));
		richiestaXML.setTel(cell.get(28));
		richiestaXML.setEmail(cell.get(29));
		richiestaXML.setHandlingClass(new Integer(cell.get(30)));
		richiestaXML.setLocExtId(cell.get(31));
		richiestaXML.setStatus(new Integer(cell.get(32)));
		String latitude = splitPoint(cell.get(33).replace(",", "."));
		String longitude = splitPoint(cell.get(34).replace(",", "."));
		richiestaXML.setLatitude(new Double(latitude));
		richiestaXML.setLongitude(new Double(longitude));
	}

	private String splitPoint(String value) {
		String newValue = "";
		String[] lonArray = value.split("\\.");
		if (lonArray.length > 2) {
			for (int i = 0; i < lonArray.length; i++) {
				newValue = newValue + lonArray[i];
				if (i == 0)
					newValue = newValue + ".";
			}
		} else
			newValue = value;
		return newValue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
