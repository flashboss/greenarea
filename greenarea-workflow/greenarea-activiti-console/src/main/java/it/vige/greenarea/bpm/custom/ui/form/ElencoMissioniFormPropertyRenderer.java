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
package it.vige.greenarea.bpm.custom.ui.form;

import static it.vige.greenarea.Constants.BASE_URI_RICHIESTE;
import static it.vige.greenarea.Constants.OPERAZIONE;
import static javax.ws.rs.client.ClientBuilder.newClient;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;

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

import it.vige.greenarea.bpm.form.ElencoMissioniFormType;
import it.vige.greenarea.dto.Missione;
import it.vige.greenarea.dto.RichiestaMissioni;

public class ElencoMissioniFormPropertyRenderer<T> extends GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 1L;

	public ElencoMissioniFormPropertyRenderer() {
		super(ElencoMissioniFormType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field getPropertyField(FormProperty formProperty) {
		ComboBox comboBox = new ComboBox(getPropertyLabel(formProperty));
		comboBox.setRequired(formProperty.isRequired());
		comboBox.setRequiredError(getMessage(FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		comboBox.setEnabled(formProperty.isWritable());
		comboBox.setNullSelectionAllowed(false);

		Object itemToSelect = null;
		Map<String, String> values = (Map<String, String>) formProperty.getType().getInformation("values");
		String user = ((LoggedInUser) get().getUser()).getId();
		if (values != null) {
			for (Entry<String, String> enumEntry : values.entrySet()) {
				if (isUser(enumEntry.getKey(), user)) {
					comboBox.addItem(enumEntry.getKey());

					String selectedValue = formProperty.getValue();
					if ((selectedValue != null && selectedValue.equals(enumEntry.getKey())) || itemToSelect == null) {
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
		Builder bldr = client.target(BASE_URI_RICHIESTE + "/getSintesiMissioni").request(APPLICATION_JSON);
		RichiestaMissioni richiesta = new RichiestaMissioni();
		richiesta.setId(new Integer(idMissione));
		List<Missione> missioni = bldr.post(entity(richiesta, APPLICATION_JSON), new GenericType<List<Missione>>() {
		});
		if (missioni.get(0).getCompagnia().equals(user))
			return true;
		else
			return false;
	}

}
