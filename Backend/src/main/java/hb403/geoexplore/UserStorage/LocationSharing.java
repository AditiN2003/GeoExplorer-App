package hb403.geoexplore.UserStorage;


public enum LocationSharing {
	DISABLED	(0),	// no sharing, will not connect to websocket
	EMERGENCY	(1),	// only shares location in case of emergency
	GROUP		(2),	// shares location in groups that have this feature enabled
	PUBLIC		(3);	// location is publicly availabe for others to view

	public int value;
	private LocationSharing(int v) { this.value = v; }
};
