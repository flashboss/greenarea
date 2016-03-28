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
package it.vige.greenarea.cl.admin.bean;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import it.vige.greenarea.cl.admin.entity.ParameterTSView;
import it.vige.greenarea.cl.admin.entity.PriceView;
import it.vige.greenarea.cl.admin.rest.TimeSlotRestClient;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;

/**
 * <p>
 * Class: TimeSlotBean
 * </P>
 * <p>
 * Description: Contiene tutti i meotodi Relativi alla pagina web TImeSlot
 * </P>
 */

public class TimeSlotBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7816667474886377450L;

	private Logger logger = getLogger(getClass());

	private List<ParameterTS> listaTimeSlot_Visualizza;

	private List<ParameterTSView> listaTimeSlot_VisualizzaView;
	private ParameterTSView parameterTSView;
	private TimeSlotRestClient rclistaParGen;
	private TimeSlotRestClient rclistaPrice;
	private List<Price> listaPrice;
	private String IdTimeslot;
	private ArrayList<ParameterTSView> listaParTSView;
	private List<ParameterTS> listaParTS;

	public TimeSlotBean() {
	}

	/**
	 * <p>
	 * Method: timeSlot_BottoneVisualizza(String IdTs)
	 * </P>
	 * <p>
	 * Description: Viene eseguita quando clicco il bottone Visualizza dalla
	 * pagina TimeSlot
	 * </P>
	 * 
	 * @param
	 * @return
	 */

	public List<ParameterTSView> timeSlot_BottoneVisualizza(String IdTs) throws Exception {

		IdTimeslot = IdTs;
		logger.debug(IdTs);

		listaTimeSlot_Visualizza = new ArrayList<ParameterTS>();
		rclistaParGen = new TimeSlotRestClient();
		listaTimeSlot_VisualizzaView = new ArrayList<ParameterTSView>();
		TimeSlotRestClient rcTimeSlot_Visualizza = new TimeSlotRestClient();
		listaTimeSlot_Visualizza = rcTimeSlot_Visualizza.findParameterOfTimeSlot(IdTs);
		for (int i = 0; i < listaTimeSlot_Visualizza.size(); i++) {
			parameterTSView = new ParameterTSView();
			TimeSlot ts = listaTimeSlot_Visualizza.get(i).getTs();
			if (ts != null)
				parameterTSView.setIdPTS(ts.getIdTS());
			parameterTSView.setParGen(listaTimeSlot_Visualizza.get(i).getParGen());
			parameterTSView.setTs(ts);
			parameterTSView.setTypePar(listaTimeSlot_Visualizza.get(i).getTypePar());
			parameterTSView.setWeight(listaTimeSlot_Visualizza.get(i).getWeight());
			parameterTSView.setMaxVal(listaTimeSlot_Visualizza.get(i).getMaxValue());
			parameterTSView.setMinVal(listaTimeSlot_Visualizza.get(i).getMinValue());

			listaTimeSlot_VisualizzaView.add(parameterTSView);
		}
		return listaTimeSlot_VisualizzaView;
	}

	/**
	 * <p>
	 * Method: timeSlot_Visualizza()
	 * </P>
	 * <p>
	 * Description: Fa vedere la lista di Request che ho nella pagine
	 * DaProcessare_Simulazione
	 * </P>
	 * 
	 * @param
	 * @return
	 */

	public List<ParameterTSView> timeSlot_Visualizza() {
		return listaTimeSlot_VisualizzaView;
	}

	/**
	 * <p>
	 * Method: listaPriceTS()()
	 * </P>
	 * <p>
	 * Description: Fa vedere la lista dei Price associati alla fascia oraria
	 * selezionata
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Price> listaPriceTS() throws Exception {

		rclistaPrice = new TimeSlotRestClient();
		listaPrice = new ArrayList<Price>();
		listaPrice = rclistaPrice.getPriceOfTimeSlot(IdTimeslot);
		return listaPrice;
	}

	/**
	 * <p>
	 * Method: listaPriceTS()()
	 * </P>
	 * <p>
	 * Description: Fa vedere la lista dei Price associati alla fascia oraria
	 * selezionata
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<PriceView> listaPriceTSView() throws Exception {

		TimeSlotRestClient rclistaPriceView = new TimeSlotRestClient();
		ArrayList<PriceView> listaPriceView = new ArrayList<PriceView>();

		listaPrice = new ArrayList<Price>();

		listaPrice = rclistaPriceView.getPriceOfTimeSlot(IdTimeslot);

		for (int i = 0; i < listaPrice.size(); i++) {

			PriceView priceView = new PriceView();

			priceView.setColor(listaPrice.get(i).getColor());
			priceView.setFixPrice(listaPrice.get(i).getFixPrice());
			priceView.setIdPrice(listaPrice.get(i).getId());
			priceView.setIdTS(listaPrice.get(i).getTs().getIdTS());
			priceView.setMaxPrice(listaPrice.get(i).getMaxPrice());
			priceView.setMinPrice(listaPrice.get(i).getMinPrice());
			priceView.setTypeEntry(listaPrice.get(i).getTypeEntry());

			listaPriceView.add(priceView);
		}
		return listaPriceView;
	}

	/**
	 * <p>
	 * Method: listaParTS()()
	 * </P>
	 * <p>
	 * Description: Fa vedere la lista dei ParameterTS associati alla fascia
	 * oraria selezionata
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<ParameterTSView> listaParTS() throws Exception {
		rclistaParGen = new TimeSlotRestClient();
		listaParTSView = new ArrayList<ParameterTSView>();

		listaParTS = rclistaParGen.findParameterOfTimeSlot(IdTimeslot);

		for (int i = 0; i < listaParTS.size(); i++) {
			parameterTSView = new ParameterTSView();

			parameterTSView.setIdPTS(listaParTS.get(i).getTs().getIdTS());
			parameterTSView.setParGen(listaParTS.get(i).getParGen());
			parameterTSView.setTs(listaParTS.get(i).getTs());
			parameterTSView.setTypePar(listaParTS.get(i).getTypePar());
			parameterTSView.setWeight(listaParTS.get(i).getWeight());
			parameterTSView.setMaxVal(listaParTS.get(i).getMaxValue());
			parameterTSView.setMinVal(listaParTS.get(i).getMinValue());

			listaParTSView.add(parameterTSView);
		}
		return listaParTSView;
	}

}
