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

import static it.vige.greenarea.Utilities.uppercaseFirstLetters;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PREZZI_TABLE_FIELDS;
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

import it.vige.greenarea.bpm.form.PrezziCollectionFormType;
import it.vige.greenarea.dto.Prezzo;

public class PrezziCollectionFormPropertyRenderer extends GreenareaAbstractFormPropertyRenderer<Prezzo> {

	private static final long serialVersionUID = -5680213877307810907L;
	private Logger logger = getLogger(getClass());
	private GreenareaPagedTable<Prezzo> table;
	private Map<String, Prezzo> values;

	public PrezziCollectionFormPropertyRenderer() {
		super(PrezziCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {
		values = (Map<String, Prezzo>) formProperty.getType().getInformation("values");
		table = new GreenareaPagedTable<Prezzo>(values.values(), getGreenareaFormPropertiesForm());
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(formProperty.isWritable());
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			Class<Prezzo> genericClass = (Class<Prezzo>) values.values().iterator().next().getClass();
			Method[] methods = genericClass.getMethods();
			java.lang.reflect.Field[] fields = genericClass.getDeclaredFields();
			I18nManager i18nManager = get().getI18nManager();
			for (java.lang.reflect.Field field : fields)
				if (visible(methods, field))
					table.addContainerProperty(i18nManager.getMessage(PREZZI_TABLE_FIELDS + field.getName()),
							String.class, null);
			for (Map.Entry<String, Prezzo> enumEntry : values.entrySet()) {

				String id = enumEntry.getKey();
				Prezzo value = enumEntry.getValue();
				table.addItem(getValues(fields, methods, value), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		return new PaginatedCollectionField<Prezzo>(table);
	}

	@Override
	public String getFieldValue(FormProperty formProperty, Field field) {
		@SuppressWarnings("unchecked")
		Collection<String> ids = (Collection<String>) table.getValue();
		String ret = "";

		for (String id : ids) {
			ret += id + "|";
		}
		return ret;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		String methodName = method.getName();
		if (methodName.startsWith("get") && methodName.substring(3).equalsIgnoreCase(field.getName())
				&& field.getType() != List.class && field.getType() != Collection.class && field.getType() != Map.class
				&& !field.getName().equalsIgnoreCase("id") && !field.getName().equalsIgnoreCase("fasciaOraria"))
			return true;
		return false;
	}

	private Object[] getValues(java.lang.reflect.Field[] fields, Method[] methods, Prezzo type) {
		List<Object> result = new ArrayList<Object>();
		try {
			for (java.lang.reflect.Field field : fields)
				for (Method method : methods) {
					if (visible(method, field)) {
						Object value = method.invoke(type);
						if (value instanceof String && value != null)
							result.add(uppercaseFirstLetters(((String) value).replaceAll("_", " ")));
						else
							result.add(value);
					}
				}
		} catch (UnsupportedOperationException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			logger.error("formattazione della collection", e.getMessage());
		}
		return result.toArray();
	}
}
