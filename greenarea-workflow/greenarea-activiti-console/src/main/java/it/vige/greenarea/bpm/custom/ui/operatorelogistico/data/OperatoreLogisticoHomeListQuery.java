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
package it.vige.greenarea.bpm.custom.ui.operatorelogistico.data;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_GESTIONE_GREEN_AREA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_GESTIONE_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_MISSIONI_AUTORIZZATE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_MONITORING_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_PERFORMANCE_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_REPORTING;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_SPEDIZIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_STORICO_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_OPERATORE_LOGISTICO_HOME_VEICOLI;
import static java.util.Arrays.asList;
import static org.activiti.explorer.ExplorerApp.get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activiti.explorer.I18nManager;
import org.activiti.explorer.data.AbstractLazyLoadingQuery;

import com.vaadin.data.Item;

import it.vige.greenarea.bpm.custom.ui.data.Page;
import it.vige.greenarea.bpm.custom.ui.data.PageListItem;

public class OperatoreLogisticoHomeListQuery extends AbstractLazyLoadingQuery {

	private I18nManager i18nManager = get().getI18nManager();

	private static final long serialVersionUID = -5121714812512928958L;
	private Page veicoli = new Page("0-0", i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_VEICOLI), null,
			false);
	private Page gestioneGreenArea = new Page("0",
			i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_GESTIONE_GREEN_AREA),
			new ArrayList<Page>(asList(new Page[] { veicoli })), false);
	private Page missioniAutorizzate = new Page("1-0",
			i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_MISSIONI_AUTORIZZATE), null, true);
	private Page spedizioni = new Page("1-1", i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_SPEDIZIONI),
			null, true);
	private Page monitoringMissioni = new Page("1-2",
			i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_MONITORING_MISSIONI), null, true);
	private Page gestioneMissioni = new Page("1",
			i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_GESTIONE_MISSIONI),
			new ArrayList<Page>(asList(new Page[] { spedizioni, missioniAutorizzate, monitoringMissioni })), false);
	private Page storicoMissioni = new Page("3-0",
			i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_STORICO_MISSIONI), null, true);
	private Page datiRilevatiDaOBU = new Page("3-1",
			i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_PERFORMANCE_MISSIONI), null, true);
	private Page reporting = new Page("3", i18nManager.getMessage(MAIN_MENU_OPERATORE_LOGISTICO_HOME_REPORTING),
			new ArrayList<Page>(asList(new Page[] { storicoMissioni, datiRilevatiDaOBU })), false);

	private List<Item> items = new ArrayList<Item>(asList(new Item[] { new PageListItem(gestioneGreenArea),
			new PageListItem(veicoli), new PageListItem(gestioneMissioni), new PageListItem(missioniAutorizzate),
			new PageListItem(spedizioni), new PageListItem(monitoringMissioni), new PageListItem(reporting),
			new PageListItem(storicoMissioni), new PageListItem(datiRilevatiDaOBU) }));

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public List<Item> loadItems(int start, int count) {
		return items;
	}

	@Override
	public Item loadSingleResult(String id) {
		return items.get(new Integer(id));
	}

	@Override
	public void setSorting(Object[] propertyIds, boolean[] ascending) {
		Collections.sort(items, new Comparator<Item>() {

			@Override
			public int compare(Item o1, Item o2) {
				return 0;
			}
		});
	}

}
