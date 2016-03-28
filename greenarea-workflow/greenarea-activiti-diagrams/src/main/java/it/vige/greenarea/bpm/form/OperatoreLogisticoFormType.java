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

import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static it.vige.greenarea.dto.Selezione.TUTTI;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

import it.vige.greenarea.dto.OperatoreLogistico;

public class OperatoreLogisticoFormType extends AbstractFormType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Map<String, String> values = new HashMap<String, String>();

	@Override
	public String getName() {
		return "operatoreLogisticoEnum";
	}

	@Override
	public Object getInformation(String key) {
		if (key.equals("values")) {
			return values;
		}
		return null;
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		validateValue(propertyValue);
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		getOperatoriLogistici();
		return (String) modelValue;
	}

	protected void validateValue(String value) {
		if (value != null) {
			if (values != null && !values.containsKey(value)) {
				throw new ActivitiIllegalArgumentException("Invalid value for enum form property: " + value);
			}
		}
	}

	private void getOperatoriLogistici() {
		IdentityService identityService = getDefaultProcessEngine().getIdentityService();
		UserQuery operatoriLogisticiQuery = identityService.createUserQuery();
		operatoriLogisticiQuery.memberOfGroup("operatorelogistico");
		List<User> users = operatoriLogisticiQuery.list();
		values.clear();
		values.put(TUTTI.name(), TUTTI.name());
		for (User user : users) {
			OperatoreLogistico op = new OperatoreLogistico(convertToGreenareaUser(user));
			values.put(op.getId(), op.getId());
		}
	}

}
