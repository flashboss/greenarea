/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.admin.bean;

import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.library.entities.TsStat;
import it.vige.greenarea.cl.admin.rest.TimeSlotRestClient;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

/**
 * <p>
 * Class: StoricoBean
 * </P>
 * <p>
 * Description: Contiene tutti i meotodi Relativi alla pagina web Storico
 * </P>
 */

public class StoricoBean implements Serializable {

	private static final long serialVersionUID = -7389408581482566252L;

	private Logger logger = getLogger(getClass());

	TimeSlotRestClient rcStorico = new TimeSlotRestClient();
	private List<TsStat> listaAllTsStat;
	private List<Request> listaStorico_Dettaglio;

	public StoricoBean() {
	}

	/**
	 * <p>
	 * Method: allTsStat()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista di TsStat che ho nella pagina Storico
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<TsStat> allTsStat() throws Exception {
		listaAllTsStat = rcStorico.getAllTsStats();
		listaAllTsStat.size();
		return listaAllTsStat;
	}

	/**
	 * <p>
	 * Method: storico_BottoneDettaglio(String data, String IdTs)
	 * </P>
	 * <p>
	 * Description: Viene eseguita quando clicco il bottone Simulazione dalla
	 * pagina DaProcessare
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Request> storico_BottoneDettaglio(Date data, String IdTs)
			throws Exception {
		TimeSlotRestClient rcStorico_Dettaglio = new TimeSlotRestClient();
		listaStorico_Dettaglio = rcStorico_Dettaglio.getStoryBoard(IdTs, data);
		for (int i = 0; i < listaStorico_Dettaglio.size(); i++) {
			logger.info(listaStorico_Dettaglio.get(i).getUserName());
		}
		return listaStorico_Dettaglio;
	}

	/**
	 * <p>
	 * Method: storico_Dettaglio()
	 * </P>
	 * <p>
	 * Description: Fa vedere la lista di Request che ho nella pagine
	 * DaProcessare_Simulazione
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Request> storico_Dettaglio() {
		return listaStorico_Dettaglio;
	}

}
