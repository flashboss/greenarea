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

	public GreenareaFormPropertiesComponent(
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
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
				if (formProperty.getType() instanceof EnumFormType
						&& formProperty.getId().contains(OPERAZIONE)) {
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
			return formPropertyRendererManager
					.getTypeLessFormPropertyRenderer();
		} else {
			FormPropertyRenderer formPropertyRenderer;
			formPropertyRenderer = formPropertyRendererManager
					.getPropertyRendererForType(formProperty.getType());
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

	public void setGreenareaFormPropertiesForm(
			GreenareaFormPropertiesForm greenareaFormPropertiesForm) {
		this.greenareaFormPropertiesForm = greenareaFormPropertiesForm;
	}

	public Form getForm() {
		return form;
	}
}
