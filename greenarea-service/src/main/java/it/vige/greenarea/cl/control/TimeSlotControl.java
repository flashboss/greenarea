/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.control;

import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_FISSO;
import static it.vige.greenarea.dto.AccessoVeicoli.PREZZO_VARIABILE;
import static it.vige.greenarea.dto.Color.GIALLO;
import static it.vige.greenarea.dto.Color.VERDE;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.ParameterInfo;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.RequestParameter;
import it.vige.greenarea.cl.bean.TimeSlotInfo;
import it.vige.greenarea.cl.library.entities.ParameterGen;
import it.vige.greenarea.cl.library.entities.ParameterTS;
import it.vige.greenarea.cl.library.entities.Price;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.TsStat;
import it.vige.greenarea.cl.library.entities.VikorResult;
import it.vige.greenarea.cl.sessions.ParameterGenFacade;
import it.vige.greenarea.cl.sessions.ParameterTSFacade;
import it.vige.greenarea.cl.sessions.PriceFacade;
import it.vige.greenarea.cl.sessions.TimeSlotFacade;
import it.vige.greenarea.cl.sessions.TsStatFacade;
import it.vige.greenarea.cl.sessions.VikorResultFacade;
import it.vige.greenarea.dto.AperturaRichieste;
import it.vige.greenarea.dto.ChiusuraRichieste;
import it.vige.greenarea.dto.Peso;
import it.vige.greenarea.dto.Ripetizione;
import it.vige.greenarea.dto.TipoParametro;
import it.vige.greenarea.dto.TipologiaClassifica;
import it.vige.greenarea.dto.TipologiaParametro;
import it.vige.greenarea.dto.Tolleranza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.slf4j.Logger;

/**
 * <p>
 * Class: TimeSlotControl
 * </p>
 * <p>
 * Description: Questa classe ?? il core di cityLogistics, Gestisce tutte le
 * chiamate e risposte
 * </p>
 * 
 * @author City Logistics Team
 * @version 0.0
 */
@Stateless
public class TimeSlotControl {

	private Logger logger = getLogger(getClass());

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Resource(lookup = "java:/jdbc/gtgdb")
	private DataSource ds;

	@EJB
	private TimeSlotFacade tsf;
	@EJB
	private ParameterGenFacade pgf;
	@EJB
	private ParameterTSFacade ptsf;
	@EJB
	private PriceFacade pf;
	@EJB
	private TsStatFacade tssf;
	@EJB
	private VikorResultFacade vrf;

	/**
	 * <p>
	 * Method: AddSlotTime
	 * </p>
	 * <p>
	 * Description: metodo che aggiunge una fascia oraria inserendo tutti i
	 * parametri
	 * </p>
	 * 
	 * @param int timeToAcceptRequest,int timeToStopRequest,int timeToRun,int
	 *        tollerance,String wmy,String startTS,String finishTS,String
	 *        dayStart,String dayFinish,int vikInd
	 * @return TimeSlot
	 */
	public TimeSlot addSlotTime(AperturaRichieste timeToAcceptRequest,
			ChiusuraRichieste timeToStopRequest, ChiusuraRichieste timeToRun,
			Tolleranza tollerance, Ripetizione wmy, String startTS,
			String finishTS, String dayStart, String dayFinish,
			TipologiaClassifica vikInd) {
		if (wmy == null) {
			return null;
		}
		TimeSlot ts = new TimeSlot();
		ts.setTimeToAcceptRequest(timeToAcceptRequest);
		ts.setTimeToStopRequest(timeToStopRequest);
		ts.setTimeToRun(timeToRun);
		ts.setTollerance(tollerance);
		ts.setWmy(wmy);
		ts.setDayStart(dayStart);
		ts.setDayFinish(dayFinish);
		ts.setStartTS(startTS);
		ts.setFinishTS(finishTS);
		ts.setVikInd(vikInd);
		tsf.create(ts);
		return ts;

	}

	/**
	 * <p>
	 * Method: findTimeSlot
	 * </p>
	 * <p>
	 * Description: cerca una fascia oraria per Id
	 * </p>
	 * 
	 * @Param int idTimeSlot
	 * @return TimeSlot ts
	 * 
	 */
	public TimeSlot findTimeSlot(int idTimeSlot) {
		return tsf.find(idTimeSlot);
	}

