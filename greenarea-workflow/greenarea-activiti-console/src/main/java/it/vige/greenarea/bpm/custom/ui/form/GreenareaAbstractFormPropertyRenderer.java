package it.vige.greenarea.bpm.custom.ui.form;

import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import static com.vaadin.ui.Alignment.BOTTOM_RIGHT;
import static com.vaadin.ui.themes.BaseTheme.BUTTON_LINK;
import static it.vige.greenarea.Constants.OPERAZIONE;
import static it.vige.greenarea.dto.Operazione.CANCELLAZIONE;
import static it.vige.greenarea.dto.Operazione.DETTAGLIO;
import static it.vige.greenarea.dto.Operazione.INSERIMENTO;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent.TYPE_SUBMIT;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;
import org.activiti.engine.impl.form.EnumFormType;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.form.AbstractFormPropertyRenderer;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public abstract class GreenareaAbstractFormPropertyRenderer<T> extends
		AbstractFormPropertyRenderer {

	private static final long serialVersionUID = -4969880376947019989L;
	private GreenareaFormPropertiesForm greenareaFormPropertiesForm;
	private FormProperty operations;
	private FormProperty mainProperty;

	public GreenareaAbstractFormPropertyRenderer(
			Class<? extends FormType> formType) {
		super(formType);
	}

	public GreenareaFormPropertiesForm getGreenareaFormPropertiesForm() {
		return greenareaFormPropertiesForm;
	}

	public void setGreenareaFormPropertiesForm(
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		this.greenareaFormPropertiesForm = greenareaFormPropertiesForm;

		List<FormProperty> formProperties = greenareaFormPropertiesForm
				.getGreenareaFormPropertiesComponent().getFormProperties();
		for (FormProperty formProperty : formProperties) {
			if (formProperty.getType() instanceof EnumFormType
					&& formProperty.getId().contains(OPERAZIONE)) {
				operations = formProperty;
			} else
				mainProperty = formProperty;
		}
	}

	protected FormProperty getOperations() {
		return operations;
	}

	protected FormProperty getMainProperty() {
		return mainProperty;
	}

	protected void setOperation(Map<String, String> formProperties,
			String operation) {
		for (String formProperty : formProperties.keySet()) {
			if (formProperty.contains(OPERAZIONE)) {
				formProperties.put(formProperty, operation);
			}
		}
	}

	protected void addButton(String operation, HorizontalLayout buttons,
			final String item, final Table table) {
		final String finalOperation = operation;
		final Button button = new Button();
		if (operation.equals(MODIFICA.name()))
			button.setIcon(new ThemeResource("img/edit.png"));
		else if (operation.equals(CANCELLAZIONE.name()))
			button.setIcon(new ThemeResource("img/delete.png"));
		else
			button.setIcon(new ThemeResource("img/task-16.png"));
		button.addStyleName(BUTTON_LINK);
		I18nManager i18nManager = get().getI18nManager();
		button.setDescription(i18nManager.getMessage(operation));
		buttons.addComponent(button);
		buttons.setComponentAlignment(button, BOTTOM_RIGHT);
		button.addListener(new ClickListener() {

			private static final long serialVersionUID = -6091586145870618870L;

			public void buttonClick(ClickEvent event) {
				// Extract the submitted values from the form. Throws
				// exception
				// when validation fails.
				try {
					table.select(item);
					Map<String, String> formProperties = greenareaFormPropertiesForm
							.getGreenareaFormPropertiesComponent()
							.getFormPropertyValues();
					setOperation(formProperties, finalOperation);
					greenareaFormPropertiesForm.getMainTitle()
							.setPropertyDataSource(
									new ObjectProperty<String>(
											greenareaFormPropertiesForm
													.getMainTitle().getValue()
													+ " > " + finalOperation,
											String.class));
					FormPropertiesEvent formPropertiesEvent = greenareaFormPropertiesForm.new FormPropertiesEvent(
							greenareaFormPropertiesForm, TYPE_SUBMIT,
							formProperties);
					greenareaFormPropertiesForm.fireEvent(formPropertiesEvent);
					button.setComponentError(null);
				} catch (InvalidValueException ive) {
					// Error is presented to user by the form component
				}
			}
		});

	}

	protected HorizontalLayout getButtons(final String item, final Table table) {
		FormProperty operations = getOperations();
		@SuppressWarnings("unchecked")
		Map<String, String> mapOperations = (Map<String, String>) operations
				.getType().getInformation("values");

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setWidth(80, UNITS_PIXELS);
		buttons.addStyleName(STYLE_DETAIL_BLOCK);
		for (String operation : mapOperations.keySet()) {
			if (operation.equals(MODIFICA.name())
					|| operation.equals(CANCELLAZIONE.name())
					|| operation.equals(DETTAGLIO.name())
					|| operation.equals(INSERIMENTO.name())) {
				addButton(operation, buttons, item, table);
			}
		}

		Label buttonSpacer = new Label();
		buttons.addComponent(buttonSpacer);
		buttons.setExpandRatio(buttonSpacer, 1.0f);
		return buttons;
	}

	protected abstract boolean visible(Method method,
			java.lang.reflect.Field field);

	public boolean visible(Method[] methods, java.lang.reflect.Field field) {
		for (Method method : methods)
			if (visible(method, field))
				return true;
		return false;
	}

	@Override
	public String getPropertyLabel(FormProperty formProperty) {
		String message = null;
		try {
			message = getMessage(formProperty.getId());
		} catch (Exception ex) {
			if (formProperty.getName() != null) {
				return formProperty.getName();
			} else {
				return formProperty.getId();
			}
		}
		return message;
	}

	@Override
	public String getMessage(String key, Object... params) {
		return ExplorerApp.get().getI18nManager().getMessage(key, params);
	}
}
