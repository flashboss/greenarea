package it.vige.greenarea.bpm.form;

import static it.vige.greenarea.bpm.UserConverter.convertToGreenareaUser;
import static it.vige.greenarea.dto.Selezione.TUTTI;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import it.vige.greenarea.dto.OperatoreLogistico;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;

public class OperatoreLogisticoFormType extends AbstractFormType implements
		Serializable {

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
				throw new ActivitiIllegalArgumentException(
						"Invalid value for enum form property: " + value);
			}
		}
	}

	private void getOperatoriLogistici() {
		IdentityService identityService = getDefaultProcessEngine()
				.getIdentityService();
		UserQuery operatoriLogisticiQuery = identityService.createUserQuery();
		operatoriLogisticiQuery.memberOfGroup("operatorelogistico");
		List<User> users = operatoriLogisticiQuery.list();
		values.clear();
		values.put(TUTTI.name(), TUTTI.name());
		for (User user : users) {
			OperatoreLogistico op = new OperatoreLogistico(
					convertToGreenareaUser(user));
			values.put(op.getId(), op.getId());
		}
	}

}
