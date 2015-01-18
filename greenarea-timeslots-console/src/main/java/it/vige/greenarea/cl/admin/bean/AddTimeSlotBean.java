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

import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.Conversioni.convertiFreightToShippingItem;
import static it.vige.greenarea.GTGsystem.enzo;
import static it.vige.greenarea.GTGsystem.lella;
import static it.vige.greenarea.Utilities.createMockShippingId;
import static it.vige.greenarea.cl.library.entities.FreightType.DOCUMENTI;
import static it.vige.greenarea.cl.library.entities.Transport.TransportState.waiting;
import static it.vige.greenarea.dto.AccessoVeicoli.GRATUITO;
import static it.vige.greenarea.dto.AccessoVeicoli.NEGATO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_FISSO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_VARIABILE;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.ROSSO;
import static it.vige.greenarea.dto.Color.VERDE;
import static it.vige.greenarea.dto.Peso.NESSUNO;
import static it.vige.greenarea.dto.TipoParametro.DA_DECIDERE;
import static it.vige.greenarea.dto.TipologiaParametro.BENEFICIO;
import static java.util.Arrays.asList;
import static javax.faces.context.FacesContext.getCurrentInstance;
import static javax.persistence.EnumType.STRING;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.library.entities.DBGeoLocation;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.ShippingItem;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.dto.AccessoVeicoli;
import it.vige.greenarea.dto.AperturaRichieste;
import it.vige.greenarea.dto.ChiusuraRichieste;
import it.vige.greenarea.dto.GeoLocation;
import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.Ripetizione;
import it.vige.greenarea.dto.TipoParametro;
import it.vige.greenarea.dto.TipologiaClassifica;
import it.vige.greenarea.dto.TipologiaParametro;
import it.vige.greenarea.dto.Tolleranza;
import it.vige.greenarea.cl.admin.entity.ParameterGenView;
import it.vige.greenarea.cl.admin.entity.ParameterTSView;
import it.vige.greenarea.cl.admin.rest.SgotRestClient;
import it.vige.greenarea.cl.admin.rest.TimeSlotRestClient;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Enumerated;

import org.slf4j.Logger;

/**
 * <p>
 * Class: AddTimeSlotBean
 * </P>
 * <p>
 * Description: Contiene tutti i meotodi Relativi alla Configurazione di una
 * Fascia Oraria
 * </P>
 */

@ManagedBean
@SessionScoped
public class AddTimeSlotBean implements Serializable {

	private static final long serialVersionUID = -5001564263057371015L;

	private Logger logger = getLogger(getClass());

	private ArrayList<ParameterTSView> listaParTSView;

	private ArrayList<ParameterGenView> listaAllParameterGenView;
	private List<ParameterGen> listaAllParameterGen;
	private int allParameterGen;
	public String settaggio;
	private ParameterTSView parameterTSView;
	private List<Price> listaPrice;
	private String idtsString;
	private TimeSlot createTS;

	// Inizializzo la lista che conterr?????????????????????????????????????????????????????? i parametriTs aggiornati
	private ArrayList<ParameterTS> listaParameterTSAggiornati = new ArrayList<ParameterTS>();
	private Price priceRed;
	private Price priceYellow;
	private Price priceGreen;

	// variabili di TimeSlot
	@Enumerated(STRING)
	private AperturaRichieste timeToAcceptRequest;
	@Enumerated(STRING)
	private ChiusuraRichieste timeToStopRequest;
	@Enumerated(STRING)
	private ChiusuraRichieste timeToRun;
	@Enumerated(STRING)
	private Tolleranza tollerance;
	@Enumerated(STRING)
	private Ripetizione wmy; // Stringa per giorni/settimana/mesi
	private Date startTS;
	private Date finishTS;
	private Date dayStart;
	private Date dayFinish;
	private String ga;
	private TipologiaClassifica vikInd;

	// variabili di ParameterTS
	private Integer idTS;
	private Integer idParGen;
	private double maxVal;
	private double minVal;
	private Peso weight;
	private TipoParametro typePar;

	// variabili di Price

