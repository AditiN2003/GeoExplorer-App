package hb403.geoexplore.datatype.request;

import hb403.geoexplore.util.GeometryUtil;

import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class LocationProximity {
	
	public Double
		latitude,
		longitude,
		range;		// miles!


	@JsonIgnore
	public boolean isValid() {
		return (
			this.range != null &&
			this.latitude != null &&
			this.longitude != null
		);
	}
	@JsonIgnore
	public boolean isInvalid() {
		return (
			this.range == null ||
			this.latitude == null ||
			this.longitude == null
		);
	}

	@JsonIgnore
	public Polygon[] getMinMaxSearchBounds() {

		if(this.range == null || this.latitude == null || this.longitude == null) return null;

		final double
			PI2 = Math.PI * 2,
			arc_rad = this.range / GeometryUtil.EARTH_RADIUS_MILES,
			src_theta = GeometryUtil.lonToThetaInRad(this.longitude),
			src_phi = GeometryUtil.latToPhiInRad(this.latitude);
		double
			min_theta = src_theta - arc_rad,
			max_theta = src_theta + arc_rad,
			min_phi = src_phi - arc_rad,
			max_phi = src_phi + arc_rad;
		final boolean
			min_phi_wrap = min_phi < 0,
			max_phi_wrap = max_phi > Math.PI,
			min_theta_wrap = min_theta < -Math.PI,
			max_theta_wrap = max_theta > Math.PI;	// longitude goes from -180 to 180 and we don't remap it to a positive range so these are the bounds

		if(min_phi_wrap || max_phi_wrap) {	// range extends beyond poles --> search through all 360 degrees of theta is needed (since MySQL is dumb)
			min_theta = 0;
			max_theta = PI2;
			if(min_phi_wrap) min_phi = 0;
			if(max_phi_wrap) max_phi = Math.PI;		// negate wrap-around for 2D search
		} else if(min_theta_wrap && max_theta_wrap) {	// just clamp each so we search the entire range
			min_theta = -Math.PI;
			max_theta = Math.PI;
		}else if(min_theta_wrap) {	// bounds cross theta=0=2pi boundry --> need 2 search boxes
			return new Polygon[]{
				GeometryUtil.makeLatLonRectFromRad(min_theta + PI2, Math.PI, min_phi, max_phi),
				GeometryUtil.makeLatLonRectFromRad(-Math.PI, max_theta, min_phi, max_phi)
			};
		} else if(max_theta_wrap) {
			return new Polygon[]{
				GeometryUtil.makeLatLonRectFromRad(-Math.PI, max_theta - PI2, min_phi, max_phi),
				GeometryUtil.makeLatLonRectFromRad(min_theta, Math.PI, min_phi, max_phi)
			};
		}

		return new Polygon[]{
			GeometryUtil.makeLatLonRectFromRad(min_theta, max_theta, min_phi, max_phi)
		};

	}


}
