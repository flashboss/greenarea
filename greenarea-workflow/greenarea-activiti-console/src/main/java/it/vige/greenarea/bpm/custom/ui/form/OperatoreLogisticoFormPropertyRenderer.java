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

import static it.vige.greenarea.Constants.OPERAZIONE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.TUTTI;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import it.vige.greenarea.bpm.form.OperatoreLogisticoFormType;
import it.vige.greenarea.dto.Selezione;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;

public class OperatoreLogisticoFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 1L;

	public OperatoreLogisticoFormPropertyRenderer() {
		super(OperatoreLogisticoFormType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field getPropertyField(FormProperty formProperty) {
		I18nManager i18nManager = get().getI18nManager();
		ComboBox comboBox = new ComboBox(getPropertyLabel(formProperty));
		comboBox.setRequired(formProperty.isRequired());
		comboBox.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		comboBox.setEnabled(formProperty.isWritable());
		comboBox.setNullSelectionAllowed(false);

		Object itemToSelect = null;
		Map<String, String> values = (Map<String, String>) formProperty
				.getType().getInformation("values");
		if (values != null) {
			for (Entry<String, String> enumEntry : values.entrySet()) {
				// Add value and label (if any)
				comboBox.addItem(enumEntry.getKey());

				String selectedValue = formProperty.getValue();
				if ((selectedValue != null && selectedValue.equals(enumEntry
						.getKey())) || itemToSelect == null) {
					itemToSelect = enumEntry.getKey(); // select first
														// element
				}

				if (enumEntry.getValue() != null) {
					String key = enumEntry.getKey();
					if (key.equals(Selezione.TUTTI.name()))
						comboBox.setItemCaption(key,
								i18nManager.getMessage(TUTTI));
					else
						comboBox.setItemCaption(key, enumEntry.getValue());
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

}
