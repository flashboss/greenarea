package it.vige.greenarea.bpm.custom.ui.form;

import static it.vige.greenarea.Constants.OPERAZIONE;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.FORM_FIELD_REQUIRED;
import it.vige.greenarea.bpm.form.TargaStFormType;
import it.vige.greenarea.dto.GreenareaUser;
import it.vige.greenarea.dto.Veicolo;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.engine.form.FormProperty;
import org.activiti.explorer.identity.LoggedInUser;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;

public class TargaStFormPropertyRenderer<T> extends
		GreenareaAbstractFormPropertyRenderer<T> {

	private static final long serialVersionUID = 1L;

	public TargaStFormPropertyRenderer() {
		super(TargaStFormType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Field getPropertyField(FormProperty formProperty) {
		ComboBox comboBox = new ComboBox(getPropertyLabel(formProperty));
		comboBox.setRequired(formProperty.isRequired());
		comboBox.setRequiredError(getMessage(FORM_FIELD_REQUIRED,
				getPropertyLabel(formProperty)));
		comboBox.setEnabled(formProperty.isWritable());
		comboBox.setNullSelectionAllowed(false);

		Object itemToSelect = null;
		Map<Veicolo, String> values = (Map<Veicolo, String>) formProperty
				.getType().getInformation("values");
		String user = ((LoggedInUser) get().getUser()).getId();
		if (values != null) {
			if (!formProperty.isRequired())
				comboBox.addItem("");
			if (itemToSelect == null)
				itemToSelect = "";
			for (Entry<Veicolo, String> enumEntry : values.entrySet()) {
				if (isUser(enumEntry.getKey(), user)) {
					// Add value and label (if any)
					comboBox.addItem(enumEntry.getKey().getTarga());

					String selectedValue = formProperty.getValue();
					if ((selectedValue != null && selectedValue
							.equals(enumEntry.getKey()))) {
						itemToSelect = enumEntry.getKey().getTarga(); // select
																		// first
						// element
					}

					if (enumEntry.getValue() != null) {
						Veicolo key = enumEntry.getKey();
						comboBox.setItemCaption(key.getTarga(),
								enumEntry.getValue());
					}
				}
			}
		}

		// Select first element
		if (itemToSelect != null) {
			comboBox.select(itemToSelect);
		}

		if (formProperty.getId().contains(OPERAZIONE))
			comboBox.setVisible(false);
		return comboBox;
	}

	@Override
	protected boolean visible(Method method, java.lang.reflect.Field field) {
		return true;
	}

	private boolean isUser(Veicolo veicolo, String user) {
		GreenareaUser greenareaUser = veicolo.getSocietaDiTrasporto();
		if (greenareaUser != null && greenareaUser.getId() != null
				&& greenareaUser.getId().equals(user))
			return true;
		else
			return false;
	}

}
