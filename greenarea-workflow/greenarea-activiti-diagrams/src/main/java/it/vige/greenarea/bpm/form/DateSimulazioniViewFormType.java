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
package it.vige.greenarea.bpm.form;

import it.vige.greenarea.dto.Richiesta;

import java.io.Serializable;
import java.text.Format;
import java.util.Date;
import java.util.List;

import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.time.FastDateFormat;

public class DateSimulazioniViewFormType extends AbstractFormType implements
		Serializable {

	private static final long serialVersionUID = -7584212910516167286L;
	private String datePattern;
	private Format dateFormat;

	private List<Richiesta> richieste;

	public DateSimulazioniViewFormType() {
		this.datePattern = "d-MM-yyyy";
		this.dateFormat = FastDateFormat.getInstance(datePattern);
	}

	public String getName() {
		return "dateSimulazioniView";
	}

	public Object getInformation(String key) {
		return richieste;
	}

	public Object convertFormValueToModelValue(String propertyValue) {
		return richieste;
	}

	@SuppressWarnings("unchecked")
	public String convertModelValueToFormValue(Object modelValue) {
		richieste = null;
		if (modelValue != null) {
			richieste = (List<Richiesta>) modelValue;
			if (richieste.size() > 0)
				return dateFormat.format(richieste.get(0).getDataMissione());
			else
				return dateFormat.format(new Date());
		} else
			return dateFormat.format(new Date());
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public Format getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(Format dateFormat) {
		this.dateFormat = dateFormat;
	}
}
