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

import static it.vige.greenarea.Constants.OPERAZIONE;

import java.util.List;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;
import org.activiti.engine.impl.form.EnumFormType;
import org.activiti.explorer.ui.form.FormPropertiesComponent;
import org.activiti.explorer.ui.form.FormPropertyRenderer;

import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

public class GreenareaFormPropertiesComponent extends FormPropertiesComponent {

	private static final long serialVersionUID = 651461645821099855L;

	private FormProperty operations;
	private GreenareaFormPropertiesForm greenareaFormPropertiesForm;

	public GreenareaFormPropertiesComponent(GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		super();
		this.greenareaFormPropertiesForm = greenareaFormPropertiesForm;
	}

	@Override
	public void setFormProperties(List<FormProperty> formProperties) {
		this.formProperties = formProperties;

		form.removeAllProperties();

		// Clear current components in the grid
		if (formProperties != null) {
			for (FormProperty formProperty : formProperties) {
				FormPropertyRenderer renderer = getRenderer(formProperty);
				if (formProperty.getType() instanceof EnumFormType && formProperty.getId().contains(OPERAZIONE)) {
					operations = formProperty;
				}

				Field editorComponent = renderer.getPropertyField(formProperty);
				if (editorComponent != null) {
					// Get label for editor component.
					form.addField(formProperty.getId(), editorComponent);
				}
			}
		}
	}

	public FormProperty getOperations() {
		return operations;
	}

	@Override
	public FormPropertyRenderer getRenderer(FormProperty formProperty) {
		FormType formPropertyType = formProperty.getType();
		if (formPropertyType == null) {
			return formPropertyRendererManager.getTypeLessFormPropertyRenderer();
		} else {
			FormPropertyRenderer formPropertyRenderer;
			formPropertyRenderer = formPropertyRendererManager.getPropertyRendererForType(formProperty.getType());
			if (formPropertyRenderer instanceof GreenareaAbstractFormPropertyRenderer) {
				((GreenareaAbstractFormPropertyRenderer<?>) formPropertyRenderer)
						.setGreenareaFormPropertiesForm(greenareaFormPropertiesForm);
			}
			return formPropertyRenderer;
		}
	}

	public GreenareaFormPropertiesForm getGreenareaFormPropertiesForm() {
		return greenareaFormPropertiesForm;
	}

	public void setGreenareaFormPropertiesForm(GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		this.greenareaFormPropertiesForm = greenareaFormPropertiesForm;
	}

	public Form getForm() {
		return form;
	}
}
