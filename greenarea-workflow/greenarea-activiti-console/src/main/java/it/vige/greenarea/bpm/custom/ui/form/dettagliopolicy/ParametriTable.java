package it.vige.greenarea.bpm.custom.ui.form.dettagliopolicy;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.FASCE_ORARIE_PARAMETRI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PARAMETRI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaFormPropertiesForm;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaPagedTable;
import it.vige.greenarea.dto.FasciaOraria;
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

public class ParametriTable extends GreenareaPagedTable<Parametro> {

	private static final long serialVersionUID = -4439617702228737642L;

	private Logger logger = getLogger(getClass());

	public ParametriTable(FormProperty formProperty,
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		super(formProperty.getType().getInformation("values") == null ? null
				: ((FasciaOraria) formProperty.getType().getInformation(
						"values")).getParametri(), greenareaFormPropertiesForm);
		FasciaOraria fasciaOraria = (FasciaOraria) formProperty.getType()
				.getInformation("values");
		if (fasciaOraria != null) {
			I18nManager i18nManager = get().getI18nManager();
			setCaption(i18nManager.getMessage(FASCE_ORARIE_PARAMETRI));
			setRequired(false);
			setRequiredError(i18nManager.getMessage(FORM_FIELD_REQUIRED,
					i18nManager.getMessage(FASCE_ORARIE_PARAMETRI)));
			setEnabled(true);
			setSelectable(false);
			setStyleName(STYLE_COLLECTION);

			List<Parametro> parametri = fasciaOraria.getParametri();
			if (parametri != null && parametri.size() > 0) {
				Method[] methods = Parametro.class.getMethods();
				java.lang.reflect.Field[] fields = Parametro.class
						.getDeclaredFields();
				for (java.lang.reflect.Field field : fields)
					if (visible(methods, field))
						addContainerProperty(
								i18nManager.getMessage(PARAMETRI_TABLE_FIELDS
										+ field.getName()), String.class, null);
				for (Parametro parametro : parametri) {

					String id = parametro.toString();
					addItem(getValues(fields, methods, parametro, null), id);
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
				&& !field.getName().equalsIgnoreCase("idGen"))
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
			Method[] methods, Parametro type, FormProperty formProperty) {
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
