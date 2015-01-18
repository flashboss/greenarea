package it.vige.greenarea.bpm.form;

import java.io.Serializable;

import it.vige.greenarea.dto.FasciaOraria;

import org.activiti.engine.form.AbstractFormType;

public class DettaglioPolicyFormType extends AbstractFormType implements
		Serializable {

	private static final long serialVersionUID = 3503702529278473233L;

	private FasciaOraria fasciaOraria;

	@Override
	public String getName() {
		return "dettaglioPolicy";
	}

	@Override
	public FasciaOraria convertFormValueToModelValue(String propertyValue) {
		return fasciaOraria;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		fasciaOraria = null;
		if (modelValue != null) {
			fasciaOraria = (FasciaOraria) modelValue;
			return modelValue.toString();
		} else
			return "";
	}

	@Override
	public Object getInformation(String key) {
		return fasciaOraria;
	}
}
