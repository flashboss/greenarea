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
package it.vige.greenarea.bpm.custom.ui.form.dettagliopolicy;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_POLICY_BUTTON;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_POLICY_DETTAGLIO_POLICY;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_POLICY_FASCIA_ORARIA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_POLICY_PERIODO_VALIDITA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_POLICY_TITLE;
import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.bpm.custom.ui.form.GreenareaAbstractFormPropertyRenderer;
import it.vige.greenarea.dto.FasciaOraria;

import java.util.Collection;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ui.custom.PopupWindow;
import org.activiti.explorer.ui.event.SubmitEvent;
import org.activiti.explorer.ui.event.SubmitEventListener;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class DettaglioPolicyField<T> extends HorizontalLayout implements Field {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TextField wrappedField;
	protected Label validityLabel;
	protected Label fasciaOrariaLabel;
	protected Button policyDetailsButton;

	public DettaglioPolicyField(
			FormProperty formProperty,
			GreenareaAbstractFormPropertyRenderer<T> greenareaAbstractFormPropertyRenderer,
			FasciaOraria fasciaOraria) {
		I18nManager i18nManager = get().getI18nManager();
		String caption = i18nManager.getMessage(DETTAGLIO_POLICY_TITLE);
		setSpacing(true);
		setCaption(caption);
		setHeight(Sizeable.SIZE_UNDEFINED, 0);
		validityLabel = new Label();
		validityLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_POLICY_PERIODO_VALIDITA)
				+ " "
				+ (fasciaOraria != null ? fasciaOraria.getValidita() : ""));
		fasciaOrariaLabel = new Label();
		fasciaOrariaLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_POLICY_FASCIA_ORARIA)
				+ " "
				+ (fasciaOraria != null ? fasciaOraria.getNome() : ""));
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.addComponent(validityLabel);
		vLayout.addComponent(fasciaOrariaLabel);
		vLayout.setStyleName("dettaglio-policy-label");

		policyDetailsButton = new Button();
		final String dettaglioPolicy = i18nManager
				.getMessage(DETTAGLIO_POLICY_DETTAGLIO_POLICY);
		policyDetailsButton.setCaption(i18nManager
				.getMessage(DETTAGLIO_POLICY_BUTTON));
		final ParametriTable parametriTable = new ParametriTable(formProperty,
				greenareaAbstractFormPropertyRenderer
						.getGreenareaFormPropertiesForm());
		final PrezziTable prezziTable = new PrezziTable(formProperty,
				greenareaAbstractFormPropertyRenderer
						.getGreenareaFormPropertiesForm());
		policyDetailsButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				PopupWindow window = new PopupWindow(dettaglioPolicy);
				VerticalLayout verticalLayout = new VerticalLayout();
				window.setContent(verticalLayout);
				verticalLayout.addComponent(parametriTable);
				verticalLayout.addComponent(parametriTable.createControls());
				verticalLayout.addComponent(prezziTable);
				verticalLayout.addComponent(prezziTable.createControls());
				verticalLayout.addStyleName("main-table");
				window.addListener(new SubmitEventListener() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void submitted(SubmitEvent event) {

					}

					@Override
					protected void cancelled(SubmitEvent event) {
					}
				});
				window.setWidth(900, Sizeable.UNITS_PIXELS);
				window.setHeight(500, Sizeable.UNITS_PIXELS);
				window.setModal(true);
				window.center();
				get().getViewManager().showPopupWindow(window);
			}
		});

		addComponent(vLayout);

		addComponent(policyDetailsButton);

		// Invisible textfield, only used as wrapped field
		wrappedField = new TextField();
		wrappedField.setVisible(false);
		addComponent(wrappedField);
	}

	public boolean isInvalidCommitted() {
		return wrappedField.isInvalidCommitted();
	}

	public void setInvalidCommitted(boolean isCommitted) {
		wrappedField.setInvalidCommitted(isCommitted);
	}

	public void commit() throws SourceException, InvalidValueException {
		wrappedField.commit();
	}

	public void discard() throws SourceException {
		wrappedField.discard();
	}

	public boolean isWriteThrough() {
		return wrappedField.isWriteThrough();
	}

	public void setWriteThrough(boolean writeThrough) throws SourceException,
			InvalidValueException {
		wrappedField.setWriteThrough(true);
	}

	public boolean isReadThrough() {
		return wrappedField.isReadThrough();
	}

	public void setReadThrough(boolean readThrough) throws SourceException {
		wrappedField.setReadThrough(readThrough);
	}

	public boolean isModified() {
		return wrappedField.isModified();
	}

	public void addValidator(Validator validator) {
		wrappedField.addValidator(validator);
	}

	public void removeValidator(Validator validator) {
		wrappedField.removeValidator(validator);
	}

	public Collection<Validator> getValidators() {
		return wrappedField.getValidators();
	}

	public boolean isValid() {
		return wrappedField.isValid();
	}

	public void validate() throws InvalidValueException {
		wrappedField.validate();
	}

	public boolean isInvalidAllowed() {
		return wrappedField.isInvalidAllowed();
	}

	public void setInvalidAllowed(boolean invalidValueAllowed)
			throws UnsupportedOperationException {
		wrappedField.setInvalidAllowed(invalidValueAllowed);
	}

	public Object getValue() {
		return wrappedField.getValue();
	}

	public void setValue(Object newValue) throws ReadOnlyException,
			ConversionException {
		wrappedField.setValue(newValue);
	}

	protected Object getSelectedUserLabel() {
		return null;
	}

	public Class<?> getType() {
		return wrappedField.getType();
	}

	public void addListener(ValueChangeListener listener) {
		wrappedField.addListener(listener);
	}

	public void removeListener(ValueChangeListener listener) {
		wrappedField.removeListener(listener);
	}

	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		wrappedField.valueChange(event);
	}

	public void setPropertyDataSource(Property newDataSource) {
		wrappedField.setPropertyDataSource(newDataSource);
	}

	public Property getPropertyDataSource() {
		return wrappedField.getPropertyDataSource();
	}

	public int getTabIndex() {
		return wrappedField.getTabIndex();
	}

	public void setTabIndex(int tabIndex) {
		wrappedField.setTabIndex(tabIndex);
	}

	public boolean isRequired() {
		return wrappedField.isRequired();
	}

	public void setRequired(boolean required) {
		wrappedField.setRequired(required);
	}

	public void setRequiredError(String requiredMessage) {
		wrappedField.setRequiredError(requiredMessage);
	}

	public String getRequiredError() {
		return wrappedField.getRequiredError();
	}

	@Override
	public void focus() {
		wrappedField.focus();
	}
}
