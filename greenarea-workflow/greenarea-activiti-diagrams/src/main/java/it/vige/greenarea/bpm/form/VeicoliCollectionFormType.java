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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.form.AbstractFormType;

public class VeicoliCollectionFormType<T> extends AbstractFormType implements
		Serializable {

	private static final long serialVersionUID = 716196969672651847L;
	private Map<String, T> values = new HashMap<String, T>();

	@Override
	public String getName() {
		return "veicoli";
	}

	@Override
	public Collection<T> convertFormValueToModelValue(String propertyValue) {
		Collection<T> ret = new ArrayList<T>();
		T element = values.get(propertyValue);
		if (element != null) {
			ret.add(element);
		}
		return ret;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String convertModelValueToFormValue(Object modelValue) {
		Collection<T> modelValues = (Collection<T>) modelValue;
		values.clear();
		for (T value : modelValues)
			values.put(value.toString(), value);
		return modelValue.toString();

	}

	@Override
	public Object getInformation(String key) {
		if ("values".equals(key)) {
			return values;
		} else {
			return null;// TODO
		}

	}
}