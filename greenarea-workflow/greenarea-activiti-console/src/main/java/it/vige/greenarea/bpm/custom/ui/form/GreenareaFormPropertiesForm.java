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

import static com.vaadin.ui.Alignment.BOTTOM_RIGHT;
import static it.vige.greenarea.Constants.OPERAZIONE;
import static it.vige.greenarea.dto.Operazione.CANCELLAZIONE;
import static it.vige.greenarea.dto.Operazione.DETTAGLIO;
import static it.vige.greenarea.dto.Operazione.INSERIMENTO;
import static it.vige.greenarea.dto.Operazione.MODIFICA;
import static org.activiti.explorer.Messages.TASK_COMPLETE;
import static org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent.TYPE_SUBMIT;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.ui.form.FormPropertiesForm;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class GreenareaFormPropertiesForm extends FormPropertiesForm {

	private static final long serialVersionUID = -3197331726904715949L;
	private Label mainTitle;
	private HorizontalLayout buttons;

	public HorizontalLayout getButtons() {
		return buttons;
	}

	public void setButtons(HorizontalLayout buttons) {
		this.buttons = buttons;
	}

	@Override
	protected void initFormPropertiesComponent() {
		formPropertiesComponent = new GreenareaFormPropertiesComponent(this);
		addComponent(formPropertiesComponent);
	}

	public void setFormProperties(List<FormProperty> formProperties) {
		// Component will refresh it's components based on the passed properties
		formPropertiesComponent.setFormProperties(formProperties);
		addButtons();
		addListeners();
	}

	@Override
	protected void initButtons() {
	}

	private List<String> getOperationsFromRenderers(
			GreenareaFormPropertiesComponent greenareaFormPropertiesComponent) {
		List<String> operationsFromRenderers = new ArrayList<String>();
		operationsFromRenderers.add(MODIFICA.name());
		operationsFromRenderers.add(DETTAGLIO.name());
		operationsFromRenderers.add(INSERIMENTO.name());
		operationsFromRenderers.add(CANCELLAZIONE.name());
		return operationsFromRenderers;
	}

	private void addButtons() {
		GreenareaFormPropertiesComponent greenareaFormPropertiesComponent = (GreenareaFormPropertiesComponent) formPropertiesComponent;
		List<String> operationsFromRenderers = getOperationsFromRenderers(greenareaFormPropertiesComponent);
		FormProperty operations = greenareaFormPropertiesComponent
				.getOperations();
		if (operations != null) {
			@SuppressWarnings("unchecked")
			Map<String, String> mapOperations = (Map<String, String>) operations
					.getType().getInformation("values");

			buttons = new HorizontalLayout();
			buttons.setSpacing(true);
			buttons.setWidth(100, UNITS_PERCENTAGE);
			buttons.addStyleName(STYLE_DETAIL_BLOCK);
			for (String operation : mapOperations.keySet()) {
				if (!operationsFromRenderers.contains(operation)) {
					final String finalOperation = operation;
					final Button button = new Button();
					button.setCaption(i18nManager.getMessage(operation));
					buttons.addComponent(button);
					buttons.setComponentAlignment(button, BOTTOM_RIGHT);
					button.addListener(new ClickListener() {

						private static final long serialVersionUID = -6091586145870618870L;

						public void buttonClick(ClickEvent event) {
							// Extract the submitted values from the form.
							// Throws
							// exception
							// when validation fails.
							try {
								Map<String, String> formProperties = formPropertiesComponent
										.getFormPropertyValues();
								setOperation(formProperties, finalOperation);
								mainTitle
										.setPropertyDataSource(new ObjectProperty<String>(
												mainTitle.getValue() + " > "
														+ finalOperation,
												String.class));
								fireEvent(new FormPropertiesEvent(
										GreenareaFormPropertiesForm.this,
										TYPE_SUBMIT, formProperties));
								button.setComponentError(null);
							} catch (InvalidValueException ive) {
								// Error is presented to user by the form
								// component
							}
						}
					});
				}
			}

			Label buttonSpacer = new Label();
			buttons.addComponent(buttonSpacer);
			buttons.setExpandRatio(buttonSpacer, 1.0f);
			addComponent(buttons);
		} else {
			submitFormButton = new Button();
			cancelFormButton = new Button();

			HorizontalLayout buttons = new HorizontalLayout();
			buttons.setSpacing(true);
			buttons.setWidth(100, UNITS_PERCENTAGE);
			buttons.addStyleName(STYLE_DETAIL_BLOCK);
			buttons.addComponent(submitFormButton);
			buttons.setComponentAlignment(submitFormButton, BOTTOM_RIGHT);

			// buttons.addComponent(cancelFormButton);
			// buttons.setComponentAlignment(cancelFormButton, BOTTOM_RIGHT);

			Label buttonSpacer = new Label();
			buttons.addComponent(buttonSpacer);
			buttons.setExpandRatio(buttonSpacer, 1.0f);
			addComponent(buttons);
			setSubmitButtonCaption(i18nManager.getMessage(TASK_COMPLETE));
			// setCancelButtonCaption(i18nManager.getMessage(TASK_RESET_FORM));
		}
	}

	@Override
	protected void initListeners() {
	}

	private void addListeners() {
		FormProperty operations = ((GreenareaFormPropertiesComponent) formPropertiesComponent)
				.getOperations();
		if (operations == null) {
			submitFormButton.addListener(new ClickListener() {

				private static final long serialVersionUID = -6091586145870618870L;

				public void buttonClick(ClickEvent event) {
					// Extract the submitted values from the form. Throws
					// exception
					// when validation fails.
					try {
						Map<String, String> formProperties = formPropertiesComponent
								.getFormPropertyValues();
						fireEvent(new FormPropertiesEvent(
								GreenareaFormPropertiesForm.this, TYPE_SUBMIT,
								formProperties));
						submitFormButton.setComponentError(null);
					} catch (InvalidValueException ive) {
						// Error is presented to user by the form component
					}
				}
			});

			// cancelFormButton.addListener(new ClickListener() {
			//
			// private static final long serialVersionUID =
			// -8980500491522472381L;
			//
			// public void buttonClick(ClickEvent event) {
			// fireEvent(new FormPropertiesEvent(
			// GreenareaFormPropertiesForm.this, TYPE_CANCEL));
			// submitFormButton.setComponentError(null);
			// }
			// });
		}
	}

	private void setOperation(Map<String, String> formProperties,
			String operation) {
		for (String formProperty : formProperties.keySet()) {
			if (formProperty.contains(OPERAZIONE)) {
				formProperties.put(formProperty, operation);
			}
		}
	}

	public Label getMainTitle() {
		return mainTitle;
	}

	public void setMainTitle(Label mainTitle) {
		this.mainTitle = mainTitle;
	}

	public GreenareaFormPropertiesComponent getGreenareaFormPropertiesComponent() {
		return (GreenareaFormPropertiesComponent) formPropertiesComponent;
	}

	@Override
	public void fireEvent(Component.Event event) {
		super.fireEvent(event);

	}

	public Button getSubmitFormButton() {
		return submitFormButton;
	}
}
