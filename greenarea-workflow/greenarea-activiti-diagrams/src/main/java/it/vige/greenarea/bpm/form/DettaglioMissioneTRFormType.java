package it.vige.greenarea.bpm.form;

import it.vige.greenarea.dto.Missione;

import java.io.Serializable;

import org.activiti.engine.form.AbstractFormType;

public class DettaglioMissioneTRFormType extends AbstractFormType implements Serializable {

	private static final long serialVersionUID = 3503702529278473233L;
	
	private Missione missione;

	@Override
	public String getName() {
		return "dettaglioMissioneTR";
	}

	@Override
	public Missione convertFormValueToModelValue(String propertyValue) {
		return missione;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue != null) {
			missione = (Missione) modelValue;
			return modelValue.toString();
		} else
			return "";
	}

	@Override
	public Object getInformation(String key) {
		return missione;
	}
}
