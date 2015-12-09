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

import java.lang.reflect.Method;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.validator.LongValidator;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class LongFormPropertyRenderer<T> extends GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = -2479682930508807219L;

	public LongFormPropertyRenderer() {
		super(LongFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) {
		final TextField textField = new TextField(getPropertyLabel(formProperty));
		textField.setRequired(formProperty.isRequired());
		textField.setEnabled(formProperty.isWritable());
		textField.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED, getPropertyLabel(formProperty)));

		if (formProperty.getValue() != null) {
			textField.setValue(formProperty.getValue());
		}

		// Add validation of numeric value
		textField.addValidator(new LongValidator("Value must be a long"));
		textField.setImmediate(true);

		return textField;
	}

	protected boolean isLong(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		return true;
	}

}
