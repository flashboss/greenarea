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
