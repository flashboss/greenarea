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

import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author Tom Baeyens
 */
public class TimeFormType extends AbstractFormType implements Serializable {

	private static final long serialVersionUID = -7584212910516167286L;
	protected String datePattern;
	protected Format dateFormat;

	public TimeFormType() {
		this.datePattern = "HH:mm";
		this.dateFormat = FastDateFormat.getInstance(datePattern);
	}

	public String getName() {
		return "time";
	}

	public Object getInformation(String key) {
		if ("datePattern".equals(key)) {
			return datePattern;
		}
		return null;
	}

	public Object convertFormValueToModelValue(String propertyValue) {
		if (StringUtils.isEmpty(propertyValue)) {
			return null;
		}
		try {
			return dateFormat.parseObject(propertyValue);
		} catch (ParseException e) {
			throw new ActivitiIllegalArgumentException("invalid date value "
					+ propertyValue);
		}
	}

	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue == null) {
			return null;
		}
		return dateFormat.format(modelValue);
	}
}
