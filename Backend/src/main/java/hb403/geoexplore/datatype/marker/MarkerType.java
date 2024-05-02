package hb403.geoexplore.datatype.marker;

public enum MarkerType {
	ALERT			("ALERT"),
	EVENT			("EVENT"),
	OBSERVATION		("OBSERVATION"),
	REPORT			("REPORT");

	public String value;
	private MarkerType(String v) { this.value = v; }

}
