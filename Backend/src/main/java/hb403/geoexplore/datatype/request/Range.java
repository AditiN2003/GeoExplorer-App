package hb403.geoexplore.datatype.request;

import hb403.geoexplore.util.GeometryUtil;

import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Range {
	
	public Double
		min_latitude,
		min_longitude,
		max_latitude,
		max_longitude;


	@JsonIgnore
	public boolean isValid() {
		return (
			this.min_latitude != null &&
			this.min_longitude != null &&
			this.max_latitude != null &&
			this.max_longitude != null
		);
	}
	@JsonIgnore
	public boolean isInvalid() {
		return (
			this.min_latitude == null ||
			this.min_longitude == null ||
			this.max_latitude == null ||
			this.max_longitude == null
		);
	}
	@JsonIgnore
	public Polygon getRect() {
		if(this.isInvalid()) return null;
		return GeometryUtil.makeRectangle(
			this.min_latitude,
			this.min_longitude,
			this.max_latitude,
			this.max_longitude
		);
	}


}
