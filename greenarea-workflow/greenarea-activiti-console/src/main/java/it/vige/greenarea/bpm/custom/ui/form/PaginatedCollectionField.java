package it.vige.greenarea.bpm.custom.ui.form;

import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class PaginatedCollectionField<T> extends VerticalLayout implements
		Field {
	private static final long serialVersionUID = 1L;
	private TextField wrappedField;

	public PaginatedCollectionField(GreenareaPagedTable<T> table) {
		addStyleName("main-table");
		addComponent(table);
		addComponent(table.createControls());

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
