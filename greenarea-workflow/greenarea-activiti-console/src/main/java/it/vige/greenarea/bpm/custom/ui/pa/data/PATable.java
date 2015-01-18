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
			Builder bldr = client.target(BASE_URI_TAP + "/veicoliInGA")
					.request(APPLICATION_JSON);
			int response = bldr.get(Integer.class);
			return response + "";
		} catch (Exception ex) {

		}
		return counter;
	}
}
