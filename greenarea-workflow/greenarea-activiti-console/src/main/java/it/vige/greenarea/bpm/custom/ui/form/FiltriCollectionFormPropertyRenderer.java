package it.vige.greenarea.bpm.custom.ui.form;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.FILTRI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.form.FiltriCollectionFormType;
import it.vige.greenarea.dto.Filtro;

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

public class FiltriCollectionFormPropertyRenderer extends
		GreenareaAbstractFormPropertyRenderer<Filtro> {

	private static final long serialVersionUID = -5680213877307810907L;
	private Logger logger = getLogger(getClass());
	private GreenareaPagedTable<Filtro> table;
	private Map<String, Filtro> values;

	public FiltriCollectionFormPropertyRenderer() {
		super(FiltriCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {

		values = (Map<String, Filtro>) formProperty.getType().getInformation(
				"values");
		table = new GreenareaPagedTable<Filtro>(values.values(),
				getGreenareaFormPropertiesForm());
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(formProperty.isWritable());
		table.setMultiSelect(false);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			Class<Filtro> genericClass = (Class<Filtro>) values.values()
					.iterator().next().getClass();
			Method[] methods = genericClass.getMethods();
			java.lang.reflect.Field[] fields = genericClass.getDeclaredFields();
			I18nManager i18nManager = get().getI18nManager();
			for (java.lang.reflect.Field field : fields)
				if (visible(methods, field))
					table.addContainerProperty(
							i18nManager.getMessage(FILTRI_TABLE_FIELDS
									+ field.getName()), String.class, null);
			for (Map.Entry<String, Filtro> enumEntry : values.entrySet()) {

				String id = enumEntry.getKey();
				Filtro value = enumEntry.getValue();
				table.addItem(getValues(fields, methods, value), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		return new PaginatedCollectionField<Filtro>(table);
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
				&& !field.getName().equalsIgnoreCase("id"))
			return true;
		return false;
	}

	private Object[] getValues(java.lang.reflect.Field[] fields,
			Method[] methods, Filtro type) {
		List<Object> result = new ArrayList<Object>();
		try {
			for (java.lang.reflect.Field field : fields)
				for (Method method : methods) {
					if (visible(method, field))
						result.add(method.invoke(type));
				}
		} catch (UnsupportedOperationException | IllegalArgumentException
				| IllegalAccessException | InvocationTargetException e) {
			logger.error("formattazione della collection", e.getMessage());
		}
		return result.toArray();
	}
}
