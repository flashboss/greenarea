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

import static it.vige.greenarea.Conversioni.addDays;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.explorer.Messages;
import org.vaadin.addons.rangedatefield.RangeDateField;

import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

/**
 * @author Frederik Heremans
 */
public class DateFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 8276247490292946211L;

	public DateFormPropertyRenderer() {
		super(DateFormType.class);
	}

	@Override
	public Field getPropertyField(FormProperty formProperty) {
		// Writable string
		RangeDateField dateField = new RangeDateField(
				getPropertyLabel(formProperty));
		dateField.setResolution(DateField.RESOLUTION_DAY);

		switch (formProperty.getId()) {
		case "elenco_missioni_pa_dal":
			Date date = addDays(new Date(), 1);
			dateField.setValue(date);
			break;
		case "elenco_missioni_pa_al":
			date = addDays(new Date(), 1);
			dateField.setValidFromDate(date);
			dateField.setValue(date);
			break;
		case "accesso_in_ga_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "accesso_in_ga_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "missioni_pa_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "missioni_pa_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "impatto_ambientale_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "impatto_ambientale_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "consegne_e_ritiri_dal":
			date = addDays(new Date(), 1);
			dateField.setValue(date);
			break;
		case "consegne_e_ritiri_al":
			date = addDays(new Date(), 1);
			dateField.setValidFromDate(date);
			dateField.setValue(date);
			break;
		case "missioni_op_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "missioni_op_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "performance_missioni_op_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "performance_missioni_op_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "performance_veicoli_st_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "performance_veicoli_st_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "performance_veicoli_tr_dal":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		case "performance_veicoli_tr_al":
			date = addDays(new Date(), -1);
			dateField.setValidToDate(date);
			dateField.setValue(date);
			break;
		}
		String datePattern = (String) formProperty.getType().getInformation(
				"datePattern");
		dateField.setDateFormat(datePattern);
		dateField.setRequired(formProperty.isRequired());
		dateField.setRequiredError(getMessage(Messages.FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		dateField.setEnabled(formProperty.isWritable());

		if (formProperty.getValue() != null) {
			// Try parsing the current value
			SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

			try {
				Date date = dateFormat.parse(formProperty.getValue());
				dateField.setValue(date);
			} catch (ParseException e) {
				// TODO: what happens if current value is illegal date?
			}
		}
		return dateField;
	}

	@Override
	public String getFieldValue(FormProperty formProperty, Field field) {
		RangeDateField dateField = (RangeDateField) field;
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
