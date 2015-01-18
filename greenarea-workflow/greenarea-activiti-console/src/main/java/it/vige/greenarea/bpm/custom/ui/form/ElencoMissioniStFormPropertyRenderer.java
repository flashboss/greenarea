package it.vige.greenarea.bpm.custom.ui.form;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Constants.OPERAZIONE;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import it.vige.greenarea.bpm.form.ElencoMissioniStFormType;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;
import it.vige.greenarea.dto.GreenareaUser;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.identity.LoggedInUser;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;

public class ElencoMissioniStFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 1L;

	public ElencoMissioniStFormPropertyRenderer() {
		super(ElencoMissioniStFormType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field getPropertyField(FormProperty formProperty) {
		ComboBox comboBox = new ComboBox(getPropertyLabel(formProperty));
		comboBox.setRequired(formProperty.isRequired());
		comboBox.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		comboBox.setEnabled(formProperty.isWritable());
		comboBox.setNullSelectionAllowed(false);

		Object itemToSelect = null;
		Map<String, String> values = (Map<String, String>) formProperty
				.getType().getInformation("values");
		String user = ((LoggedInUser) get().getUser()).getId();
		if (values != null) {
			if (!formProperty.isRequired()) {
				comboBox.addItem("");
				comboBox.setItemCaption("", "");
			}
			for (Entry<String, String> enumEntry : values.entrySet()) {
				if (isUser(enumEntry.getKey(), user)) {
					comboBox.addItem(enumEntry.getKey());

					String selectedValue = formProperty.getValue();
					if ((selectedValue != null && selectedValue
							.equals(enumEntry.getKey()))
							|| itemToSelect == null) {
						if (!formProperty.isRequired())
							itemToSelect = "";
						else
							itemToSelect = enumEntry.getKey(); // select first
																// element
					}

					if (enumEntry.getValue() != null) {
						String key = enumEntry.getKey();
						comboBox.setItemCaption(key, enumEntry.getValue());
					}
				}
			}
		}

		// Select first element
		if (itemToSelect != null) {
			comboBox.select(itemToSelect);
		}

		if (formProperty.getId().contains(OPERAZIONE))
			comboBox.setVisible(false);
		return comboBox;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		return true;
	}

	private boolean isUser(String idMissione, String user) {
		Client client = newClient();
		Builder bldr = client
				.target(BASE_URI_RICHIESTE + "/getSintesiMissioni").request(
						APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		richiesta.setId(new Integer(idMissione));
		List<Missione> missioni = bldr.post(
				entity(richiesta, APPLICATION_JSON),
				new GenericType<List<Missione>>() {
				});
		GreenareaUser greenareaUser = missioni.get(0).getVeicolo()
				.getSocietaDiTrasporto();
		if (greenareaUser != null && greenareaUser.getId().equals(user))
			return true;
		else
			return false;
	}

}
