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
package it.vige.greenarea.cl.control;

import static it.vige.greenarea.Conversioni.convertiVehiclesToVeicoli;
import static it.vige.greenarea.dto.Color.values;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sound.sampled.LineUnavailableException;
import javax.sql.DataSource;

import org.slf4j.Logger;

import it.vige.greenarea.Conversioni;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.RequestParameter;
import it.vige.greenarea.cl.library.entities.Mission;
import it.vige.greenarea.cl.library.entities.ShippingOrder;
import it.vige.greenarea.cl.library.entities.Transport;
import it.vige.greenarea.cl.library.entities.ValueMission;
import it.vige.greenarea.cl.library.entities.Vehicle;
import it.vige.greenarea.cl.scheduling.Tone;
import it.vige.greenarea.cl.sessions.ValueMissionFacade;
import it.vige.greenarea.dto.RichiestaVeicolo;
import it.vige.greenarea.dto.StatoVeicolo;
import it.vige.greenarea.dto.Veicolo;
import it.vige.greenarea.gtg.db.facades.MissionFacade;
import it.vige.greenarea.gtg.db.facades.TransportFacade;
import it.vige.greenarea.gtg.db.facades.TruckFacade;
import it.vige.greenarea.sgapl.sgot.facade.ShippingOrderFacade;

/**
 *
 * 
 */
@Stateless
public class UserControl {

	private Logger logger = getLogger(getClass());

	@PersistenceContext(unitName = "GTGwebPU")
	private EntityManager em;

	@Resource(lookup = "java:/jdbc/gtgdb")
	private DataSource ds;

	@EJB
	private MissionFacade mf;
	@EJB
	private ValueMissionFacade vmf;
	@EJB
	private TransportFacade sf;
	@EJB
	private ShippingOrderFacade sof;
	@EJB
	private TruckFacade tf;

