package it.vige.greenarea.bpm.custom.ui.form;

import it.vige.greenarea.bpm.form.DettaglioMissioneTRFormType;
import it.vige.greenarea.dto.Missione;

import java.lang.reflect.Method;

import org.activiti.engine.form.FormProperty;

import com.vaadin.ui.Field;

public class DettaglioMissioneTRFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -736095927840135968L;

	public DettaglioMissioneTRFormPropertyRenderer() {
		super(DettaglioMissioneTRFormType.class);
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Field getPropertyField(FormProperty arg0) {
		String value = arg0.getValue();
		Missione missione = null;
		if (value != null)
			missione = (Missione) arg0.getType().getInformation(value);
		DettaglioMissioneTRField<T> field = new DettaglioMissioneTRField<T>(
				arg0, this, missione);
		return field;
	}
}
