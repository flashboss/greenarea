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

import static it.vige.greenarea.Utilities.yyyyMMdd;
import static java.lang.System.getenv;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import it.vige.greenarea.dto.Filtro;
import it.vige.greenarea.dto.OperatoreLogistico;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.vo.RichiestaXML;

public class ImportaXLSFile implements ImportaFile {

	private final static String IMPORT_FOLDER = getenv("HOME") + "/greenarea";

	private List<Filtro> acceptedRoundCodes = new ArrayList<Filtro>();

	private Logger logger = getLogger(getClass());

	private OperatoreLogistico operatoreLogistico;

	public ImportaXLSFile(OperatoreLogistico operatoreLogistico) {
		this.operatoreLogistico = operatoreLogistico;
	}

	@Override
	public File recuperaFile() {
		File importFolder = getDirectory();
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				long today = 0;
				String todayStr = yyyyMMdd.format(new Date());
				try {
					today = yyyyMMdd.parse(todayStr).getTime();
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
	public List<Richiesta> convertiARichieste(List<RichiestaXML> richiesteXML, OperatoreLogistico operatoreLogistico) {
		List<Richiesta> richieste = new ArrayList<Richiesta>();
		for (RichiestaXML richiestaXML : richiesteXML) {
			Richiesta richiesta = new Richiesta(richiestaXML);
			richiesta.setOperatoreLogistico(operatoreLogistico);
			richieste.add(richiesta);
		}
		return richieste;
	}

	@Override
	public List<RichiestaXML> prelevaDati(InputStream inputStream, List<Filtro> filtri) throws Exception {
		if (filtri != null)
			acceptedRoundCodes.addAll(filtri);
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0);

		int rowsCount = sheet.getPhysicalNumberOfRows();
		List<RichiestaXML> richiesteXML = new ArrayList<RichiestaXML>();
		for (int i = 1; i < rowsCount; i++) {
			Row row = sheet.getRow(i);
			int colCounts = row.getLastCellNum();
			RichiestaXML richiestaXML = new RichiestaXML();
			for (int j = 0; j < colCounts; j++) {
				Cell cell = row.getCell(j);
				if (cell != null)
					aggiungiCampoARichiestaXML(richiestaXML, cell, j);
			}
			String roundCode = richiestaXML.getRoundCode();
			if (acceptRoundCode(roundCode))
				richiesteXML.add(richiestaXML);
		}
		workbook.close();
		return richiesteXML;
	}

	@Override
	public boolean acceptRoundCode(String roundCode) {
		boolean result = false;
		if (acceptedRoundCodes.size() == 0)
			return true;
		else if (roundCode != null)
			for (Filtro filtro : acceptedRoundCodes) {
				if (filtro.getRoundCode().equals(roundCode))
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
		File importDirectory = new File(IMPORT_FOLDER + "/" + operatoreLogistico.getId());
		if (!importDirectory.exists())
			importDirectory.mkdir();
		return importDirectory;
	}

	private void aggiungiCampoARichiestaXML(RichiestaXML richiestaXML, Cell cell, int posizione) throws Exception {
		switch (posizione) {
		case 0:
			richiestaXML.setShipmentId((long) cell.getNumericCellValue() + "");
			break;
		case 1:
			richiestaXML.setIdStop(cell.getStringCellValue());
			break;
		case 2:
			richiestaXML.setDepot(cell.getStringCellValue());
			break;
		case 3:
			richiestaXML.setPieces((int) cell.getNumericCellValue());
			break;
		case 4:
			richiestaXML.setWeight(cell.getNumericCellValue());
			break;
		case 5:
			richiestaXML.setVolume(cell.getNumericCellValue());
			break;
		case 6:
			int data = (int) cell.getNumericCellValue();
			Date dataFormattata = yyyyMMdd.parse(data + "");
			richiestaXML.setDataEarlestPu(dataFormattata);
			break;
		case 7:
			data = (int) cell.getNumericCellValue();
			dataFormattata = yyyyMMdd.parse(data + "");
			richiestaXML.setDataLatestPu(dataFormattata);
			break;
		case 8:
			data = (int) cell.getNumericCellValue();
			dataFormattata = yyyyMMdd.parse(data + "");
			richiestaXML.setDataEarlestDelivery(dataFormattata);
			break;
		case 9:
			data = (int) cell.getNumericCellValue();
			dataFormattata = yyyyMMdd.parse(data + "");
			richiestaXML.setDataLatestDelivery(dataFormattata);
			break;
		case 10:
			richiestaXML.setTimeFromPu(cell.getNumericCellValue());
			break;
		case 11:
			richiestaXML.setTimeToPu(cell.getNumericCellValue());
			break;
		case 12:
			richiestaXML.setTimeFromDelivery(cell.getNumericCellValue());
			break;
		case 13:
			richiestaXML.setTimeToDelivery(cell.getNumericCellValue());
			break;
		case 14:
			richiestaXML.setHandlingType(cell.getStringCellValue());
			break;
		case 15:
			richiestaXML.setPackageType(cell.getStringCellValue().charAt(0));
			break;
		case 16:
			richiestaXML.setCustomer(cell.getStringCellValue());
			break;
		case 17:
			richiestaXML.setAddress(cell.getStringCellValue());
			break;
		case 18:
			richiestaXML.setZipCode((int) cell.getNumericCellValue());
			break;
		case 19:
			richiestaXML.setProvince(cell.getStringCellValue());
			break;
		case 20:
			richiestaXML.setCity(cell.getStringCellValue());
			break;
		case 21:
			richiestaXML.setCountry(cell.getStringCellValue());
			break;
		case 22:
			richiestaXML.setShoppingCentre((int) cell.getNumericCellValue());
			break;
		case 23:
			richiestaXML.setRoundCode(cell.getStringCellValue());
			break;
		case 24:
			richiestaXML.setTntType(cell.getStringCellValue());
			break;
		case 25:
			richiestaXML.setFlagInternational(cell.getStringCellValue());
			break;
		case 26:
			richiestaXML.setDomesticLdv(cell.getStringCellValue());
			break;
		case 27:
			richiestaXML.setTaskField(cell.getStringCellValue());
			break;
		case 28:
			richiestaXML.setTel(cell.getStringCellValue());
			break;
		case 29:
			richiestaXML.setEmail(cell.getStringCellValue());
			break;
		case 30:
			richiestaXML.setHandlingClass((int) cell.getNumericCellValue());
			break;
		case 31:
			richiestaXML.setLocExtId(cell.getStringCellValue());
			break;
		case 32:
			richiestaXML.setStatus(new Integer(cell.getStringCellValue()));
			break;
		}

	}
}
