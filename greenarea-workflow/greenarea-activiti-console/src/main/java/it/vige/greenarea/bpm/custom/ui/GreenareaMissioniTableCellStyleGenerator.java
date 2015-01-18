package it.vige.greenarea.bpm.custom.ui;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_OP_SINTESI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_OP_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_PA_SINTESI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_ST_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MISSIONI_TR_TABLE_FIELDS;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.SIMULAZIONI_TABLE_FIELDS;
import static org.activiti.explorer.ExplorerApp.get;

import org.activiti.explorer.I18nManager;

import com.vaadin.ui.Table.CellStyleGenerator;

public class GreenareaMissioniTableCellStyleGenerator implements
		CellStyleGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getStyle(Object itemId, Object propertyId) {
		if (propertyId != null) {
			I18nManager i18nManager = get().getI18nManager();
			if (i18nManager.getMessage(MISSIONI_TABLE_FIELDS + "ranking")
					.equals((String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_TABLE_FIELDS + "creditomobilita").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							SIMULAZIONI_TABLE_FIELDS + "ranking").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							SIMULAZIONI_TABLE_FIELDS + "creditomobilita")
							.equals((String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_OP_TABLE_FIELDS + "ranking").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_OP_TABLE_FIELDS + "creditomobilita")
							.equals((String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_OP_SINTESI_TABLE_FIELDS + "ranking")
							.equals((String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_OP_SINTESI_TABLE_FIELDS
									+ "creditomobilita").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_OP_SINTESI_TABLE_FIELDS + "bonus").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_PA_SINTESI_TABLE_FIELDS
									+ "creditidimobilita").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_PA_SINTESI_TABLE_FIELDS + "bonus").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_TR_TABLE_FIELDS + "ranking").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_TR_TABLE_FIELDS + "creditomobilita")
							.equals((String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_ST_TABLE_FIELDS + "ranking").equals(
							(String) propertyId)
					|| i18nManager.getMessage(
							MISSIONI_ST_TABLE_FIELDS + "creditomobilita")
							.equals((String) propertyId)) {
				return "highlights";
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

}