	/**
	 * <p>
	 * Method: addSlotTime
	 * </p>
	 * <p>
	 * Description: add a Slot Time
	 * 
	 * @Param TimeSlot ts
	 * @return TimeSlot ts
	 * 
	 */
	public TimeSlot addSlotTime(TimeSlot ts) {
		tsf.create(ts);
		return ts;
	}

	/**
	 * <p>
	 * Method: addSlotTime
	 * </p>
	 * <p>
	 * Description: add a Slot Time
	 * 
	 * @Param TimeSlot ts
	 * @return TimeSlot ts
	 * 
	 */
	public TimeSlot updateSlotTime(TimeSlot ts) {
		tsf.edit(ts);
		return ts;
	}

	/**
	 * <p>
	 * Method: deleteSlotTime
	 * </p>
	 * <p>
	 * Description: remove a Slot Time
	 * 
	 * @Param TimeSlot ts
	 * @return TimeSlot ts
	 * 
	 */
	public TimeSlot deleteSlotTime(TimeSlot ts) {
		TimeSlot timeSlot = tsf.find(ts.getIdTS());
		List<Price> prices = pf.findAll(timeSlot);
		for (Price price : prices)
			pf.remove(price);
		List<ParameterTS> parameters = ptsf.findAll(timeSlot);
		for (ParameterTS parameter : parameters)
			ptsf.remove(parameter);
		tsf.remove(timeSlot);
		return tsf.find(ts.getIdTS());
	}

	/**
	 * <p>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @return
	 * 
	 * 
	 */
	public List<TimeSlot> findAllTimeSlots() {
		return tsf.findAll();
	}

	/**
	 * <p>
	 * Method: findAllTimeSlots(String userId)
	 * </p>
	 * <p>
	 * Description: Cerca le fasce orarie disponibili alla configurazione, cio??????
	 * con nome utente scelto
	 * </p>
	 * 
	 * @param
	 * @return List<TimeSlot>
	 */
	public List<TimeSlot> findAllTimeSlots(String userId) {

		List<TimeSlot> result = em
				.createQuery("SELECT c FROM TimeSlot c WHERE  c.pa = :user")
				.setParameter("user", userId).getResultList();

		if (result.isEmpty()) {
			return null;
		}
		return result;
	}

	/**
	 * <p>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @return
	 */
	public List<ParameterGen> findAllParameterGen() {
		return pgf.findAll();
	}

	/**
	 * <p>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @return
	 */
	public void updateParameterGen(ParameterGen pg) {
		pgf.edit(pg);
	}

	/**
	 * <p>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @return
	 */
	public ParameterGen deleteParameterGen(ParameterGen pg) {
		pgf.remove(pg);
		return pgf.find(pg.getId());
	}

	/**
	 * <p>
	 * </p>
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @return
	 */
	public void createParameterGen(ParameterGen pg) {
		pgf.create(pg);
	}

	/**
	 * <p>
	 * Method: findParameterGenAvailable
	 * </p>
	 * <p>
	 * Description: Cerca i parametri disponibili alla configurazione, cio??????????????????
	 * con useType=1
	 * </p>
	 * 
	 * @param
	 * @return List<ParameterGen>
	 */
	public List<ParameterGen> findParameterGenAvailable() {

		List<ParameterGen> result = em.createQuery(
				"SELECT c FROM ParameterGen c WHERE c.useType is true")
				.getResultList();

		if (result.isEmpty()) {
			return null;
		}
		return result;
	}

	/**
	 * <p>
	 * Method: addParameterGen
	 * </p>
	 * <p>
	 * Descriprion: Aggiunge un parametro generale
	 * </p>
	 * 
	 * @param ParameterGen
	 *            pg
	 * @return ParameterGen
	 */
	public ParameterGen addParameterGen(ParameterGen pg) {
		pgf.create(pg);
		return pg;
	}

	/**
	 * <p>
	 * Method: addParameterGen
	 * </p>
	 * <p>
	 * Descriprion: Aggiunge un parametro generale al sistema inserendo i valori
	 * </p>
	 * 
	 * @param String
	 *            nameP,int typePG,String measureUnit,boolean useType,String
	 *            description
	 * @return ParameterGen
	 */
	public ParameterGen addParameterGen(String nameP,
			TipologiaParametro typePG, String measureUnit, boolean useType,
			String description) {
		// check control input
		if (nameP == null) {
			return null;
		}
		ParameterGen ptg = new ParameterGen();
		ptg.setNamePG(nameP);
		ptg.setTypePG(typePG);
		ptg.setMeasureUnit(measureUnit);
		ptg.setDescription(description);
		ptg.setUseType(useType);
		pgf.create(ptg);
		return ptg;
	}

