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
package it.tvige.tap.test.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import org.junit.Test;
import org.slf4j.Logger;

import it.vige.tap.spreceiver.ws.GroupData;
import it.vige.tap.spreceiver.ws.OutData;
import it.vige.tap.spreceiver.ws.ParamData;
import it.vige.tap.spreceiver.ws.SpPushDataService;
import it.vige.tap.spreceiver.ws.SpPushDataServiceService;

public class TapConnectionTest {

	private static Logger logger = getLogger(TapConnectionTest.class);

	@Test
	public void testKeepAlive() {
		SpPushDataServiceService service = new SpPushDataServiceService();
		SpPushDataService spPushDataService = service.getSpPushDataServicePort();
		spPushDataService.keepAlive();
	}

	@Test
	public void testPushData() {
		SpPushDataServiceService service = new SpPushDataServiceService();
		SpPushDataService spPushDataService = service.getSpPushDataServicePort();

		BindingProvider provider = (BindingProvider) spPushDataService;
		Map<String, Object> req_ctx = provider.getRequestContext();
		/*
		 * req_ctx.put(ENDPOINT_ADDRESS_PROPERTY,
		 * "http://82.107.53.122/greenarea-service/SpPushDataService");
		 */
		req_ctx.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/greenarea-service/SpPushDataService");
		OutData parameters = new OutData();
		parameters.setCodeFunction("code_function");
		try {
			parameters.setDate(asXMLGregorianCalendar(new Date()));
		} catch (DatatypeConfigurationException e) {
			logger.error("Errore di conversione del XMLGregorianCalendar", e);
		}
		parameters.setVin("vin");
		parameters.setServiceProvider("service_provider");
		GroupData groupData = new GroupData();
		groupData.setName("name");
		ParamData paramData = new ParamData();
		paramData.setName("name_p");
		paramData.setValue("value_p");
		groupData.getParams().add(paramData);
		parameters.getGroups().add(groupData);
		spPushDataService.pushData(parameters);
	}

	public XMLGregorianCalendar asXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		} else {
			DatatypeFactory df = DatatypeFactory.newInstance();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(date.getTime());
			return df.newXMLGregorianCalendar(gc);
		}
	}

}