	private Integer idPriceV;
	private int maxPriceV;
	private int minPriceV;
	private int fixPriceV;
	private AccessoVeicoli typeEntryV;

	private Integer idPriceG;
	private int maxPriceG;
	private int minPriceG;
	private int fixPriceG;
	private AccessoVeicoli typeEntryG;

	private Integer idPriceR;
	private int maxPriceR;
	private int minPriceR;
	private int fixPriceR;
	private AccessoVeicoli typeEntryR;

	// variabili di ParameterGen
	private Integer idPG;
	private String namePG;
	private TipologiaParametro typePG;
	private String measureUnit;
	private boolean useType;
	private String description;

	// Variabili extra
	// ID del TimeSlot Creato
	private Integer idts;
	// check
	private ChooseBean cb;
	// elemento bean

	// size
	private int size;
	// Lista lista delle Fasce Orarie Create
	private List<TimeSlot> listaTS;

	// creo una lista liPa
	private List<ParameterGen> liPa;

	private boolean scelto;

	private List<ParameterGen> listaParametriScelti;

	private int sizeLiPa;

	private ParameterTS parameterTS;

	/**
	 * Creates a new instance of AddTimeSlotBean
	 */
	public AddTimeSlotBean() {
	}

	public Date getDayFinish() {
		return dayFinish;
	}

	public void setDayFinish(Date dayFinish) {
		this.dayFinish = dayFinish;
	}

	public Date getDayStart() {
		return dayStart;
	}

	public void setDayStart(Date dayStart) {
		this.dayStart = dayStart;
	}

	public Date getFinishTS() {
		return finishTS;
	}

	public void setFinishTS(Date finishTS) {
		this.finishTS = finishTS;
	}

	public int getFixPriceG() {
		return fixPriceG;
	}

	public void setFixPriceG(int fixPriceG) {
		this.fixPriceG = fixPriceG;
	}

	public int getFixPriceR() {
		return fixPriceR;
	}

	public void setFixPriceR(int fixPriceR) {
		this.fixPriceR = fixPriceR;
	}

	public int getFixPriceV() {
		return fixPriceV;
	}

	public void setFixPriceV(int fixPriceV) {
		this.fixPriceV = fixPriceV;
	}

	public Integer getIdParGen() {
		return idParGen;
	}

	public void setIdParGen(Integer idParGen) {
		this.idParGen = idParGen;
	}

	public Integer getIdPriceG() {
		return idPriceG;
	}

	public void setIdPriceG(Integer idPriceG) {
		this.idPriceG = idPriceG;
	}

	public Integer getIdPriceR() {
		return idPriceR;
	}

	public void setIdPriceR(Integer idPriceR) {
		this.idPriceR = idPriceR;
	}

	public Integer getIdPriceV() {
		return idPriceV;
	}

	public void setIdPriceV(Integer idPriceV) {
		this.idPriceV = idPriceV;
	}

	public Integer getIdTS() {
		return idTS;
	}

	public void setIdTS(Integer idTS) {
		this.idTS = idTS;
	}

	public TipoParametro getTypePar() {
		return typePar;
	}

	public void setTypePar(TipoParametro typePar) {
		this.typePar = typePar;
	}

	public Peso getWeight() {
		return weight;
	}

	public void setWeight(Peso weight) {
		this.weight = weight;
	}

	public Integer getIdts() {
		return idts;
	}

	public void setIdts(Integer idts) {
		this.idts = idts;
	}

	public List<ParameterGen> getListaParametriScelti() {
		return listaParametriScelti;
	}

	public void setListaParametriScelti(List<ParameterGen> listaParametriScelti) {
		this.listaParametriScelti = listaParametriScelti;
	}

	public int getMaxPriceG() {
		return maxPriceG;
	}

	public void setMaxPriceG(int maxPriceG) {
		this.maxPriceG = maxPriceG;
	}

	public int getMaxPriceR() {
		return maxPriceR;
	}

	public void setMaxPriceR(int maxPriceR) {
		this.maxPriceR = maxPriceR;
	}

	public int getMaxPriceV() {
		return maxPriceV;
	}

