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

import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_BUTTON;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_CODICE_FILIALE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_CREDITO_DI_MOBILITA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_DATA_MISSIONE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_ID_MISSIONE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_OPERATORE_LOGISTICO;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_RANKING;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.DETTAGLIO_MISSIONE_TITLE;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.dto.Missione;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.I18nManager;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class DettaglioMissioneTRField<T> extends HorizontalLayout implements
		Field {
	private static final long serialVersionUID = 1L;
	protected TextField wrappedField;
	protected Button policyDetailsButton;
	private DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");

	public DettaglioMissioneTRField(
			FormProperty formProperty,
			GreenareaAbstractFormPropertyRenderer<T> greenareaAbstractFormPropertyRenderer,
			Missione missione) {
		I18nManager i18nManager = get().getI18nManager();
		String caption = i18nManager.getMessage(DETTAGLIO_MISSIONE_TITLE);
		setSpacing(true);
		setCaption(caption);
		setHeight(Sizeable.SIZE_UNDEFINED, 0);
		Label opLabel = new Label();
		opLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_OPERATORE_LOGISTICO)
				+ " "
				+ missione.getCompagnia());
		opLabel.setStyleName("missione_label");
		Label missionIdLabel = new Label();
		missionIdLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_ID_MISSIONE)
				+ " "
				+ missione.getNome());
		missionIdLabel.setStyleName("missione_label");
		Label missionDateLabel = new Label();
		missionDateLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_DATA_MISSIONE)
				+ " "
				+ dateFormat.format(missione.getDataInizio()));
		missionDateLabel.setStyleName("missione_label");
		Label agencyCodeLabel = new Label();
		agencyCodeLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_CODICE_FILIALE)
				+ " "
				+ missione.getCodiceFiliale());
		agencyCodeLabel.setStyleName("missione_label");
		Label rankingLabel = new Label();
		rankingLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_RANKING));
		Embedded rankingImage = null;
		if (missione.getRanking() != null) {
			if (missione.getRanking().equals(VERDE))
				rankingImage = new Embedded(null, new ThemeResource(
						"img/circle_green.png"));
			else if (missione.getRanking().equals(GIALLO))
				rankingImage = new Embedded(null, new ThemeResource(
						"img/circle_orange.png"));
			else if (missione.getRanking().equals(ROSSO))
				rankingImage = new Embedded(null, new ThemeResource(
						"img/circle_red.png"));
			rankingImage.setWidth(20, UNITS_PIXELS);
			rankingImage.setStyleName("missione_label");
		}
		Label mobilityCreditLabel = new Label();
		mobilityCreditLabel.setValue(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_CREDITO_DI_MOBILITA)
				+ " "
				+ missione.getCreditoMobilita());
		mobilityCreditLabel.setStyleName("missione_label");
		addComponent(opLabel);
		addComponent(missionIdLabel);
		addComponent(missionDateLabel);
		addComponent(agencyCodeLabel);
		addComponent(rankingLabel);
		if (rankingImage != null)
			addComponent(rankingImage);
		addComponent(mobilityCreditLabel);
		setStyleName("dettaglio-missione");

		policyDetailsButton = new Button();
		policyDetailsButton.setCaption(i18nManager
				.getMessage(DETTAGLIO_MISSIONE_BUTTON));
		final GreenareaAbstractFormPropertyRenderer<?> fGreenareaAbstractFormPropertyRenderer = greenareaAbstractFormPropertyRenderer;
		policyDetailsButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				fGreenareaAbstractFormPropertyRenderer
						.getGreenareaFormPropertiesForm().getSubmitFormButton()
						.click();
			}
		});

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