	/**
	 * <p>
	 * Method: configParameterToTimeSlot
	 * </p>
	 * <p>
	 * Description: Configura un parametroTS
	 * </p>
	 * 
	 * @param ParameterTS
	 *            pts
	 * @return ParameterTS
	 */
	public ParameterTS configParameterToTimeSlot(ParameterTS pts) {
		pts.setId(null);
		ptsf.create(pts);
		return pts;

	}

	/**
	 * <p>
	 * Method: addPriceToTimeSlot
	 * </p>
	 * <p>
	 * Description: Aggiunge un pedaggio a una fascia oraria
	 * </p>
	 * 
	 * @param Price
	 *            Price
	 * @return void
	 */
	public void addPriceToTimeSlot(Price price) {
		pf.create(price);
	}

	/**
	 * <p>
	 * Method: updatePriceToTimeSlot
	 * </p>
	 * <p>
	 * Description: Aggiorna un prezzo a una fascia oraria
	 * </p>
	 * 
	 * @param Price
	 *            Price
	 * @return void
	 */
	public void updatePriceToTimeSlot(Price price) {
		List<Price> prices = pf.findAll(price.getTs());
		for (Price prize : prices)
			if (prize.getColor().equals(price.getColor())) {
				prize.setFixPrice(price.getFixPrice());
				prize.setMaxPrice(price.getMaxPrice());
				prize.setMinPrice(price.getMinPrice());
				prize.setTypeEntry(price.getTypeEntry());
				pf.edit(prize);
			}
	}

	/**
	 * <p>
	 * Method: selectMission
	 * </p>
	 * <p>
	 * Description: Seleziona una tipologia di richieste per una fascia oraria e
	 * una certa data in base ad alcuni parametri typePg
	 * </p>
	 * 
	 * @param int dateMission,int idTimeSlot,int typePg
	 * @return List<Request>
	 */
	public List<Request> selectMission(Date dateMission, int idTimeSlot,
			int typePg) {

		List<Request> rList = new ArrayList<Request>();

		ResultSet rs = null;
		Connection con = null;
		PreparedStatement ps = null;

		try {

			con = ds.getConnection();
			ps = con.prepareStatement("SELECT b.id,a.idParameter,b.company,b.name,b.truck_PLATENUMBER,c.namePG,b.timeSlot_idTS,b.startTime, a.valuePar  "
					+ "FROM Mission as b JOIN ValueMission as a JOIN ParameterGen as c WHERE b.ID = a.mission_id AND a.idparameter = c.idpg "
					+ "AND DATE(b.startTime) = ? AND b.timeSlot_idTS = ? AND c.typePg <? ORDER by a.mission_id, a.idParameter  ");
			logger.debug("query per missioni: " + ps);
			java.sql.Date time = new java.sql.Date(dateMission.getTime());
			ps.setDate(1, time);
			logger.debug("query per missioni time: " + time);
			ps.setInt(2, idTimeSlot);
			logger.debug("query per missioni idTimeSlot: " + idTimeSlot);
			ps.setInt(3, typePg);
			logger.debug("query per missioni typePg: " + typePg);
			rs = ps.executeQuery();
			int i = 0;

			while (rs.next()) {

				Request r = new Request();
				RequestParameter rp = new RequestParameter();

				if (rList.isEmpty()) {
					// Se la lista ?? vuota creo una request e
					// un primo
					// request
					// parameter
					r.setDateMiss(rs.getDate("startTime"));
					r.setIdMission(rs.getInt("id"));
					r.setUserName(rs.getString("name"));
					r.setCompany(rs.getString("company"));
					r.setCarPlate(rs.getString("truck_PLATENUMBER"));
					r.setIdTimeSlot(rs.getInt("timeSlot_idTS"));
					rList.add(r);
					i++;
				}
				if (!rList.isEmpty()) {
					if (rs.getInt("id") == rList.get(i - 1).getIdMission()) {
						rp.setIdParameter(rs.getInt("idParameter"));
						rp.setValuePar(rs.getDouble("valuePar"));
						rp.setName(rs.getString("namePG"));
						rList.get(i - 1).addRequestParameter(rp);
					}
				}
				if (rs.getInt("id") != rList.get(i - 1).getIdMission()) {
					r.setDateMiss(rs.getDate("startTime"));
					r.setIdMission(rs.getInt("id"));
					r.setUserName(rs.getString("name"));
					r.setCompany(rs.getString("company"));
					r.setCarPlate(rs.getString("truck_PLATENUMBER"));
					r.setIdTimeSlot(rs.getInt("timeSlot_idTS"));
					rp.setIdParameter(rs.getInt("idParameter"));
					rp.setValuePar(rs.getDouble("valuePar"));
					rp.setName(rs.getString("namePG"));
					r.addRequestParameter(rp);
					rList.add(r);
					i++;
				}
			}
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		} finally {
			connectionClose(rs, ps, con);
		}
		return rList;
	}

