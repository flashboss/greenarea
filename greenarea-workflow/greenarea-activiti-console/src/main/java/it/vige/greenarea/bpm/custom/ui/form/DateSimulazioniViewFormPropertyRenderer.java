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

import static it.vige.greenarea.bpm.custom.ui.mainlayout.GreenareaExplorerLayout.STYLE_DATE_VIEW;

import java.lang.reflect.Method;

import org.activiti.engine.form.FormProperty;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import it.vige.greenarea.bpm.form.DateSimulazioniViewFormType;

public class DateSimulazioniViewFormPropertyRenderer<T> extends GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 8276247490292946211L;

	public DateSimulazioniViewFormPropertyRenderer() {
		super(DateSimulazioniViewFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) {
		// Writable string
		String value = formProperty.getValue();

		TextField dateField = new TextField();
		// Use the datePattern specified in the form property type
		dateField.setValue(getPropertyLabel(formProperty) + " " + value);
		dateField.setReadOnly(!formProperty.isWritable());
		dateField.setStyleName(STYLE_DATE_VIEW);

		return dateField;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		return true;
	}

}
