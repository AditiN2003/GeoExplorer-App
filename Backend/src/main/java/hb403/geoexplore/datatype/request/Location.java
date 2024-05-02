package hb403.geoexplore.datatype.request;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Location {
	
	public Double
		latitude,
		longitude;

	@JsonIgnore
	public boolean isValid() {
		return (
			this.latitude != null &&
			this.longitude != null
		);
	}
	@JsonIgnore
	public boolean isInvalid() {
		return (
			this.latitude == null ||
			this.longitude == null
		);
	}

}
