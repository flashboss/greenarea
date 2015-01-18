package it.vige.greenarea.bpm.custom.ui.operatorelogistico.data;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static java.util.Arrays.asList;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.IdentityService;

import com.vaadin.ui.Table;

public class OperatoreLogisticoTable extends Table {

	private static final long serialVersionUID = 3386561634281670377L;
	private DecimalFormat df = new DecimalFormat("#.##");

	/* Table constructors */

	/**
	 * Creates a new empty table.
	 */
	public OperatoreLogisticoTable() {
		addStyleName("op-table");
		setRowHeaderMode(ROW_HEADER_MODE_HIDDEN);
		setFooterVisible(true);
		setColumnFooter("name", counter());
	}

	public String counter() {
		String counter = "0";
		try {
			IdentityService identityService = getDefaultProcessEngine()
					.getIdentityService();
			String operatoreLogistico = get().getLoggedInUser().getId();
			String creditoMobilitaIniziale = identityService.getUserInfo(
					operatoreLogistico, "creditoMobilita");

			RichiestaMissioni richiestaMissioniOP = new RichiestaMissioni();
			richiestaMissioniOP
					.setOperatoriLogistici(asList(new String[] { operatoreLogistico }));

			Client client = newClient();
			Builder bldr = client.target(
					BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
					APPLICATION_JSON);
			List<Missione> response = bldr.post(
					entity(richiestaMissioniOP, APPLICATION_JSON),
					new GenericType<List<Missione>>() {
					});

			double creditoMobilita = 0.0;
			if (response != null) {
				Date date = new Date();
				for (Missione missioneOp : response) {
					if (missioneOp.getDataInizio().compareTo(date) <= 0)
						creditoMobilita += missioneOp.getCreditoMobilita()
								- missioneOp.getBonus();
				}
			}
			double doubleValue = new Double(creditoMobilitaIniziale)
					- creditoMobilita;
			counter = df.format(doubleValue);
		} catch (Exception ex) {

		}
		return counter;
	}
}
