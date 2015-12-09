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

public class GreenareaMissioniTableCellStyleGenerator implements CellStyleGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getStyle(Object itemId, Object propertyId) {
		if (propertyId != null) {
			I18nManager i18nManager = get().getI18nManager();
			if (i18nManager.getMessage(MISSIONI_TABLE_FIELDS + "ranking").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_TABLE_FIELDS + "creditomobilita").equals((String) propertyId)
					|| i18nManager.getMessage(SIMULAZIONI_TABLE_FIELDS + "ranking").equals((String) propertyId)
					|| i18nManager.getMessage(SIMULAZIONI_TABLE_FIELDS + "creditomobilita").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_OP_TABLE_FIELDS + "ranking").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_OP_TABLE_FIELDS + "creditomobilita").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_OP_SINTESI_TABLE_FIELDS + "ranking").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_OP_SINTESI_TABLE_FIELDS + "creditomobilita")
							.equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_OP_SINTESI_TABLE_FIELDS + "bonus").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS + "creditidimobilita")
							.equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_PA_SINTESI_TABLE_FIELDS + "bonus").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_TR_TABLE_FIELDS + "ranking").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_TR_TABLE_FIELDS + "creditomobilita").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_ST_TABLE_FIELDS + "ranking").equals((String) propertyId)
					|| i18nManager.getMessage(MISSIONI_ST_TABLE_FIELDS + "creditomobilita")
							.equals((String) propertyId)) {
				return "highlights";
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

}
