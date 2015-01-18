/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.vige.greenarea.bpm.custom.ui.form;

import it.vige.greenarea.bpm.custom.ui.TimeField;
import it.vige.greenarea.bpm.form.TimeFormType;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.Messages;

import com.vaadin.ui.Field;

/**
 * @author Frederik Heremans
 */
public class TimeFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 3182117599032514508L;

	public TimeFormPropertyRenderer() {
		super(TimeFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) {
		// Writable string
		TimeField timeField = new TimeField(getPropertyLabel(formProperty));
		String datePattern = (String) formProperty.getType().getInformation(
				"datePattern");
		timeField.setRequired(formProperty.isRequired());
		timeField.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		timeField.setEnabled(formProperty.isWritable());

		if (formProperty.getValue() != null) {
			// Try parsing the current value
			SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

			try {
				Date date = dateFormat.parse(formProperty.getValue());
				timeField.setValue(date);
			} catch (ParseException e) {
				// TODO: what happens if current value is illegal date?
			}
		}
		return timeField;
	}

	@Override
	public String getFieldValue(FormProperty formProperty, Field field) {
		TimeField dateField = (TimeField) field;
		Date selectedDate = (Date) dateField.getValue();

		if (selectedDate != null) {
			// Use the datePattern specified in the form property type
			String datePattern = (String) formProperty.getType()
					.getInformation("datePattern");
			SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
			return dateFormat.format(selectedDate);
		}

		return null;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		return true;
	}

}
