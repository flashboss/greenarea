package it.vige.greenarea.bpm.custom.ui.societaditrasporto.data;

import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_ELENCO_MISSIONI_AUTORIZZATE;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_GESTIONE_GREEN_AREA;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_GESTIONE_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_MONITORING_MISSIONI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_PERFORMANCE_VEICOLI;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_POSIZIONE_VEICOLO;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_REPORTING;
import static it.vige.greenarea.bpm.custom.GreenareaMessages.MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_VEICOLI;
import static java.util.Arrays.asList;
import static org.activiti.explorer.ExplorerApp.get;
import it.vige.greenarea.bpm.custom.ui.data.Page;
import it.vige.greenarea.bpm.custom.ui.data.PageListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.activiti.explorer.I18nManager;
import org.activiti.explorer.data.AbstractLazyLoadingQuery;

import com.vaadin.data.Item;

public class SocietaDiTrasportoHomeListQuery extends AbstractLazyLoadingQuery {

	private I18nManager i18nManager = get().getI18nManager();

	private static final long serialVersionUID = -5121714812512928958L;
	private Page veicoli = new Page(
			"0-0",
			i18nManager.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_VEICOLI),
			null, false);
	private Page gestioneGreenArea = new Page(
			"0",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_GESTIONE_GREEN_AREA),
			new ArrayList<Page>(asList(new Page[] { veicoli })), false);
	private Page elencoMissioniAutorizzate = new Page(
			"1-0",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_ELENCO_MISSIONI_AUTORIZZATE),
			null, true);
	private Page monitoringMissioni = new Page(
			"1-1",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_MONITORING_MISSIONI),
			null, true);
	private Page gestioneMissioni = new Page(
			"1",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_GESTIONE_MISSIONI),
			new ArrayList<Page>(asList(new Page[] { elencoMissioniAutorizzate,
					monitoringMissioni })), false);
	private Page posizioneVeicolo = new Page(
			"2-0",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_POSIZIONE_VEICOLO),
			null, true);
	private Page performanceVeicoli = new Page(
			"2-1",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_PERFORMANCE_VEICOLI),
			null, true);
	private Page reporting = new Page("2",
			i18nManager
					.getMessage(MAIN_MENU_SOCIETA_DI_TRASPORTO_HOME_REPORTING),
			new ArrayList<Page>(asList(new Page[] { posizioneVeicolo,
					performanceVeicoli })), false);

	private List<Item> items = new ArrayList<Item>(asList(new Item[] {
			new PageListItem(gestioneGreenArea), new PageListItem(veicoli),
			new PageListItem(gestioneMissioni),
			new PageListItem(elencoMissioniAutorizzate),
			new PageListItem(monitoringMissioni), new PageListItem(reporting),
			new PageListItem(posizioneVeicolo),
			new PageListItem(performanceVeicoli) }));

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
