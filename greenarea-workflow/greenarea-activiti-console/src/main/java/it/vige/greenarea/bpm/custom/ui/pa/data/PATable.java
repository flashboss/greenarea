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
package it.vige.greenarea.bpm.custom.ui.pa.data;

import static it.vige.greenarea.Constants.BASE_URI_TAP;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;

import com.vaadin.ui.Table;

public class PATable extends Table {

	private static final long serialVersionUID = -5139083447593204933L;

	/**
	 * Creates a new empty table.
	 */
	public PATable() {
		addStyleName("pa-table");
		setRowHeaderMode(ROW_HEADER_MODE_HIDDEN);
		setFooterVisible(true);
		setColumnFooter("name", counter());
	}

	public String counter() {
		String counter = "0";
		try {
			Client client = newClient();
			Builder bldr = client.target(BASE_URI_TAP + "/veicoliInGA").request(APPLICATION_JSON);
			int response = bldr.get(Integer.class);
			return response + "";
		} catch (Exception ex) {

		}
		return counter;
	}
}
