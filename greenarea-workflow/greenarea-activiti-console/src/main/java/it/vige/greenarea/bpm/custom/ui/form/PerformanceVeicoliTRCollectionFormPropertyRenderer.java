package it.vige.greenarea.bpm.custom.ui.form;

import static com.vaadin.ui.AbstractSelect.MultiSelectMode.SIMPLE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.PERFORMANCE_VEICOLI_TR_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.bpm.form.PerformanceVeicoliTRCollectionFormType;
import it.vige.greenarea.dto.PerformanceVeicoli;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;
import org.slf4j.Logger;

import com.vaadin.ui.Field;
import com.vaadin.ui.Table;

public class PerformanceVeicoliTRCollectionFormPropertyRenderer extends
		GreenareaAbstractFormPropertyRenderer<PerformanceVeicoli> {

	private static final long serialVersionUID = -5680213877307810907L;
	private Table table;
	private Map<String, PerformanceVeicoli> values;
	private Logger logger = getLogger(getClass());
	private DecimalFormat df = new DecimalFormat("#.##");

	public PerformanceVeicoliTRCollectionFormPropertyRenderer() {
		super(PerformanceVeicoliTRCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {

		values = (Map<String, PerformanceVeicoli>) formProperty.getType()
				.getInformation("values");
		table = new GreenareaPagedTable<PerformanceVeicoli>(values.values(),
				getGreenareaFormPropertiesForm());
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(formProperty.isWritable());
		table.setMultiSelect(true);
		table.setMultiSelectMode(SIMPLE);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			I18nManager i18nManager = get().getI18nManager();
			String id = i18nManager
					.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS + "targa");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "tipoalimentazione");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "classeEcologica");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "numeromissioni");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "numerokm");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "consumoTotale");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "numeroMedioConsegneAMissione");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "percentualeConsegneABuonFine");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
					+ "numeroMedioDiRitiriAMissione");
			table.addContainerProperty(id, String.class, null);
			boolean captionOK = false;
			for (Map.Entry<String, PerformanceVeicoli> enumEntry : values
					.entrySet()) {

				id = enumEntry.getKey();
				PerformanceVeicoli value = enumEntry.getValue();
				if (!captionOK) {
					table.setCaption(getPropertyLabel(formProperty, value));
					captionOK = true;
				}
				table.addItem(getValues(value, formProperty), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		return table;
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

	private Object[] getValues(PerformanceVeicoli type,
			FormProperty formProperty) {
		List<Object> result = new ArrayList<Object>();
		result.add(type.getTarga());
		String tipoAlimentazione = type.getTipoAlimentazione().name();
		I18nManager i18nManager = get().getI18nManager();
		try {
			tipoAlimentazione = i18nManager
					.getMessage(PERFORMANCE_VEICOLI_TR_TABLE_FIELDS
							+ tipoAlimentazione);
		} catch (Exception ex) {
			logger.error("errore internazionalizzazione ", tipoAlimentazione);
		}
		result.add(tipoAlimentazione);
		result.add(type.getClasseEcologica());
		result.add(type.getNumeroMissioni());
		result.add(df.format(type.getNumeroKmPercorsiInGA()));
		result.add(df.format(type.getConsumoTotale()));
		result.add(type.getNumeroMedioConsegneAMissione());
		result.add(df.format(type.getPercentualeConsegneABuonFine()));
		result.add(type.getNumeroMedioDiRitiriAMissione());
		if (formProperty.isWritable())
			result.add(getButtons(type.toString(), table));
		return result.toArray();
	}

	public String getPropertyLabel(FormProperty formProperty,
			PerformanceVeicoli type) {
		DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
		String message = null;
		try {
			message = getMessage(formProperty.getId(),
					dateFormat.format(type.getDal()),
					dateFormat.format(type.getAl()));
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
