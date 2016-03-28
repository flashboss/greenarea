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

import static com.vaadin.ui.AbstractSelect.MultiSelectMode.SIMPLE;
import static it.vige.greenarea.Utilities.giornata;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.IMPATTO_AMBIENTALE_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;

import com.vaadin.ui.Field;

import it.vige.greenarea.bpm.form.ImpattoAmbientaleCollectionFormType;
import it.vige.greenarea.dto.ImpattoAmbientale;

public class ImpattoAmbientaleCollectionFormPropertyRenderer
		extends GreenareaAbstractFormPropertyRenderer<ImpattoAmbientale> {

	private static final long serialVersionUID = -5680213877307810907L;
	private GreenareaPagedTable<ImpattoAmbientale> table;
	private Map<String, ImpattoAmbientale> values;
	private DecimalFormat df = new DecimalFormat("#.##");

	public ImpattoAmbientaleCollectionFormPropertyRenderer() {
		super(ImpattoAmbientaleCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {

		values = (Map<String, ImpattoAmbientale>) formProperty.getType().getInformation("values");
		table = new GreenareaPagedTable<ImpattoAmbientale>(values.values(), getGreenareaFormPropertiesForm(), 10);
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(formProperty.isWritable());
		table.setMultiSelect(true);
		table.setMultiSelectMode(SIMPLE);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			I18nManager i18nManager = get().getI18nManager();
			String id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "tipoalimentazione");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "numeromissioni");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "percentualemissioni");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "numerokm");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "percentualekm");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "numeroemissioni");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + "percentualeemissioni");
			table.addContainerProperty(id, String.class, null);
			boolean captionOK = false;
			for (Map.Entry<String, ImpattoAmbientale> enumEntry : values.entrySet()) {

				id = enumEntry.getKey();
				ImpattoAmbientale value = enumEntry.getValue();
				if (!captionOK) {
					table.setCaption(getPropertyLabel(formProperty, value));
					captionOK = true;
				}
				table.addItem(getValues(value, formProperty), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		table.setPageLength(10);
		return new PaginatedCollectionField<ImpattoAmbientale>(table);
	}

	@Override
	public String getFieldValue(FormProperty formProperty, Field field) {

		assert field == table;

		@SuppressWarnings("unchecked")
		Collection<String> ids = (Collection<String>) field.getValue();
		String ret = "";

		for (String id : ids) {
			ret += id + "|";
		}
		return ret;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		return true;
	}

	private Object[] getValues(ImpattoAmbientale type, FormProperty formProperty) {
		List<Object> result = new ArrayList<Object>();
		String chiave = type.getChiave();
		I18nManager i18nManager = get().getI18nManager();
		chiave = i18nManager.getMessage(IMPATTO_AMBIENTALE_TABLE_FIELDS + chiave);
		result.add(chiave);
		result.add(type.getNumeroMissioni());
		result.add(df.format(type.getPercentualeMissioni()));
		result.add(df.format(type.getNumeroKmPercorsiInGA()));
		result.add(df.format(type.getPercentualeKmPercorsiInGA()));
		result.add(df.format(type.getNumeroEmissioni()));
		result.add(df.format(type.getPercentualeEmissioni()));
		if (formProperty.isWritable())
			result.add(getButtons(type.toString(), table));
		return result.toArray();
	}

	public String getPropertyLabel(FormProperty formProperty, ImpattoAmbientale type) {
		String message = null;
		try {
			message = getMessage(formProperty.getId(), giornata.format(type.getDal()),
					giornata.format(type.getAl()));
		} catch (Exception ex) {
			if (formProperty.getName() != null) {
				return formProperty.getName();
			} else {
				return formProperty.getId();
			}
		}
		return message;
	}
}
