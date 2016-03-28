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
package it.vige.greenarea.bpm.custom.ui.pa.data;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_ACCESSO_IN_GA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_CONFIGURAZIONE_POLICY;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_DEFINIZIONE_GREEN_AREA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_GESTIONE_FASCE_ORARIE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_GESTIONE_PARAMETRI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_GREEN_AREA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_IMPATTO_AMBIENTALE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_MISSIONI_AUTORIZZATE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_REPORTING;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_SIMULAZIONE_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_PA_HOME_SINTESI_MISSIONI;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.activiti.explorer.ExplorerApp.get;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.activiti.explorer.I18nManager;
import org.activiti.explorer.data.AbstractLazyLoadingQuery;

import com.vaadin.data.Item;

import it.vige.greenarea.bpm.custom.ui.data.Page;
import it.vige.greenarea.bpm.custom.ui.data.PageListItem;

public class PAHomeListQuery extends AbstractLazyLoadingQuery {

	private I18nManager i18nManager = get().getI18nManager();

	private static final long serialVersionUID = -5121714812512928958L;
	private Page definizioneGreenArea = new Page("0-0",
			i18nManager.getMessage(MAIN_MENU_PA_HOME_DEFINIZIONE_GREEN_AREA), null, false);
	private Page visualizzaParametri = new Page("0-1-0", i18nManager.getMessage(MAIN_MENU_PA_HOME_GESTIONE_PARAMETRI),
			null, false);
	private Page visualizzaFasceOrarie = new Page("0-1-1",
			i18nManager.getMessage(MAIN_MENU_PA_HOME_GESTIONE_FASCE_ORARIE), null, false);
	private Page configurazionePolicy = new Page("0-1", i18nManager.getMessage(MAIN_MENU_PA_HOME_CONFIGURAZIONE_POLICY),
			new ArrayList<Page>(asList(new Page[] { visualizzaParametri, visualizzaFasceOrarie })), false);
	private Page greenArea = new Page("0", i18nManager.getMessage(MAIN_MENU_PA_HOME_GREEN_AREA),
			new ArrayList<Page>(asList(new Page[] { definizioneGreenArea, configurazionePolicy })), false);
	private Page missioniAutorizzate = new Page("1-0", i18nManager.getMessage(MAIN_MENU_PA_HOME_MISSIONI_AUTORIZZATE),
			null, true);
	private Page simulazioneMissioni = new Page("1-1", i18nManager.getMessage(MAIN_MENU_PA_HOME_SIMULAZIONE_MISSIONI),
			null, true);
	private Page missioni = new Page("1", i18nManager.getMessage(MAIN_MENU_PA_HOME_MISSIONI),
			new ArrayList<Page>(asList(new Page[] { missioniAutorizzate, simulazioneMissioni })), false);
	private Page accessoInGA = new Page("3-0", i18nManager.getMessage(MAIN_MENU_PA_HOME_ACCESSO_IN_GA), null, true);
	private Page sintesiMissioni = new Page("3-1", i18nManager.getMessage(MAIN_MENU_PA_HOME_SINTESI_MISSIONI), null,
			true);
	private Page impattoAmbientale = new Page("3-2", i18nManager.getMessage(MAIN_MENU_PA_HOME_IMPATTO_AMBIENTALE), null,
			true);
	private Page reporting = new Page("3", i18nManager.getMessage(MAIN_MENU_PA_HOME_REPORTING),
			new ArrayList<Page>(asList(new Page[] { accessoInGA, sintesiMissioni, impattoAmbientale })), false);

	private List<Item> items = new ArrayList<Item>(asList(new Item[] { new PageListItem(greenArea),
			new PageListItem(definizioneGreenArea), new PageListItem(configurazionePolicy),
			new PageListItem(visualizzaParametri), new PageListItem(visualizzaFasceOrarie), new PageListItem(missioni),
			new PageListItem(missioniAutorizzate), new PageListItem(simulazioneMissioni), new PageListItem(reporting),
			new PageListItem(accessoInGA), new PageListItem(sintesiMissioni), new PageListItem(impattoAmbientale) }));

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
		sort(items, new Comparator<Item>() {

			@Override
			public int compare(Item o1, Item o2) {
				return 0;
			}
		});
	}

}
