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

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.VEICOLI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;
import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;
import org.slf4j.Logger;

import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

import it.vige.greenarea.bpm.form.VeicoliCollectionFormType;
import it.vige.greenarea.dto.Veicolo;

public class VeicoliCollectionFormPropertyRenderer extends GreenareaAbstractFormPropertyRenderer<Veicolo> {

	private static final long serialVersionUID = -5680213877307810907L;
	private GreenareaPagedTable<Veicolo> table;
	private Map<String, Veicolo> values;
	private Logger logger = getLogger(getClass());

	public VeicoliCollectionFormPropertyRenderer() {
		super(VeicoliCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {
		values = (Map<String, Veicolo>) formProperty.getType().getInformation("values");
		table = new GreenareaPagedTable<Veicolo>(values.values(), getGreenareaFormPropertiesForm());
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(false);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			I18nManager i18nManager = get().getI18nManager();
			String id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "codiceFiliale");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "societaDiTrasporto");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "marcaVeicolo");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "codiceGiro");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "tipoVeicolo");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "classeEcologica");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "tipoAlimentazione");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "targa");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "consumiPresunti");
			table.addContainerProperty(id, String.class, null);
			if (formProperty.isWritable())
				table.addContainerProperty("", HorizontalLayout.class, null);
			for (Map.Entry<String, Veicolo> enumEntry : values.entrySet()) {

				id = enumEntry.getKey();
				Veicolo value = enumEntry.getValue();
				table.addItem(getValues(value, formProperty), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		table.setPageLength(10);
		return new PaginatedCollectionField<Veicolo>(table);
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
		return true;
	}

	private Object[] getValues(Veicolo type, FormProperty formProperty) {
		I18nManager i18nManager = get().getI18nManager();
		List<Object> result = new ArrayList<Object>();
		result.add(type.getCodiceFiliale());
		if (type.getSocietaDiTrasporto() != null && type.getSocietaDiTrasporto().getId() != null
				&& !type.getSocietaDiTrasporto().getId().trim().equals(""))
			result.add(type.getSocietaDiTrasporto());
		else
			result.add(type.getAutista());
		result.add(type.getValori().getBaseName() + " " + type.getValori().getModel());
		result.add(type.getRoundCode());
		result.add(type.getValori().getDescription());
		result.add(i18nManager.getMessage(VEICOLI_TABLE_FIELDS + "euro_value") + " " + type.getValori().getEuro());
		String tipoAlimentazione = type.getValori().getFuel();
		try {
			tipoAlimentazione = i18nManager.getMessage(VEICOLI_TABLE_FIELDS + tipoAlimentazione);
		} catch (Exception ex) {
			logger.error("errore internazionalizzazione ", tipoAlimentazione);
		}
		result.add(tipoAlimentazione);
		result.add(type.getTarga());
		result.add(type.getValori().getConsumiPresunti());
		if (formProperty.isWritable())
			result.add(getButtons(type.toString(), table));
		return result.toArray();
	}

	@Override
	protected HorizontalLayout getButtons(final String item, final Table table) {
		FormProperty operations = getOperations();
		@SuppressWarnings("unchecked")
		Map<String, String> mapOperations = (Map<String, String>) operations.getType().getInformation("values");

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth(20, UNITS_PIXELS);
		buttons.addStyleName(STYLE_DETAIL_BLOCK);
		for (String operation : mapOperations.keySet()) {
			if (operation.equals(MODIFICA.name())) {
				addButton(operation, buttons, item, table);
			}
		}

		Label buttonSpacer = new Label();
		buttons.addComponent(buttonSpacer);
		buttons.setExpandRatio(buttonSpacer, 1.0f);
		return buttons;
	}
}
