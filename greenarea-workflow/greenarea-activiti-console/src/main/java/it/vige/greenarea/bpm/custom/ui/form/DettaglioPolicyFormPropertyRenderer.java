package it.vige.greenarea.bpm.custom.ui.form;

import it.vige.greenarea.bpm.custom.ui.form.dettagliopolicy.DettaglioPolicyField;
import it.vige.greenarea.bpm.form.DettaglioPolicyFormType;
import it.vige.greenarea.dto.FasciaOraria;

import java.lang.reflect.Method;

import org.activiti.engine.form.FormProperty;

import com.vaadin.ui.Field;

public class DettaglioPolicyFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -736095927840135968L;

	public DettaglioPolicyFormPropertyRenderer() {
		super(DettaglioPolicyFormType.class);
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Field getPropertyField(FormProperty arg0) {
		String value = arg0.getValue();
		FasciaOraria fasciaOraria = null;
		if (value != null)
			fasciaOraria = (FasciaOraria) arg0.getType().getInformation(value);
		DettaglioPolicyField<T> field = new DettaglioPolicyField<T>(arg0, this,
				fasciaOraria);
		return field;
	}

}
