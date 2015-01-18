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
package it.vige.greenarea.bpm.custom.ui.form.dettagliopolicy;

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.FASCE_ORARIE_PREZZI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PREZZI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaPagedTable;
import it.vige.greenarea.dto.FasciaOraria;
import it.vige.greenarea.dto.Prezzo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;
import org.slf4j.Logger;

public class PrezziTable extends GreenareaPagedTable<Prezzo> {

	private static final long serialVersionUID = 6704792180058166196L;

	private Logger logger = getLogger(getClass());

	public PrezziTable(FormProperty formProperty,
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		super(formProperty.getType().getInformation("values") == null ? null
				: ((FasciaOraria) formProperty.getType().getInformation(
						"values")).getPrezzi(), greenareaFormPropertiesForm);
		FasciaOraria fasciaOraria = (FasciaOraria) formProperty.getType()
				.getInformation("values");
		if (fasciaOraria != null) {
			I18nManager i18nManager = get().getI18nManager();
			setCaption(i18nManager.getMessage(FASCE_ORARIE_PREZZI));
			setRequired(false);
			setRequiredError(i18nManager.getMessage(FORM_FIELD_REQUIRED,
					i18nManager.getMessage(FASCE_ORARIE_PREZZI)));
			setEnabled(true);
			setSelectable(false);
			setStyleName(STYLE_COLLECTION);

			List<Prezzo> prezzi = fasciaOraria.getPrezzi();
			if (prezzi != null && prezzi.size() > 0) {
				Method[] methods = Prezzo.class.getMethods();
				java.lang.reflect.Field[] fields = Prezzo.class
						.getDeclaredFields();
				for (java.lang.reflect.Field field : fields)
					if (visible(methods, field))
						addContainerProperty(
								i18nManager.getMessage(PREZZI_TABLE_FIELDS
										+ field.getName()), String.class, null);
				for (Prezzo prezzo : prezzi) {

					String id = prezzo.toString();
					addItem(getValues(fields, methods, prezzo), id);
				}
			}
			setPageLength(10);
		}
	}

	protected boolean visible(Method method, java.lang.reflect.Field field) {
		String methodName = method.getName();
		if (methodName.startsWith("get")
				&& methodName.substring(3).equalsIgnoreCase(field.getName())
				&& field.getType() != List.class
				&& field.getType() != Collection.class
				&& field.getType() != Map.class
				&& !field.getName().equalsIgnoreCase("id")
				&& !field.getName().equalsIgnoreCase("fasciaOraria"))
			return true;
		return false;
	}

	public boolean visible(Method[] methods, java.lang.reflect.Field field) {
		for (Method method : methods)
			if (visible(method, field))
				return true;
		return false;
	}

	private Object[] getValues(java.lang.reflect.Field[] fields,
			Method[] methods, Prezzo type) {
		List<Object> result = new ArrayList<Object>();
		try {
			for (java.lang.reflect.Field field : fields)
				for (Method method : methods) {
					if (visible(method, field)) {
						Object value = method.invoke(type);
						if (value instanceof String && value != null)
							result.add(uppercaseFirstLetters(((String) value)
									.replaceAll("_", " ")));
						else
							result.add(value);
					}
				}
		} catch (UnsupportedOperationException | IllegalArgumentException
				| IllegalAccessException | InvocationTargetException e) {
			logger.error("formattazione della collection", e.getMessage());
		}
		return result.toArray();
	}
}
