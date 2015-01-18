package it.vige.greenarea.bpm.custom.ui.form;

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PARAMETRI_TS_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static it.vige.greenarea.dto.Operazione.CANCELLAZIONE;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.form.ParametriLetturaCollectionFormType;
import it.vige.greenarea.dto.Parametro;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class ParametriLetturaCollectionFormPropertyRenderer extends
		GreenareaAbstractFormPropertyRenderer<Parametro> {

	private static final long serialVersionUID = -5680213877307810907L;
	private Logger logger = getLogger(getClass());
	private GreenareaPagedTable<Parametro> table;
	private Map<String, Parametro> values;

	public ParametriLetturaCollectionFormPropertyRenderer() {
		super(ParametriLetturaCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {

		values = (Map<String, Parametro>) formProperty.getType()
				.getInformation("values");
		table = new GreenareaPagedTable<Parametro>(values.values(),
				getGreenareaFormPropertiesForm(), 25);
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(false);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			Class<Parametro> genericClass = (Class<Parametro>) values.values()
					.iterator().next().getClass();
			Method[] methods = genericClass.getMethods();
			java.lang.reflect.Field[] fields = genericClass.getDeclaredFields();
			I18nManager i18nManager = get().getI18nManager();
			for (java.lang.reflect.Field field : fields)
				if (visible(methods, field))
					table.addContainerProperty(
							i18nManager.getMessage(PARAMETRI_TS_TABLE_FIELDS
									+ field.getName()), String.class, null);
			if (formProperty.isWritable())
				table.addContainerProperty("", HorizontalLayout.class, null);
			for (Map.Entry<String, Parametro> enumEntry : values.entrySet()) {

				String id = enumEntry.getKey();
				Parametro value = enumEntry.getValue();
				table.addItem(getValues(fields, methods, value, formProperty),
						id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		table.setPageLength(10);
		return new PaginatedCollectionField<Parametro>(table);
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
		if (methodName.startsWith("get")
				&& methodName.substring(3).equalsIgnoreCase(field.getName())
				&& field.getType() != List.class
				&& field.getType() != Collection.class
				&& field.getType() != Map.class
				&& !field.getName().equalsIgnoreCase("id")
				&& !field.getName().equalsIgnoreCase("idGen")
				&& !field.getName().equalsIgnoreCase("valoreMinimo")
				&& !field.getName().equalsIgnoreCase("valoreMassimo")
				&& !field.getName().equalsIgnoreCase("peso"))
			return true;
		return false;
	}

	private Object[] getValues(java.lang.reflect.Field[] fields,
			Method[] methods, Parametro type, FormProperty formProperty) {
		List<Object> result = new ArrayList<Object>();
		try {
			for (java.lang.reflect.Field field : fields)
				for (Method method : methods) {
					if (visible(method, field))
						result.add(method.invoke(type));
				}
			if (formProperty.isWritable())
				result.add(getButtons(type.toString(), table));
		} catch (UnsupportedOperationException | IllegalArgumentException
				| IllegalAccessException | InvocationTargetException e) {
			logger.error("formattazione della collection", e.getMessage());
		}
		return result.toArray();
	}

	@Override
	protected HorizontalLayout getButtons(final String item, final Table table) {
		FormProperty operations = getOperations();
		@SuppressWarnings("unchecked")
		Map<String, String> mapOperations = (Map<String, String>) operations
				.getType().getInformation("values");

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth(53, UNITS_PIXELS);
		buttons.addStyleName(STYLE_DETAIL_BLOCK);
		for (String operation : mapOperations.keySet()) {
			if (operation.equals(MODIFICA.name())
					|| operation.equals(CANCELLAZIONE.name())) {
				addButton(operation, buttons, item, table);
			}
		}

		Label buttonSpacer = new Label();
		buttons.addComponent(buttonSpacer);
		buttons.setExpandRatio(buttonSpacer, 1.0f);
		return buttons;
	}
}
