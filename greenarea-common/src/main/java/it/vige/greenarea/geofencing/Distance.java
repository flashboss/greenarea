package it.vige.greenarea.geofencing;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

public class Distance {
	private static Logger logger = getLogger(Distance.class);

	public static double calcolateDistance(String src, String dst) {
		if (src == null)
			return 0.0;
		double lon1 = new Double(src.split(",")[1]);
		double lon2 = new Double(dst.split(",")[1]);
		double lat1 = new Double(src.split(",")[0]);
		double lat2 = new Double(dst.split(",")[0]);
		double theta = lon1 - lon2;
		double dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2))
				+ cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta));
		dist = acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		logger.debug("calcolateDistance dist = " + dist);
		if (new Double(dist).isNaN())
			return 0.0;
		else
			return dist;
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / PI);
	}

	private static double deg2rad(double deg) {
		return (deg * PI / 180.0);
	}

}
