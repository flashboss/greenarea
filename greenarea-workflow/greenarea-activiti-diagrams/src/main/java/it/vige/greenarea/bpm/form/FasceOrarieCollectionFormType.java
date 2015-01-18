package it.vige.greenarea.bpm.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.form.AbstractFormType;

public class FasceOrarieCollectionFormType<T> extends AbstractFormType
		implements Serializable {

	private static final long serialVersionUID = 132973472818842203L;
	private Map<String, T> values = new HashMap<String, T>();

	@Override
	public String getName() {
		return "fasceOrarie";
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