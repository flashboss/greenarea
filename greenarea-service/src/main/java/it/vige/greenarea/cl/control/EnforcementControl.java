/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.vige.greenarea.cl.control;

import static it.vige.greenarea.dto.Color.values;
import static org.slf4j.LoggerFactory.getLogger;
import it.vige.greenarea.cl.bean.Request;
import it.vige.greenarea.cl.bean.RequestParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.slf4j.Logger;

/**
 *
 * 
 */
@Stateless
public class EnforcementControl {

	private Logger logger = getLogger(getClass());

	@Resource(lookup = "java:/jdbc/gtgdb")
	private DataSource ds;

	private void connectionClose(ResultSet rs, PreparedStatement ps,
			Connection conn) {

		try {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		}
	}

	public Request getInfoRequest(int idTimeSlot, String idVehicle) {

		Request req = new Request();
		java.sql.Date dtoday = new java.sql.Date(new Date().getTime());

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			logger.info("Sono un vigile" + idTimeSlot + "   " + idVehicle
					+ "   " + dtoday);

			con = ds.getConnection();
			ps = con.prepareStatement("SELECT a.startTime,a.truck_PLATENUMBER,a.timeSlot_idTS,a.id,a.company,a.name,a.resvikor,c.color,c.price,b.*,d.* "
					+ "FROM Mission as a JOIN ValueMission as b JOIN VikorResult as c JOIN ParameterGen as d WHERE a.id = b.mission_id "
					+ "AND a.id = c.idMission AND b.idParameter = d.idpg AND a.timeSlot_idTS = ? AND a.truck_PLATENUMBER = ? AND a.startTime = ? "
					+ "ORDER by a.id, b.idParameter ");
			ps.setDate(3, dtoday);
			ps.setInt(1, idTimeSlot);
			ps.setString(2, idVehicle);
			rs = ps.executeQuery();
			int i = 0;

			while (rs.next()) {
				if (i == 0) {
					logger.info("Dentro While");
					req.setCarPlate(rs.getString("idVehicle"));
					req.setCompany(rs.getString("company"));
					req.setDateMiss(rs.getDate("startTime"));
					req.setColor(values()[rs.getInt("color")]);
					req.setIdMission(rs.getInt("id"));
					req.setIdTimeSlot(rs.getInt("idTimeSlot"));
					req.setPrice(rs.getDouble("price"));
					req.setUserName(rs.getString("name"));
				}
				RequestParameter rp = new RequestParameter();
				rp.setIdParameter(rs.getInt("idParameter"));
				rp.setName(rs.getString("namePG"));
				rp.setValuePar(rs.getDouble("valuePar"));
				req.addRequestParameter(rp);
				i++;
			}

		} catch (Exception e) {
			logger.error("accessi fasceorarie", e);
		} finally {
			connectionClose(rs, ps, con);
		}

		return req;
	}
}
