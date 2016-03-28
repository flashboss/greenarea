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

import static it.vige.greenarea.Constants.CENTRALE;
import static it.vige.greenarea.Utilities.uppercaseFirstLetters;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.FASCE_ORARIE_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;
import org.slf4j.Logger;

import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;

import it.vige.greenarea.bpm.form.FasceOrarieCollectionFormType;
import it.vige.greenarea.dto.FasciaOraria;

public class FasceOrarieCollectionFormPropertyRenderer extends GreenareaAbstractFormPropertyRenderer<FasciaOraria> {

	private static final long serialVersionUID = -5680213877307810907L;
	private Logger logger = getLogger(getClass());
	private GreenareaPagedTable<FasciaOraria> table;
	private Map<String, FasciaOraria> values;

	public FasceOrarieCollectionFormPropertyRenderer() {
		super(FasceOrarieCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {
		values = (Map<String, FasciaOraria>) formProperty.getType().getInformation("values");
		table = new GreenareaPagedTable<FasciaOraria>(values.values(), getGreenareaFormPropertiesForm(), 10);
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(false);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			Class<FasciaOraria> genericClass = (Class<FasciaOraria>) values.values().iterator().next().getClass();
			Method[] methods = genericClass.getMethods();
			java.lang.reflect.Field[] fields = genericClass.getDeclaredFields();
			I18nManager i18nManager = get().getI18nManager();
			for (java.lang.reflect.Field field : fields)
				if (visible(methods, field))
					table.addContainerProperty(i18nManager.getMessage(FASCE_ORARIE_TABLE_FIELDS + field.getName()),
							String.class, null);
			if (formProperty.isWritable())
				table.addContainerProperty("", HorizontalLayout.class, null);
			for (Map.Entry<String, FasciaOraria> enumEntry : values.entrySet()) {

				String id = enumEntry.getKey();
				FasciaOraria value = enumEntry.getValue();
				table.addItem(getValues(fields, methods, value, formProperty), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		table.setPageLength(10);
		return new PaginatedCollectionField<FasciaOraria>(table);
	}

	@Override
	public String getFieldValue(FormProperty formProperty, Field field) {
		String id = "";
		if (table != null)
			id = (String) table.getValue();
		return id;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		String methodName = method.getName();
		if (methodName.startsWith("get") && methodName.substring(3).equalsIgnoreCase(field.getName())
				&& field.getType() != List.class && field.getType() != Collection.class && field.getType() != Map.class
				&& !field.getName().equalsIgnoreCase("id") && !field.getName().equalsIgnoreCase("dataInizio")
				&& !field.getName().equalsIgnoreCase("dataFine") && !field.getName().equalsIgnoreCase("orarioInizio")
				&& !field.getName().equalsIgnoreCase("orarioFine") && !field.getName().equalsIgnoreCase("pa")
				&& !field.getName().equalsIgnoreCase("tipologiaClassifica"))
			return true;
		return false;
	}

	private Object[] getValues(java.lang.reflect.Field[] fields, Method[] methods, FasciaOraria type,
			FormProperty formProperty) {
		List<Object> result = new ArrayList<Object>();
		try {
			for (java.lang.reflect.Field field : fields)
				for (Method method : methods) {
					if (visible(method, field)) {
						Object value = method.invoke(type);
						if (value instanceof String && value != null)
							if (field.getName().equals("ga"))
								result.add(CENTRALE);
							else
								result.add(uppercaseFirstLetters(
										((String) value).replaceAll("_", " ").replaceAll(" PER CENTO", "%")));
						else
							result.add(value);
					}
				}
			if (formProperty.isWritable())
				result.add(getButtons(type.toString(), table));
		} catch (UnsupportedOperationException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			logger.error("formattazione della collection", e.getMessage());
		}
		return result.toArray();
	}
}
