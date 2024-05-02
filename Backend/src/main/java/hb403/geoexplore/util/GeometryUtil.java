package hb403.geoexplore.util;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.util.GeometricShapeFactory;


public final class GeometryUtil {

	public static double EARTH_RADIUS_MILES = 3958.8;

	public static final int DEFAULT_CIRCLE_KEYPOINTS = 32;
	
	public static final WKTReader wkt_reader = new WKTReader();
	public static final GeometryFactory geom_factory = new GeometryFactory();
	public static final GeometricShapeFactory shape_factory = new GeometricShapeFactory(geom_factory);

	/** Converts a 'Well Known Text' string encoding geometry into a java object */
	public static Geometry getGeometry(String wkt) throws ParseException {
		return wkt_reader.read(wkt);
	}
	/** */
	public static Point makePoint(double x, double y) {
		return makePoint(new Coordinate(x, y));
	}
	/**  */
	public static Point makePoint(Coordinate c) {
		return geom_factory.createPoint(c);
	}
	/** */
	public static Polygon makeCircle(double x, double y, double rad) {
		return makeCircle(x, y, rad, DEFAULT_CIRCLE_KEYPOINTS);
	}
	/** */
	public static Polygon makeCircle(double x, double y, double rad, int keypoints) {
		return makeCircle(new Coordinate(x, y), rad, keypoints);
	}
	/** */
	public static Polygon makeCircle(Coordinate c, double rad, int keypoints) {
		shape_factory.setNumPoints(keypoints);
		shape_factory.setCentre(c);
		shape_factory.setSize(rad * 2);
		return shape_factory.createCircle();
	}
	/** */
	public static Polygon makeRectangle(double x1, double y1, double x2, double y2) {
		final double
			xmin = Math.min(x1, x2),
			xmax = Math.max(x1, x2),
			ymin = Math.min(y1, y2),
			ymax = Math.max(y1, y2);

		return geom_factory.createPolygon(
			new Coordinate[]{
				new Coordinate(xmin, ymin),
				new Coordinate(xmin, ymax),
				new Coordinate(xmax, ymax),
				new Coordinate(xmax, ymin),
				new Coordinate(xmin, ymin)
			}
		);
	}
	public static Polygon makeLatLonRectFromRad(double theta1, double theta2, double phi1, double phi2) {
		return makeRectangle(
			phiToLatInDeg(phi1),
			thetaToLonInDeg(theta1),
			phiToLatInDeg(phi2),
			thetaToLonInDeg(theta2)
		);
	}


	public static double latToPhiInDeg(double lat_deg) {
		return 90.0 - lat_deg;
	}
	public static double latToPhiInRad(double lat_deg) {
		return Math.toRadians(latToPhiInDeg(lat_deg));
	}
	public static double lonToThetaInDeg(double lon_deg) {
		return lon_deg;
	}
	public static double lonToThetaInRad(double lon_deg) {
		return Math.toRadians(lonToThetaInDeg(lon_deg));
	}

	public static double thetaToLonInDeg(double theta_rad) {
		return Math.toDegrees(theta_rad);
	}
	public static double phiToLatInDeg(double phi_rad) {
		return 90.0 - Math.toDegrees(phi_rad);
	}


	/** Compute the dot product of the unit vectors formed using the provided locations in spherical coordinates -- units are radians */
	public static double arcdot(double t1, double p1, double t2, double p2) {

		final double
			sin_theta1 = Math.sin(t1),
			sin_theta2 = Math.sin(t2),
			sin_phi1 = Math.sin(p1),
			sin_phi2 = Math.sin(p2),
			cos_theta1 = Math.cos(t1),
			cos_theta2 = Math.cos(t2),
			cos_phi1 = Math.cos(p1),
			cos_phi2 = Math.cos(p2);

		return (
			((cos_theta1 * cos_theta2) + (sin_theta1 * sin_theta2)) * (sin_phi1 * sin_phi2) +
			(cos_phi1 * cos_phi2)
		);

	}
	/** Compute the arc-angle between two sets of spherical coordinates -- units are radians */
	public static double arcangle(double t1, double p1, double t2, double p2) {
		return Math.acos(arcdot(t1, p1, t2, p2));
	}

	public static double arcdotDeg(double t1, double p1, double t2, double p2) {
		return arcdot(
			Math.toRadians(t1),
			Math.toRadians(p1),
			Math.toRadians(t2),
			Math.toRadians(p2)
		);
	}
	public static double arcangleDegInRad(double t1, double p1, double t2, double p2) {
		return Math.acos(arcdotDeg(t1, p1, t2, p2));
	}
	public static double arcangleDegInDeg(double t1, double p1, double t2, double p2) {
		return Math.toDegrees(arcangleDegInRad(t1, p1, t2, p2));
	}

	public static double arcdotGlobal(double lat1, double lon1, double lat2, double lon2) {
		return arcdot(
			lonToThetaInRad(lon1),
			latToPhiInRad(lat1),
			lonToThetaInRad(lon2),
			latToPhiInRad(lat2)
		);
	}
	/** IN MILES!!! */
	public static double arcdistanceGlobal(double lat1, double lon1, double lat2, double lon2) {
		return EARTH_RADIUS_MILES * Math.acos(arcdotGlobal(lat1, lon1, lat2, lon2));
	}


}