	/**
	 * <p>
	 * Method: getParameterForRank
	 * </p>
	 * <p>
	 * Description: Dato un idTimeSlot restituisce i parametri configurati per
	 * quella fascia oraria
	 * </p>
	 * 
	 * @param int idTimeSlot
	 * @return List<ParameterTS>
	 */
	public List<ParameterTS> getParameterForRank(int idTimeSlot) {

		List<ParameterTS> ptsList = null;
		String qu = "SELECT c FROM ParameterTS c WHERE c.ts.idTS = :idTimeSlot AND c.typePar<2  ORDER BY c.parGen.idPG";
		Query query = em.createQuery(qu);
		query.setParameter("idTimeSlot", idTimeSlot);
		ptsList = query.getResultList();
		return ptsList;
	}

	/**
	 * <p>
	 * Method: getParameterTSofTimeSlot
	 * </p>
	 * <p>
	 * Description: Restituisce una lista di ParameterTS dato un idTimeSlot
	 * </p>
	 * 
	 * @param int idTimeSlot
	 * @return Lisst<ParameterTS>
	 */
	public List<ParameterTS> getParameterTSofTimeSlot(int idTimeSlot) {

		List<ParameterTS> ptsList = null;
		String qu = "SELECT c FROM ParameterTS c WHERE c.ts.idTS = :idTimeSlot AND ORDER BY c.parGen.idPG ";
		Query query = em.createQuery(qu);
		query.setParameter("idTimeSlot", idTimeSlot);
		ptsList = query.getResultList();
		return ptsList;
	}

	/**
	 * <p>
	 * Method: getInfoTimeSlot
	 * </p>
	 * <p>
	 * Restituisce le informazioni di una fascia oraria
	 * </p>
	 * 
	 * @param idTimeSlot
	 * @return TimeSlotInfo
	 */
	public TimeSlotInfo getInfoTimeSlot(int idTimeSlot) {

		ResultSet rs = null;
		Connection con = null;
		PreparedStatement ps = null;
		TimeSlotInfo tsi = new TimeSlotInfo();

		try {

			con = ds.getConnection();
			ps = con.prepareStatement("SELECT * FROM TimeSlot as a JOIN ParameterGen as b JOIN ParameterTS as c "
					+ "WHERE a.IDTS=? AND a.IDTS = c.ts_IDTS AND b.IDPG = c.parGen_idPG ");
			ps.setInt(1, idTimeSlot);
			rs = ps.executeQuery();
			int i = 0;

			while (rs.next()) {

				if (i == 0) {
					logger.info("Dentro if");
					tsi.setIdTS(rs.getInt("idTS"));
					logger.info("IdTS=" + rs.getInt("idTS")
							+ rs.getInt("tollerance"));

					tsi.setStartTS(rs.getString("startTS"));
					tsi.setFinishTS(rs.getString("finishTS"));
					tsi.setDayStart(rs.getString("dayStart"));
					tsi.setDayFinish(rs.getString("dayFinish"));
					tsi.setTollerance(rs.getInt("tollerance"));
					tsi.setTimeToAcceptRequest(rs.getInt("timeToAcceptRequest"));
					tsi.setTimeToStopRequest(rs.getInt("timeToStopRequest"));
					tsi.setTimeToRun(rs.getInt("timeToRun"));
					tsi.setWmy(rs.getString("wmy"));

					ParameterInfo pi = new ParameterInfo();
					pi.setTypePG(TipologiaParametro.values()[rs
							.getInt("typePG")]);
					pi.setTypePar(TipoParametro.values()[rs.getInt("typePar")]);
					pi.setNamePG(rs.getString("namePG"));
					pi.setDescription(rs.getString("description"));
					pi.setMeasureUnit(rs.getString("measureUnit"));
					pi.setWeight(Peso.values()[rs.getInt("weight")]);
					pi.setMaxVal(rs.getDouble("maxVal"));
					pi.setMinVal(rs.getDouble("minVal"));
					pi.setUseType(rs.getBoolean("useType"));
					pi.setIdPg(rs.getInt("idPG"));
					tsi.addParameterInfo(pi);
				}
				if (i > 0) {
					ParameterInfo pi = new ParameterInfo();
					pi.setTypePG(TipologiaParametro.values()[rs
							.getInt("typePG")]);
					pi.setTypePar(TipoParametro.values()[rs.getInt("typePar")]);
					pi.setNamePG(rs.getString("namePG"));
					pi.setDescription(rs.getString("description"));
					pi.setMeasureUnit(rs.getString("measureUnit"));
					pi.setWeight(Peso.values()[rs.getInt("weight")]);
					pi.setMaxVal(rs.getDouble("maxVal"));
					pi.setMinVal(rs.getDouble("minVal"));
					pi.setUseType(rs.getBoolean("useType"));
					pi.setIdPg(rs.getInt("idPG"));
					tsi.addParameterInfo(pi);
				}
				i++;
			}
			// con.commit();
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		} finally {
			connectionClose(rs, ps, con);
		}
		return tsi;
	}

