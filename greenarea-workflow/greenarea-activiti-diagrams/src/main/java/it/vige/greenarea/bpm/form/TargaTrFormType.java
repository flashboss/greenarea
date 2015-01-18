package it.vige.greenarea.bpm.form;

import static it.vige.greenarea.Constants.BASE_URI_USER;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import it.vige.greenarea.dto.RichiestaVeicolo;
import it.vige.greenarea.dto.Veicolo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.form.AbstractFormType;

public class TargaTrFormType extends AbstractFormType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Map<Veicolo, String> values = new HashMap<Veicolo, String>();

	@Override
	public String getName() {
		return "targaTrEnum";
	}

	@Override
	public Object getInformation(String key) {
		if (key.equals("values")) {
			return values;
		}
		return null;
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		getTarghe();
		return (String) modelValue;
	}

	private void getTarghe() {
		Client client = newClient();
		Builder bldr = client.target(BASE_URI_USER + "/findVinVehicles")
				.request(APPLICATION_JSON);
		RichiestaVeicolo richiestaVeicolo = new RichiestaVeicolo();
		List<Veicolo> veicoli = bldr.post(
				entity(richiestaVeicolo, APPLICATION_JSON),
				new GenericType<List<Veicolo>>() {
				});
		values.clear();
		if (veicoli != null)
			for (Veicolo veicolo : veicoli) {
				values.put(veicolo, veicolo.getTarga());
			}
	}

}