	public void setMaxPriceV(int maxPriceV) {
		this.maxPriceV = maxPriceV;
	}

	public double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(double maxVal) {
		this.maxVal = maxVal;
	}

	public int getMinPriceG() {
		return minPriceG;
	}

	public void setMinPriceG(int minPriceG) {
		this.minPriceG = minPriceG;
	}

	public int getMinPriceR() {
		return minPriceR;
	}

	public void setMinPriceR(int minPriceR) {
		this.minPriceR = minPriceR;
	}

	public int getMinPriceV() {
		return minPriceV;
	}

	public void setMinPriceV(int minPriceV) {
		this.minPriceV = minPriceV;
	}

	public double getMinVal() {
		return minVal;
	}

	public void setMinVal(double minVal) {
		this.minVal = minVal;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Date getStartTS() {
		return startTS;
	}

	public void setStartTS(Date startTS) {
		this.startTS = startTS;
	}

	public AperturaRichieste getTimeToAcceptRequest() {
		return timeToAcceptRequest;
	}

	public void setTimeToAcceptRequest(AperturaRichieste timeToAcceptRequest) {
		this.timeToAcceptRequest = timeToAcceptRequest;
	}

	public ChiusuraRichieste getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(ChiusuraRichieste timeToRun) {
		this.timeToRun = timeToRun;
	}

	public ChiusuraRichieste getTimeToStopRequest() {
		return timeToStopRequest;
	}

	public void setTimeToStopRequest(ChiusuraRichieste timeToStopRequest) {
		this.timeToStopRequest = timeToStopRequest;
	}

	public Tolleranza getTollerance() {
		return tollerance;
	}

	public void setTollerance(Tolleranza tollerance) {
		this.tollerance = tollerance;
	}

	public AccessoVeicoli getTypeEntryG() {
		return typeEntryG;
	}

	public void setTypeEntryG(AccessoVeicoli typeEntryG) {
		this.typeEntryG = typeEntryG;
	}

	public AccessoVeicoli getTypeEntryR() {
		return typeEntryR;
	}

	public void setTypeEntryR(AccessoVeicoli typeEntryR) {
		this.typeEntryR = typeEntryR;
	}

	public AccessoVeicoli getTypeEntryV() {
		return typeEntryV;
	}

	public void setTypeEntryV(AccessoVeicoli typeEntryV) {
		this.typeEntryV = typeEntryV;
	}

	public Ripetizione getWmy() {
		return wmy;
	}

	public void setWmy(Ripetizione wmy) {
		this.wmy = wmy;
	}

	public ChooseBean getCb() {
		return cb;
	}

	public void setCb(ChooseBean cb) {
		this.cb = cb;
	}

	public List<TimeSlot> getListaTS() {
		return listaTS;
	}

	public void setListaTS(List<TimeSlot> listaTS) {
		this.listaTS = listaTS;
	}

	public List<ParameterGen> getLiPa() {
		return liPa;
	}

	public void setLiPa(List<ParameterGen> liPa) {
		this.liPa = liPa;
	}

	public boolean isScelto() {
		return scelto;
	}

	public void setScelto(boolean scelto) {
		this.scelto = scelto;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIdPG() {
		return idPG;
	}

	public void setIdPG(Integer idPG) {
		this.idPG = idPG;
	}

	public TipologiaParametro getTypePG() {
		return typePG;
	}

	public void setTypePG(TipologiaParametro typePG) {
		this.typePG = typePG;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public String getNamePG() {
		return namePG;
	}

	public void setNamePG(String namePG) {
		this.namePG = namePG;
	}

	public int getSizeLiPa() {
		return sizeLiPa;
	}

	public void setSizeLiPa(int sizeLiPa) {
		this.sizeLiPa = sizeLiPa;
	}

	public boolean isUseType() {
		return useType;
	}

	public TipologiaClassifica getVikInd() {
		return vikInd;
	}

	public void setVikInd(TipologiaClassifica vikInd) {
		this.vikInd = vikInd;
	}

	public String getGa() {
		return ga;
	}

	public void setGa(String ga) {
		this.ga = ga;
	}

	/**
	 * <p>
	 * Method: addTimeSlot()
	 * </P>
	 * <p>
	 * Description: Memorizza i valori del TimeSlot che si vuole creare
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public TimeSlot createTimeSlot() {

		createTS = new TimeSlot();

		String oraInizio = "";
		String oraFine = "";

		String giornoInizio = "";
		String giornoI = "";
		String meseI = "";
		String annoI = "";

		String giornoFine = "";
		String giornoF = "";
		String meseF = "";
		String annoF = "";

		String[] oraInizioArray = new String[10];
		String[] oraFineArray = new String[10];
		String[] giornoInizioArray = new String[10];
		String[] giornoFineArray = new String[10];

		oraInizioArray = startTS.toString().split(" ")[3].split(":");
		oraInizio = oraInizioArray[0] + ":" + oraInizioArray[1];

		oraFineArray = finishTS.toString().split(" ")[3].split(":");
		oraFine = oraFineArray[0] + ":" + oraFineArray[1];

		giornoInizioArray = dayStart.toString().split(" ");
		giornoI = giornoInizioArray[2];
		meseI = giornoInizioArray[1];
		annoI = giornoInizioArray[5];

		if (meseI.equals("Jan"))
			meseI = "01";
		if (meseI.equals("Feb"))
			meseI = "02";
		if (meseI.equals("Mar"))
			meseI = "03";
		if (meseI.equals("Apr"))
			meseI = "04";
		if (meseI.equals("May"))
			meseI = "05";
		if (meseI.equals("Jun"))
			meseI = "06";
		if (meseI.equals("Jul"))
			meseI = "07";
		if (meseI.equals("Aug"))
			meseI = "08";
		if (meseI.equals("Sep"))
			meseI = "09";
		if (meseI.equals("Oct"))
			meseI = "10";
		if (meseI.equals("Nov"))
			meseI = "11";
		if (meseI.equals("Dec"))
			meseI = "12";

		giornoInizio = giornoI + "-" + meseI + "-" + annoI;

		giornoFineArray = dayFinish.toString().split(" ");
		giornoF = giornoFineArray[2];
		meseF = giornoFineArray[1];
		annoF = giornoFineArray[5];

		if (meseF.equals("Jan"))
			meseF = "01";
		if (meseF.equals("Feb"))
			meseF = "02";
		if (meseF.equals("Mar"))
			meseF = "03";
		if (meseF.equals("Apr"))
			meseF = "04";
		if (meseF.equals("May"))
			meseF = "05";
		if (meseF.equals("Jun"))
			meseF = "06";
		if (meseF.equals("Jul"))
			meseF = "07";
		if (meseF.equals("Aug"))
			meseF = "08";
		if (meseF.equals("Sep"))
			meseF = "09";
		if (meseF.equals("Oct"))
			meseF = "10";
		if (meseF.equals("Nov"))
			meseF = "11";
		if (meseF.equals("Dec"))
			meseF = "12";

		giornoFine = giornoF + "-" + meseF + "-" + annoF;

		logger.info(giornoFine);

		createTS.setStartTS(oraInizio);
		createTS.setFinishTS(oraFine);
		createTS.setDayStart(giornoInizio);
		createTS.setDayFinish(giornoFine);
		createTS.setWmy(wmy);
		createTS.setTollerance(tollerance);
		createTS.setTimeToAcceptRequest(timeToAcceptRequest);
		createTS.setTimeToStopRequest(timeToStopRequest);
		createTS.setTimeToRun(timeToStopRequest);
		createTS.setVikInd(vikInd);
		createTS.setRoundCode(ga);

		return createTS;
	}

	/**
	 * <p>
	 * Method: parametriTrue()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista di ParameterGen con UseType = 1
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public ArrayList<ParameterGenView> parametriTrue() throws Exception {

		listaAllParameterGen = new ArrayList<ParameterGen>();
		TimeSlotRestClient rcParametriTrue = new TimeSlotRestClient();
		listaAllParameterGen = rcParametriTrue.findAllParameterGenAvailable();
		allParameterGen = listaAllParameterGen.size();
		listaAllParameterGenView = new ArrayList<ParameterGenView>();
		for (int i = 0; i < allParameterGen; i++) {
			ParameterGenView parameterGenView = new ParameterGenView();
			parameterGenView.setDescription(listaAllParameterGen.get(i)
					.getDescription());
			parameterGenView.setIdPG(listaAllParameterGen.get(i).getId());
			parameterGenView.setTypePG(listaAllParameterGen.get(i).getTypePG());
			parameterGenView.setMeasureUnit(listaAllParameterGen.get(i)
					.getMeasureUnit());
			parameterGenView.setNamePG(listaAllParameterGen.get(i).getNamePG());
			parameterGenView
					.setUseType(listaAllParameterGen.get(i).isUseType());
			listaAllParameterGenView.add(parameterGenView);
		}
		return listaAllParameterGenView;
	}

	/**
	 * <p>
	 * Method: getTSList()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista delle Fasce Orarie Create
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<TimeSlot> getTSList() throws Exception {
		TimeSlotRestClient rcListaTS = new TimeSlotRestClient();
		listaTS = rcListaTS.findAllTimeSlots();
		return listaTS;
	}

	/**
	 * <p>
	 * Method: addPrice()
	 * </P>
	 * <p>
	 * Description: Memorizza i valori dei 3 Price
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public Price createPrice() throws Exception {

		priceRed = new Price();
		priceYellow = new Price();
		priceGreen = new Price();
		priceRed.setColor(ROSSO);
		priceRed.setFixPrice(fixPriceR);
		priceRed.setMaxPrice(maxPriceR);
		priceRed.setMinPrice(minPriceR);
		priceRed.setTypeEntry(typeEntryR);
		priceYellow.setColor(GIALLO);
		priceYellow.setFixPrice(0);
		priceYellow.setMaxPrice(maxPriceG);
		priceYellow.setMinPrice(minPriceG);
		priceYellow.setTypeEntry(typeEntryG);
		priceGreen.setColor(VERDE);
		priceGreen.setFixPrice(fixPriceV);
		priceGreen.setMaxPrice(fixPriceV);
		priceGreen.setMinPrice(fixPriceV);
		priceGreen.setTypeEntry(typeEntryV);

		addTimeSlot();

		return priceRed;
	}

	/**
	 * <p>
	 * Method: addTSStep2_BottoneSetta(int idParTS)
	 * </P>
	 * <p>
	 * Description: Il metodo viene chiamato alla pressione del bottone Setta.
	 * Il meotodo crea una nuovo ParameterTS e ne setta l'id
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public ParameterTS addTSStep2_BottoneSetta(int idParTS) {
		parameterTS = new ParameterTS();
		TimeSlotRestClient rc = new TimeSlotRestClient();
		try {
			List<ParameterGen> parameterGens = rc
					.findAllParameterGenAvailable();
			for (ParameterGen parameterGen : parameterGens)
				if (parameterGen.getId() == idParTS)
					parameterTS.setParGen(parameterGen);
		} catch (Exception e) {
			logger.error("richiesta parameter gen", e);
		}
		return parameterTS;
	}

	/**
	 * <p>
	 * Method: addParameterTS()
	 * </P>
	 * <p>
	 * Description: Il meotodo continua a settare il ParameterTS creato alla
	 * pressione del bottone set
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public ParameterTS createParameterTS() {

		for (int i = 0; i < listaParameterTSAggiornati.size(); i++) {
			if (listaParameterTSAggiornati.get(i).getParGen().getId() == parameterTS
					.getParGen().getId()) {
				listaParameterTSAggiornati.remove(i);
			}
		}

		parameterTS.setTypePar(DA_DECIDERE);
		parameterTS.setWeight(weight);
		parameterTS.setMaxValue(maxVal);
		parameterTS.setMinValue(minVal);

		listaParameterTSAggiornati.add(parameterTS);

		return parameterTS;
	}

	/**
	 * <p>
	 * Method: listaParTS()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista dei ParametriTS associati a un certo Time
	 * Slot
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<ParameterTSView> listaParTS() throws Exception {
		listaParTSView = new ArrayList<ParameterTSView>();
		for (int i = 0; i < listaParameterTSAggiornati.size(); i++) {
			parameterTSView = new ParameterTSView();

			TimeSlot ts = listaParameterTSAggiornati.get(i).getTs();
			if (ts != null)
				parameterTSView.setIdPTS(ts.getIdTS());
			parameterTSView.setParGen(listaParameterTSAggiornati.get(i)
					.getParGen());
			parameterTSView.setTs(ts);
			parameterTSView.setTypePar(listaParameterTSAggiornati.get(i)
					.getTypePar());
			parameterTSView.setWeight(listaParameterTSAggiornati.get(i)
					.getWeight());
			parameterTSView.setMaxVal(listaParameterTSAggiornati.get(i)
					.getMaxValue());
			parameterTSView.setMinVal(listaParameterTSAggiornati.get(i)
					.getMinValue());

			listaParTSView.add(parameterTSView);
		}
		return listaParTSView;
	}

	/**
	 * <p>
	 * Method: listaPriceTS()
	 * </P>
	 * <p>
	 * Description: Ritorna la lista dei Prezzi associati a un certo Time Slot
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public List<Price> listaPriceTS() throws Exception {

		TimeSlotRestClient rclistaPrice = new TimeSlotRestClient();
		listaPrice = new ArrayList<Price>();
		listaPrice = rclistaPrice.getPriceOfTimeSlot(idtsString);
		return listaPrice;
	}

	/**
	 * <p>
	 * Method: addTimeSlot()
	 * </P>
	 * <p>
	 * Description: Aggiunge il TimeSlot, i ParameterTS e i 3 Prezzi creati al
	 * database
	 * </P>
	 * 
	 * @param
	 * @return
	 */
	public void addTimeSlot() throws Exception {

		// Inserisco TimeSlot nel db
		Principal principal = getCurrentInstance().getExternalContext()
				.getUserPrincipal();
		if (principal != null)
			createTS.setPa(principal.getName());
		TimeSlotRestClient rcAddTimeSlot = new TimeSlotRestClient();
		SgotRestClient rcSgot = new SgotRestClient();
		createTS = rcAddTimeSlot.addTimeSlot(createTS);
		idts = createTS.getIdTS();
		Date d1 = new Date();
		for (int i = 0; i < 14; i++) {
			String shippingOrderId = createMockShippingId();
			Transport transport = new Transport(shippingOrderId);
			transport.setTimeSlot(createTS);
			transport.setDateMiss(d1);
			DBGeoLocation lellaGeoLocation = new DBGeoLocation(lella);
			lellaGeoLocation.setName(lella.getName());
			lellaGeoLocation.setSurname(lella.getSurname());
			lellaGeoLocation.setEmail(lella.getEmail());
			lellaGeoLocation.setPhone(lella.getPhone());
			lellaGeoLocation.setMobile(lella.getMobile());
			DBGeoLocation enzoGeoLocation = new DBGeoLocation(enzo);
			enzoGeoLocation.setName(enzo.getName());
			enzoGeoLocation.setSurname(enzo.getSurname());
			enzoGeoLocation.setEmail(enzo.getEmail());
			enzoGeoLocation.setPhone(enzo.getPhone());
			enzoGeoLocation.setMobile(enzo.getMobile());
			transport.setSource(new GeoLocation(lella));
			transport.setPickup(lellaGeoLocation);
			transport.setDestination(new GeoLocation(enzo));
			transport.setDropdown(enzoGeoLocation);
			transport.setServiceClass(rcSgot.findTransportServiceClass(
					"FURGONATO").get(0));
			transport.setTransportState(waiting);
			Freight freight = new Freight(shippingOrderId + 1);
			freight.setDescription(shippingOrderId);
			freight.setHeight(10);
			freight.setLeng(10);
			freight.setWidth(55);
			freight.setWeight(55);
			freight.setVolume(150);
			freight.setFt(DOCUMENTI);
			transport.setFreightItems(asList(new Freight[] { freight }));
			ShippingOrder shippingOrder = new ShippingOrder(shippingOrderId);
			shippingOrder.setMittente(lellaGeoLocation);
			shippingOrder.setDestinatario(enzoGeoLocation);
			shippingOrder.setTransport(transport);
			rcAddTimeSlot.simulSch(transport);
			rcSgot.addShipping(shippingOrder);
			ShippingItem shippingItem = convertiFreightToShippingItem(freight);
			shippingItem.setShippingOrder(shippingOrder);
			rcSgot.addShippingItem(shippingItem);
			d1 = addDays(d1, 1);
		}

		// Inserisco i ParameterTS nel db
		for (int i = 0; i < listaParameterTSAggiornati.size(); i++) {
			listaParameterTSAggiornati.get(i).setTs(createTS);
			rcAddTimeSlot
					.configParameterTsToTimeSlot(listaParameterTSAggiornati
							.get(i));
		}

		// Inserisco i Price nel db
		priceGreen.setTs(createTS);
		priceYellow.setTs(createTS);
		priceRed.setTs(createTS);
		priceRed = rcAddTimeSlot.addPrices(priceRed);
		priceYellow = rcAddTimeSlot.addPrices(priceYellow);
		priceGreen = rcAddTimeSlot.addPrices(priceGreen);

	}

	public void clear() {
		listaParTSView = null;
		listaAllParameterGenView = null;
		listaAllParameterGen = null;
		allParameterGen = 0;
		settaggio = null;
		parameterTSView = null;
		listaPrice = null;
		idtsString = null;
		createTS = null;
		listaParameterTSAggiornati = new ArrayList<ParameterTS>();
		priceRed = null;
		priceYellow = null;
		priceGreen = null;
		timeToAcceptRequest = null;
		timeToStopRequest = null;
		timeToRun = null;
		tollerance = null;
		wmy = null;
		startTS = null;
		finishTS = null;
		dayStart = null;
		dayFinish = null;
		vikInd = null;
		ga = null;
		idTS = null;
		idParGen = null;
		maxVal = 0.0;
		minVal = 0.0;
		weight = NESSUNO;
		typePar = DA_DECIDERE;
		idPriceV = null;
		maxPriceV = 0;
		minPriceV = 0;
		fixPriceV = 0;
		typeEntryV = GRATUITO;
		idPriceG = null;
		maxPriceG = 0;
		minPriceG = 0;
		fixPriceG = 0;
		typeEntryG = GRATUITO;
		idPriceR = null;
		maxPriceR = 0;
		minPriceR = 0;
		fixPriceR = 0;
		typeEntryR = NEGATO;
		idPG = null;
		namePG = null;
		typePG = BENEFICIO;
		measureUnit = null;
		useType = false;
		description = null;
		idts = null;
		cb = null;
		size = 0;
		listaTS = null;
		liPa = null;
		scelto = false;
		listaParametriScelti = null;
		sizeLiPa = 0;
		parameterTS = null;
	}

	public Ripetizione[] getRipetizioni() {
		return Ripetizione.values();
	}

	public AperturaRichieste[] getAperturaRichieste() {
		return AperturaRichieste.values();
	}

	public ChiusuraRichieste[] getChiusuraRichieste() {
		return ChiusuraRichieste.values();
	}

	public TipologiaClassifica[] getTipologiaClassifiche() {
		return TipologiaClassifica.values();
	}

	public Tolleranza[] getTolleranze() {
		return Tolleranza.values();
	}

	public Peso[] getPesi() {
		return Peso.values();
	}

	public AccessoVeicoli[] getAccessoVeicoliVerdi() {
		return asList(GRATUITO, PREZZO_FISSO).toArray(new AccessoVeicoli[0]);
	}

	public AccessoVeicoli[] getAccessoVeicoliGialli() {
		return asList(GRATUITO, PREZZO_VARIABILE)
				.toArray(new AccessoVeicoli[0]);
	}

	public AccessoVeicoli[] getAccessoVeicoliRossi() {
		return asList(NEGATO).toArray(new AccessoVeicoli[0]);
	}

}
