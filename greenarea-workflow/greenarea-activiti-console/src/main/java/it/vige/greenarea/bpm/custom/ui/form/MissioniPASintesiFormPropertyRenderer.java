package it.vige.greenarea.bpm.custom.ui.form;

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_PA_SINTESI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_COLLECTION;
import static it.vige.greenarea.dto.Operazione.CANCELLAZIONE;
import static it.vige.greenarea.dto.Operazione.DETTAGLIO;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;
import it.vige.greenarea.bpm.custom.ui.GreenareaMissioniTableCellStyleGenerator;
import it.vige.greenarea.bpm.form.MissioniPASintesiFormType;
import it.vige.greenarea.dto.DettaglioMissione;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;

import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class MissioniPASintesiFormPropertyRenderer extends
		GreenareaAbstractFormPropertyRenderer<DettaglioMissione> {

	private static final long serialVersionUID = -5680213877307810907L;
	private GreenareaPagedTable<DettaglioMissione> table;
	private Map<String, DettaglioMissione> values;
	private double totaleCrediti;
	private DecimalFormat df = new DecimalFormat("#.##");

	public MissioniPASintesiFormPropertyRenderer() {
		super(MissioniPASintesiFormType.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Field getPropertyField(FormProperty formProperty) {
		totaleCrediti = 0.0;
		values = (Map<String, DettaglioMissione>) formProperty.getType()
				.getInformation("values");
		table = new GreenareaPagedTable<DettaglioMissione>(values.values(),
				getGreenareaFormPropertiesForm(), 10);
		table.setCaption(getPropertyLabel(formProperty));
		table.setRequired(formProperty.isRequired());
		table.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		table.setEnabled(formProperty.isReadable());
		table.setSelectable(false);
		table.setStyleName(STYLE_COLLECTION);
		table.setCellStyleGenerator(new GreenareaMissioniTableCellStyleGenerator());

		if (values != null && values.size() > 0) {
			I18nManager i18nManager = get().getI18nManager();
			String id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "operatorelogistico");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "missioni");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "accessiingatotale");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "accessiingamedia");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "kmpercorsiinga");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "tempotrascorsoinga");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "numerostop");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "numeroconsegneperstop");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "emissionitotali");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "creditidimobilita");
			table.addContainerProperty(id, String.class, null);
			id = i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS
					+ "bonus");
			table.addContainerProperty(id, String.class, null);
			if (formProperty.isWritable())
				table.addContainerProperty("", HorizontalLayout.class, null);

			boolean captionOK = false;
			for (Map.Entry<String, DettaglioMissione> enumEntry : values
					.entrySet()) {

				id = enumEntry.getKey();
				DettaglioMissione value = enumEntry.getValue();
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
		return new PaginatedCollectionField<DettaglioMissione>(table);
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

	private Object[] getValues(DettaglioMissione type, FormProperty formProperty) {
		List<Object> result = new ArrayList<Object>();
		String chiave = type.getChiave();
		if (chiave.equals(MISSIONI_PA_SINTESI_TABLE_FIELDS + "totale")) {
			I18nManager i18nManager = get().getI18nManager();
			chiave = i18nManager.getMessage(chiave);
			type.setCreditidiMobilita(totaleCrediti);
		} else {
			type.setCreditidiMobilita(type.getCreditidiMobilita());
			totaleCrediti += type.getCreditidiMobilita();
		}
		result.add(chiave);
		result.add(type.getMissioni());
		result.add(type.getAccessiInGATotale());
		result.add(df.format(type.getAccessiInGAMedia()));
		result.add(df.format(type.getKmPercorsiInGA()));
		result.add(type.getTempoTrascorsoInGA());
		result.add(type.getNumeroStop());
		result.add(type.getNumeroConsegneperStop());
		result.add(df.format(type.getEmissioniTotali()));
		result.add(df.format(type.getCreditidiMobilita()));
		result.add(df.format(type.getBonus()));
		if (formProperty.isWritable())
			result.add(getButtons(type.toString(), table));
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
		buttons.setWidth(20, UNITS_PIXELS);
		buttons.addStyleName(STYLE_DETAIL_BLOCK);
		for (String operation : mapOperations.keySet()) {
			if (operation.equals(MODIFICA.name())
					|| operation.equals(CANCELLAZIONE.name())
					|| operation.equals(DETTAGLIO.name())) {
				addButton(operation, buttons, item, table);
			}
		}

		Label buttonSpacer = new Label();
		buttons.addComponent(buttonSpacer);
		buttons.setExpandRatio(buttonSpacer, 1.0f);
		return buttons;
	}

	public String getPropertyLabel(FormProperty formProperty,
			DettaglioMissione type) {
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
