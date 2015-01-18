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
package it.vige.greenarea.cl.scheduling;

import static it.vige.greenarea.Conversioni.addDays;
import static it.vige.greenarea.cl.scheduling.Tone.sound;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.control.TimeSlotControl;
import it.vige.greenarea.cl.library.entities.Freight;
import it.vige.greenarea.cl.library.entities.TimeSlot;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.sessions.TimeSlotFacade;
import it.vige.greenarea.dto.Richiesta;
import it.vige.greenarea.gtg.db.facades.FreightFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sound.sampled.LineUnavailableException;

import org.slf4j.Logger;

/**
 *
 * 
 */

@Stateless
public class Scheduler {

	private Logger logger = getLogger(getClass());

	DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@EJB
	private TimeSlotFacade tsf;
	@EJB
	private TransportFacade sf;
	@EJB
	private FreightFacade ff;
	@EJB
	private ShippingOrderFacade sof;
	@EJB
	private TimeSlotControl tsc;
	@Resource
	private TimerService timerService;

	ArrayList<Transport> listSchedule;

	/**
     * 
     */
	public Scheduler() {

		this.listSchedule = new ArrayList<Transport>();
	}

	/**
	 * 
	 * @param sc
	 */
	public void addSchedule(Transport sc) {
		List<Freight> freights = sc.getFreightItems();
		if (freights != null)
			for (Freight freight : freights) {
				freight.setTransport(sc);
				ff.create(freight);
			}
		sf.create(sc);
	}

	/**
	 * 
	 * @param sc
	 */
	public void removeSchedule(Transport sc) {
		this.listSchedule.remove(sc);
	}

	/**
	 * 
	 * @param days
	 */
	public void editSchedule(Transport sc) {
		try {
			TimeSlot tiSlo = sc.getTimeSlot();
			String timeTS = tiSlo.getStartTS() + " " + tiSlo.getFinishTS();
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			String dateTs = fmt.format(sc.getDateMiss()) + " " + timeTS;
			sc.setTimeSlot(tiSlo);
			sc.setTimeAccept(makeDate(dateTs, tiSlo.getTimeToAcceptRequest()
					.getValue()));
			sc.setTimeClosing(makeDate(dateTs, tiSlo.getTimeToStopRequest()
					.getValue()));
			sc.setTimeRank(makeDate(dateTs, tiSlo.getTimeToRun().getValue()));

		} catch (Exception e) {
			logger.error("scheduler ", e);
		}

	}

	/**
	 * 
	 * @param date
	 * @param hours
	 * @return
	 */
	private Date makeDate(String date, int hours) {
		Calendar c1 = getInstance();
		try {
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			fmt.setLenient(false);
			Date d1 = fmt.parse(date);
			c1.setTime(d1);
			c1.add(HOUR_OF_DAY, -hours);
		} catch (Exception e) {
		}
		return c1.getTime();
	}

	/**
	 * 
	 * @return
	 */
	public List<Transport> getAllSchedule() {
		return sf.findAll();
	}

	/**
	 * 
	 * @return
	 */
	public List<Transport> getAllSchedule(Richiesta richiesta) {
		Date inizio = richiesta.getOrarioInizio();
		Date fine = richiesta.getOrarioFine();
		if (inizio != null) {
			String strInizio = dateFormat.format(inizio);
			String strFine = dateFormat.format(fine);
			try {
				richiesta.setOrarioInizio(dateFormat.parse(strInizio));
				richiesta.setOrarioFine(addDays(dateFormat.parse(strFine), 1));
			} catch (ParseException e) {
				logger.error("errore nel format della data inizio", e);
			}
		}
		return sf.findAll(richiesta);
	}

	/**
     * 
     */
	public void setScheduleToday() {

		java.sql.Date td = new java.sql.Date(new Date().getTime());
		logger.info(td + "");
		try {
			Query query = em
					.createQuery("Select a FROM Transport a where a.timeRank like :timeRank");
			query.setParameter("timeRank", "%" + td + "%");
			@SuppressWarnings("unchecked")
			List<Transport> rs = query.getResultList();
			Date timeToRank = null;
			for (Transport transport : rs) {
				timeToRank = transport.getTimeRank();
				Calendar calendar = getInstance();
				calendar.setTime(timeToRank);
				schedula(calendar.get(HOUR_OF_DAY) + "", calendar.get(MINUTE)
						+ "");
			}
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		}
	}

	/**
     * 
     */
	@Schedule(second = "00", minute = "20", hour = "12")
	public void sendSalesScheduler() {
		this.setScheduleToday();
		try {
			sound(600, 100);
		} catch (LineUnavailableException ex) {
			logger.error("sales scheduler", ex);
		}
	}

	/**
	 * 
	 * @param hour
	 * @param minute
	 */
	public void schedula(String hour, String minute) {
		ScheduleExpression scheduleExpression = new ScheduleExpression();
		scheduleExpression.minute(minute).hour(hour);
		logger.info("Schedulato con " + hour + ":" + minute);
		this.timerService.createCalendarTimer(scheduleExpression);
		try {
			sound(1000, 100);
		} catch (LineUnavailableException ex) {
			logger.error("schedula", ex);
		}

	}

	/**
	 * 
	 * @param timer
	 */
	@Timeout
	public void doTask(Timer timer) {

		logger.info("Dentro il Task--Schedulato");
		// Chiamare il rank su vikor
		// Seleziono il primo schedule da eliminare
		Date dateMission = null;
		int idTimeSlot = 0;

		try {
			Query query = em
					.createQuery("SELECT a FROM Transport a ORDER BY a.alfacode");
			Transport transport = (Transport) query.getSingleResult();
			TimeSlot tSlot = transport.getTimeSlot();
			dateMission = addDays(transport.getDateMiss(), -1);
			idTimeSlot = tSlot.getIdTS();
			logger.info("Risultato del task: Data:" + dateMission
					+ "ID Time Slot: " + idTimeSlot);
			tsc.getRank(dateMission, idTimeSlot);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		try {

			sound(1500, 100);
		} catch (LineUnavailableException ex) {
			logger.error(ex.getMessage());
		}
	}

	public List<Transport> getSchedules(Integer idTimeslot) {
		List<Transport> result = new ArrayList<Transport>();
		List<Transport> schedulers = sf.findAll();
		for (Transport sched : schedulers)
			if (sched.getTimeSlot().getIdTS().equals(idTimeslot))
				result.add(sched);
		return result;
	}
}