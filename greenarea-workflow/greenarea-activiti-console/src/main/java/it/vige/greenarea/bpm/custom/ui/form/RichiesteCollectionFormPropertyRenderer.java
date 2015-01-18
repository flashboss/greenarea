package it.vige.greenarea.bpm.custom.ui.form;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.RICHIESTE_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static it.vige.greenarea.dto.TipoRichiesta.CONSEGNA;
import static java.util.Arrays.asList;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import it.vige.greenarea.bpm.form.RichiesteCollectionFormType;
import it.vige.greenarea.dto.Pacco;
import it.vige.greenarea.dto.Richiesta;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;

import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;

public class RichiesteCollectionFormPropertyRenderer extends
		GreenareaAbstractFormPropertyRenderer<Richiesta> {

	private static final long serialVersionUID = -5680213877307810907L;
	private GreenareaPagedTable<Richiesta> table;
	private Map<String, Richiesta> values;
	private DecimalFormat df = new DecimalFormat("#.##");

	public RichiesteCollectionFormPropertyRenderer() {
		super(RichiesteCollectionFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {
		values = (Map<String, Richiesta>) formProperty.getType()
				.getInformation("values");
		List<Richiesta> valuesRichieste = new ArrayList<Richiesta>(
				values.values());
		Collections.sort(valuesRichieste, new Comparator<Richiesta>() {

			@Override
			public int compare(Richiesta o1, Richiesta o2) {
				// TODO Auto-generated method stub
				return o1.getDataMissione().compareTo(o2.getDataMissione());
			}

		});
		table = new GreenareaPagedTable<Richiesta>(valuesRichieste,
				getGreenareaFormPropertiesForm());
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(false);
		table.setStyleName(STYLE_COLLECTION);

		if (values != null && values.size() > 0) {
			I18nManager i18nManager = get().getI18nManager();
			String id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS
					+ "shipmentId");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS + "tipo");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS + "stato");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS + "idstop");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS + "numerocolli");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS + "peso");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS + "nomecliente");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(RICHIESTE_TABLE_FIELDS
					+ "indirizzocliente");
			table.addContainerProperty(id, String.class, null);
			if (formProperty.isWritable())
				table.addContainerProperty("", HorizontalLayout.class, null);
			for (Map.Entry<String, Richiesta> enumEntry : values.entrySet()) {

				id = enumEntry.getKey();
				Richiesta value = enumEntry.getValue();
				table.addItem(getValues(value, formProperty), id);

				if (enumEntry.getValue() != null) {
				}
			}
		}
		return new PaginatedCollectionField<Richiesta>(table);
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

	private Object[] getValues(Richiesta type, FormProperty formProperty) {
		List<Object> result = new ArrayList<Object>();
		result.add(type.getShipmentId());
		result.add(type.getTipo());
		result.add(type.getStato());
		result.add(type.getIdStop());
		result.add(type.getPacchi().length);

		List<Pacco> pacchi = asList(type.getPacchi());
		double peso = 0.0;

		for (Pacco pacco : pacchi) {
			peso += new Double(pacco.getAttributi().get("Weight"));
		}

		result.add(df.format(peso));
		if (type.getTipo().equals(CONSEGNA.name())) {
			result.add(type.getToName());
			result.add(type.getToAddress());
		} else {
			result.add(type.getFromName());
			result.add(type.getFromAddress());
		}
		if (formProperty.isWritable())
			result.add(getButtons(type.toString(), table));
		return result.toArray();
	}
}