	private void connectionClose(ResultSet rs, PreparedStatement ps, Connection conn) {

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

	public Mission addMission(Mission mis) {
		Mission m = mis;
		mis.getValuesMission();
		mf.create(m);

		try {
			Tone.sound(1600, 100);
		} catch (LineUnavailableException ex) {
			logger.error("add mission", ex);
		}

		try {

			Date da = mis.getStartTime();
			Query query = em.createQuery("Select a from Transport as a where a.dateMiss = :dateMiss");
			query.setParameter("dateMiss", da);
			@SuppressWarnings("unchecked")
			List<Transport> rs = (List<Transport>) query.getResultList();
			for (Transport transport : rs) {
				logger.info("Dentro Rs.Next");
				ShippingOrder shippingOrder = new ShippingOrder();
				sof.create(shippingOrder);
				transport.setShippingOrder(shippingOrder);
				sf.edit(transport);
			}
			logger.info("Data: " + da);

		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		}

		return m;

	}
	
	public ValueMission addValueMission(ValueMission vm) {
		vmf.create(vm);
		return vm;
	}

	public Request getInfoRequest(int idMission) {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Request req = new Request();
		try {

			con = ds.getConnection();

			ps = con.prepareStatement(
					"SELECT * " + " FROM Mission as a " + " LEFT JOIN VikorResult as c ON a.id = c.idMission "
							+ " JOIN ValueMission as b " + " JOIN ParameterGen as d " + " WHERE a.id= b.mission_id "
							+ " AND b.idParameter = d.idpg " + " AND a.id = ?");

			ps.setInt(1, idMission);
			rs = ps.executeQuery();

			int i = 0;
			while (rs.next()) {
				logger.info("Dentro primo ");
				if (i == 0) {
					req.setCarPlate(rs.getString("truck_PLATENUMBER"));
					req.setColor(values()[rs.getInt("color")]);
					req.setCompany(rs.getString("company"));
					req.setDateMiss(rs.getDate("startTime"));
					;
					req.setIdMission(rs.getInt("id"));
					req.setIdTimeSlot(rs.getInt("timeSlot_idTS"));
					req.setPrice(rs.getDouble("price"));
					req.setUserName(rs.getString("name"));
					logger.info("Dentro while: " + req.getCompany());
				}
				RequestParameter rp = new RequestParameter();
				rp.setIdParameter(rs.getInt("idParameter"));
				rp.setName(rs.getString("namePG"));
				rp.setValuePar(rs.getDouble("valuePar"));
				req.addRequestParameter(rp);
				i++;

			}

		} catch (Exception e) {
			logger.error("user control", e);
			return null;
		} finally {
			connectionClose(rs, ps, con);
		}

		return req;

	}

	/**
	 * <p>
	 * Method: findVehicles
	 * </p>
	 * <p>
	 * Description: Restituisce una lista di veicoli passandogli i campi di
	 * input
	 * </p>
	 * 
	 * @param Veicolo
	 *            veicolo
	 * @return List<Veicolo>
	 */
	public List<Veicolo> findVehicles(RichiestaVeicolo veicolo) {

		boolean where = false;
		String qu = "SELECT c FROM Vehicle c";
		if (veicolo.getAutista() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.autista = :autista";
			} else
				qu = qu + " and c.autista = :autista";
		}
		if (veicolo.getOperatoreLogistico() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.operatoreLogistico = :operatoreLogistico";
			} else
				qu = qu + " and c.operatoreLogistico = :operatoreLogistico";
		}
		if (veicolo.getSocietaDiTrasporto() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.societaDiTrasporto = :societaDiTrasporto";
			} else
				qu = qu + " and c.societaDiTrasporto = :societaDiTrasporto";
		}
		if (!where) {
			qu = qu + " where";
			where = true;
			qu = qu + " c.vin is not null";
		} else
			qu = qu + " and c.vin is not null";
		Query query = em.createQuery(qu);
		if (veicolo.getAutista() != null)
			query.setParameter("autista", veicolo.getAutista().getId());
		if (veicolo.getOperatoreLogistico() != null)
			query.setParameter("operatoreLogistico", veicolo.getOperatoreLogistico().getId());
		if (veicolo.getSocietaDiTrasporto() != null)
			query.setParameter("societaDiTrasporto", veicolo.getSocietaDiTrasporto().getId());
		@SuppressWarnings("unchecked")
		List<Vehicle> vehicleList = query.getResultList();
		List<Veicolo> veicoli = convertiVehiclesToVeicoli(vehicleList);
		return veicoli;
	}

	/**
	 * <p>
	 * Method: findVehicles
	 * </p>
	 * <p>
	 * Description: Restituisce una lista di veicoli passandogli i campi di
	 * input
	 * </p>
	 * 
	 * @param Veicolo
	 *            veicolo
	 * @return List<Veicolo>
	 */
	public List<Veicolo> findVehicles(Veicolo veicolo) {

		boolean where = false;
		String qu = "SELECT c FROM Vehicle c";
		if (veicolo.getAutista() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.autista = :autista";
			} else
				qu = qu + " and c.autista = :autista";
		}
		if (veicolo.getOperatoreLogistico() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.operatoreLogistico = :operatoreLogistico";
			} else
				qu = qu + " and c.operatoreLogistico = :operatoreLogistico";
		}
		if (veicolo.getSocietaDiTrasporto() != null) {
			if (!where) {
				qu = qu + " where";
				where = true;
				qu = qu + " c.societaDiTrasporto = :societaDiTrasporto";
			} else
				qu = qu + " and c.societaDiTrasporto = :societaDiTrasporto";
		}
		Query query = em.createQuery(qu);
		if (veicolo.getAutista() != null)
			query.setParameter("autista", veicolo.getAutista().getId());
		if (veicolo.getOperatoreLogistico() != null)
			query.setParameter("operatoreLogistico", veicolo.getOperatoreLogistico().getId());
		if (veicolo.getSocietaDiTrasporto() != null)
			query.setParameter("societaDiTrasporto", veicolo.getSocietaDiTrasporto().getId());
		@SuppressWarnings("unchecked")
		List<Vehicle> vehicleList = query.getResultList();
		List<Veicolo> veicoli = convertiVehiclesToVeicoli(vehicleList);
		return veicoli;
	}

	public Veicolo aggiornaStatoVeicolo(Veicolo veicolo) {
		Vehicle vehicle = tf.find(veicolo.getTarga());
		vehicle.setState(StatoVeicolo.valueOf(veicolo.getStato()));
		return Conversioni.convertiVehicleToVeicolo(em.merge(vehicle));
	}
}