	private void connectionClose(ResultSet rs, PreparedStatement ps,
			Connection conn) {

		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		}
	}

	/**
	 * <p>
	 * Method: makeWeight
	 * </p>
	 * <p>
	 * Description: Dato un array ordinato di numeri interi rappresentanti un
	 * valore di peso, calcola il loro peso reale
	 * </p>
	 * 
	 * @param int[] weight
	 * @return double[]
	 */
	public double[] makeWeight(int[] weight) {

		double s = 0;
		int n = weight.length;

		int[] sum = new int[n];

		double[] weights = new double[n];
		int[][] w = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {

				if (weight[i] == weight[j]) {
					w[i][j] = 2;
				}
				if (weight[i] > weight[j]) {
					w[i][j] = 3;
				}
				if (weight[i] < weight[j]) {
					w[i][j] = 1;
				}
				sum[i] = sum[i] + w[i][j];

			}
			s = s + sum[i];
		}
		for (int k = 0; k < n; k++) {
			weights[k] = (sum[k] / s);
		}
		return weights;
	}

	/**
	 * <p>
	 * Method: updateQuForRequest
	 * </p>
	 * <p>
	 * Descriptio: metodo privato per aggiornamento dei valori di ranking
	 * </p>
	 * 
	 * @param List
	 *            <Request> reqList
	 * @return void
	 */
	private void updateQuForRequest(List<Request> reqList) {

		int size = reqList.size();

		if (size > 0) {

			for (int i = 0; i < size; i++) {
				VikorResult vr = new VikorResult();
				vr.setIdMission(reqList.get(i).getIdMission());
				double[] ques = new double[3];
				ques = reqList.get(i).getQu();
				vr.setPrice(reqList.get(i).getPrice());
				vr.setColor(reqList.get(i).getColor());
				vr.setDateMission(reqList.get(i).getDateMiss());
				vr.setqH(ques[2]);
				vr.setqM(ques[1]);
				vr.setqL(ques[0]);
				vrf.create(vr);
			}
		}
		if (size == 0)
			logger.error("Input Empty");

	}

	/**
	 * <p>
	 * Method: getRank
	 * </p>
	 * <p>
	 * Description: Data una data e una idTimeSlot calcola il ranking delle
	 * missioni e restituisce una lista processata
	 * </p>
	 * 
	 * @param int dateMission es: gmaaaa, int idTimeSlot
	 * @return
	 */
	public List<Request> getRank(Date dateMission, int idTimeSlot) {
		logger.info(dateMission + "        " + idTimeSlot);
		List<Request> reqList = this.simulRank(dateMission, idTimeSlot);
		return updateVikor(reqList, dateMission, idTimeSlot);
	}

	/**
	 * <p>
	 * Method: getRank
	 * </p>
	 * <p>
	 * Description: Data una data e una idTimeSlot calcola il ranking delle
	 * missioni e restituisce una lista processata
	 * </p>
	 * 
	 * @param int dateMission es: gmaaaa, int idTimeSlot
	 * @return
	 */
	public List<Request> updateVikor(List<Request> reqList, Date dateMission,
			int idTimeSlot) {
		if (reqList.isEmpty())
			return null;
		TimeSlot ts = this.findTimeSlot(idTimeSlot);
		if (ts == null)
			return null;
		// Aggiorno i valori di Q per ogni Missione
		updateQuForRequest(reqList);
		TsStat tsStat = new TsStat();
		int g = 0;
		int y = 0;
		for (int i = 0; i < reqList.size(); i++) {
			if (reqList.get(i).getColor().equals(VERDE))
				g++;
			if (reqList.get(i).getColor().equals(GIALLO))
				y++;
		}

		tsStat.setInKo(y);
		tsStat.setInOk(g);
		tsStat.setInWithPayment(g + y);
		tsStat.setTotalReq(reqList.size());
		tsStat.setDateMission(dateMission);
		tsStat.setIdTimeSlot(idTimeSlot);
		tssf.create(tsStat);
		return reqList;
	}

	/**
	 * <p>
	 * Method: printVector
	 * </p>
	 * <p>
	 * Description: stampa un vettore di numeri reali
	 * </p>
	 * 
	 * @param double[][] vector
	 * @return void
	 */
	public void printVector(double[][] vect) {
		logger.info("Print Vector");
		int rows = vect.length;
		int columns = vect[0].length;
		String stamp = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				stamp = stamp + " " + vect[i][j] + " ";
			}
			stamp += "\n";
		}
		logger.info("\n" + stamp);
	}

	/**
	 * <p>
	 * Method: getPriceOfTimeSlot
	 * </p>
	 * <p>
	 * Title: return the pricing of a Time Slot
	 * </p>
	 * 
	 * @param idTimeSlot
	 * @return List
	 */
	public List<Price> getPriceOfTimeSlot(int idTimeSlot) {

		List<Price> priceList = new ArrayList<Price>();

		try {
			Query query = em
					.createQuery("SELECT a FROM Price a where a.ts.idTS=:timeslot ORDER BY a.idPrice");
			query.setParameter("timeslot", idTimeSlot);
			priceList = (List<Price>) query.getResultList();
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		}

		return priceList;
	}

	/**
	 * <p>
	 * Method: getAllStats
	 * </p>
	 * <p>
	 * Description: restituisce una lista di tutte le statistiche nel DB
	 * </p>
	 * 
	 * @param
	 * @return List<TsStats>
	 */
	public List<TsStat> getAllStats() {
		return tssf.findAll();
	}

	/**
	 * <p>
	 * Method: getStoryBoard
	 * </p>
	 * <p>
	 * Description: restituisce lo storico di una fascia oraria, e della
	 * giornata in cui ?????? stata processata
	 * </p>
	 * 
	 * @param int idMission, int dateMiss
	 * @return List<Request>
	 */
	public List<Request> getStoryBoard(int idTimeSlot, Date dateMiss) {

		List<Request> reqList = selectMission(dateMiss, idTimeSlot, 5);

		if (reqList.isEmpty())
			return null;

		try {

			Query query = em
					.createQuery("SELECT a FROM VIKORRESULT a WHERE a.datemission = :dateMission ORDER BY a.idMission");
			query.setParameter("dateMission",
					new java.sql.Date(dateMiss.getTime()));
			@SuppressWarnings("unchecked")
			List<VikorResult> rs = (List<VikorResult>) query.getResultList();

			int i = 0;
			for (VikorResult vikorResult : rs) {
				logger.info("ID : " + reqList.get(i).getIdMission()
						+ " --> ID " + vikorResult.getIdMission());
				if (reqList.get(i).getIdMission() == vikorResult.getIdMission()) {
					reqList.get(i).setPrice(vikorResult.getPrice());
					reqList.get(i).setColor(vikorResult.getColor());
				}
				i++;
			}
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		}

		return reqList;
	}

	/**
	 * <p>
	 * Method: simulRank
	 * </p>
	 * <p>
	 * Description: Calcola il ranking delle richieste per una fascia oraria per
	 * giorno
	 * </p>
	 * 
	 * @param int dateMission, int idTimeSlot
	 * @return Array con 3 risultati Q0.25 Q0.5 e Q.0.75
	 */

	public List<Request> simulRank(Date dateMission, int idTimeSlot) {
		// Carico i parametri che vincolano una Fascia Oraria
		List<ParameterTS> ptsList = getParameterForRank(idTimeSlot);
		int size = ptsList.size();
		if (size == 0) {
			logger.debug("simulRank getParameterForRank size = null");
			return null;
		}
		int typePg = 3;
		// Carico tutte le missioni per quella precisa fascia oraria e data
		List<Request> reqList = selectMission(dateMission, idTimeSlot, typePg);
		int sizeReqList = reqList.size();
		if (sizeReqList == 0) {
			logger.debug("simulRank selectMission size = 0");
		} else {
			// Carico la fascia oraria
			TimeSlot ts = findTimeSlot(idTimeSlot);
			Tolleranza tollerance = ts.getTollerance();

			List<RequestParameter> reqParList = new ArrayList<RequestParameter>();

			logger.info("ReqList size: " + sizeReqList + " PTSList size: "
					+ size);
			double[][] valuesMission = new double[sizeReqList][size];

			for (int i = 0; i < sizeReqList; i++) {
				// Di ogni richiesta prendo i suoi parametri

				reqParList = reqList.get(i).getReqParList();
				if (!reqParList.isEmpty()) {
					for (int c = 0; c < size; c++) {
						logger.info("Valore Parametro List"
								+ reqParList.get(c).getValuePar());
					}
					for (int j = 0; j < size; j++) {
						valuesMission[i][j] = reqParList.get(j).getValuePar();
						logger.info("Valore : " + valuesMission[i][j]
								+ " Riga: " + i + " Colonna: " + j);
					}
				}
			}
			logger.info("+++++++++++++++++++++++++++++++++++");
			printVector(valuesMission);
			logger.info("+++++++++++++++++++++++++++++++++++");

			int[] coBe = new int[size];
			double[] ideale = new double[size];
			double[] arTollerance = new double[size];
			double[] antiIdeale = new double[size];
			int[] vpesi = new int[size];

			for (int i = 0; i < size; i++) {

				coBe[i] = ptsList.get(i).getTypePar().ordinal();
				ideale[i] = ptsList.get(i).getMaxValue();
				antiIdeale[i] = ptsList.get(i).getMinValue();
				vpesi[i] = ptsList.get(i).getWeight().ordinal();

				if (coBe[i] == 0)
					arTollerance[i] = antiIdeale[i]
							+ ((antiIdeale[i] * tollerance.getValue()) / 100);

				if (coBe[i] == 1) { // selected TypePar = cost
					double a = ideale[i];
					ideale[i] = antiIdeale[i];
					antiIdeale[i] = a;
					arTollerance[i] = antiIdeale[i]
							- ((antiIdeale[i] * tollerance.getValue()) / 100);
				}
			}

			double[] pesi = makeWeight(vpesi);
			int rows = valuesMission.length;
			int columns = valuesMission[0].length;
			int numIdea = ideale.length;
			int numAntiIdea = antiIdeale.length;
			int numPesi = pesi.length;

			if (columns != numIdea || columns != numAntiIdea
					|| numIdea != numAntiIdea || columns != numPesi) {
				logger.error("Errore nell'inserimento dati");
				return null;
			}
			double[][] normalinonpesati = new double[rows][columns];
			double[][] normali = new double[rows][columns];
			double[] erre = new double[rows];
			double[] esse = new double[rows];
			double[][] qu = new double[rows][3];
			logger.info("Righe :" + qu.length + ";" + "Colonne : "
					+ qu[0].length + ";");

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (j == 0)
						esse[j] = 0;
					normali[i][j] = pesi[j] * (ideale[j] - valuesMission[i][j])
							/ (ideale[j] - antiIdeale[j]);
					normalinonpesati[i][j] = (ideale[j] - valuesMission[i][j])
							/ (ideale[j] - antiIdeale[j]);
					esse[i] += normali[i][j];
					if (j == 0)
						erre[i] = normali[i][j];
					if (j > 0)
						erre[i] = normali[i][j] > normali[i][j - 1] ? normali[i][j]
								: normali[i][j - 1];

				}

				qu[i][0] = 0.5 * esse[i] + (1 - 0.5) * erre[i];
				qu[i][1] = 0.75 * esse[i] + (1 - 0.75) * erre[i];
				qu[i][2] = 0.25 * esse[i] + (1 - 0.25) * erre[i];
				reqList.get(i).setQu(qu[i]); // Check
			}
			logger.info("Stampa normali  non pesati");
			printVector(normalinonpesati);
			logger.info("Stampa normali pesati");
			printVector(normali);
			logger.info("Stampa qu");
			printVector(qu);

			// Questo ciclo for sposta costi/beneficio e calcola il veicolo
			// tolleranza
			for (int i = 0; i < size; i++) {
				coBe[i] = ptsList.get(i).getTypePar().ordinal();
				ideale[i] = ptsList.get(i).getMaxValue();
				logger.info("Max " + ideale[i]);
				antiIdeale[i] = ptsList.get(i).getMinValue();
				vpesi[i] = ptsList.get(i).getWeight().ordinal();

				if (coBe[i] == 0)
					arTollerance[i] = antiIdeale[i]
							+ ((antiIdeale[i] * tollerance.getValue()) / 100);

				if (coBe[i] == 1) { // selected TypePar = cost
					double a = ideale[i];
					ideale[i] = antiIdeale[i];
					antiIdeale[i] = a;
					arTollerance[i] = antiIdeale[i]
							- ((antiIdeale[i] * tollerance.getValue()) / 100);
				}
			}

			String idea = " ";
			String anti = " ";
			String toll = " ";

			pesi = makeWeight(vpesi);
			// Calcolo q per il veicolo tolleranza
			double[] normaliQu = new double[size];
			double erreTol = 0;
			double esseTol = 0;
			double[] quTol = new double[3];
			logger.info("Ci sono ParameterTS  " + size);

			for (int j = 0; j < size; j++) {
				if (j == 0)
					esseTol = 0;

				normaliQu[j] = pesi[j] * (ideale[j] - arTollerance[j])
						/ (ideale[j] - antiIdeale[j]);
				esseTol += normaliQu[j];

				if (j == 0)
					erreTol = normaliQu[j];
				if (j > 0)
					erreTol = normaliQu[j] > normaliQu[j - 1] ? normaliQu[j]
							: normaliQu[j - 1];

				quTol[0] = 0.5 * esseTol + (1 - 0.5) * erreTol;
				quTol[1] = 0.75 * esseTol + (1 - 0.75) * erreTol;
				quTol[2] = 0.25 * esseTol + (1 - 0.25) * erreTol;
			}
			for (int i = 0; i < size; i++) {

				idea += " " + ideale[i] + " ";
				anti += "" + antiIdeale[i] + " ";
				toll += " " + arTollerance[i] + "";
			}
			logger.info("Ideale: " + idea);
			logger.info("AntiIdeale: " + anti);
			logger.info("Tollerato : " + toll);

			logger.info(quTol[0] + " " + quTol[1] + " " + quTol[2] + " ");
			Price green = null, yellow = null;
			List<Price> priceList = getPriceOfTimeSlot(idTimeSlot);
			for (int i = 0; i < priceList.size(); i++) {
				if (priceList.get(i).getColor().equals(VERDE))
					green = priceList.get(i);
				if (priceList.get(i).getColor().equals(GIALLO))
					yellow = priceList.get(i);
			}
			TipologiaClassifica vikorIdx = ts.getVikInd();
			int reqSize = reqList.size();

			for (int i = 0; i < reqSize; i++) {
				double[] ques = reqList.get(i).getQu();
				reqList.get(i).setDateMiss(dateMission);
				if (ques[vikorIdx.getValue()] >= quTol[vikorIdx.getValue()]
						&& yellow != null) {// Davide
					// situazione gialla
					reqList.get(i).setColor(GIALLO);
					if (yellow.getTypeEntry() == PREZZO_VARIABILE) {
						double price = yellow.getMaxPrice()
								- ques[vikorIdx.getValue()]
								/ (yellow.getMaxPrice() - yellow.getMinPrice());
						reqList.get(i).setPrice(price);
					}
					if (yellow.getTypeEntry() == PREZZO_FISSO) {
						reqList.get(i).setPrice(yellow.getFixPrice());
					}
				}
				if (ques[vikorIdx.getValue()] <= quTol[vikorIdx.getValue()]
						&& green != null) {// Davide
					// situzione verde
					reqList.get(i).setColor(VERDE);
					if (green.getTypeEntry() == PREZZO_VARIABILE) {
						double price = green.getMaxPrice()
								- ques[vikorIdx.getValue()]
								/ (green.getMaxPrice() - green.getMinPrice());
						reqList.get(i).setPrice(price);
					}
					if (green.getTypeEntry() == PREZZO_FISSO) {
						reqList.get(i).setPrice(green.getFixPrice());
					}
				}
			}
		}
		return reqList;
	}
}
