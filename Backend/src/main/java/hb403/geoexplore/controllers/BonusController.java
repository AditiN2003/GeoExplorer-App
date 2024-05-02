package hb403.geoexplore.controllers;

import hb403.geoexplore.datatype.marker.*;
import hb403.geoexplore.datatype.marker.repository.*;
import hb403.geoexplore.util.GeometryUtil;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class BonusController {
	
	@Autowired
	private AlertRepository alert_repo;
	@Autowired
	private EventRepository event_repo;
	@Autowired
	private ObservationRepository obs_repo;
	@Autowired
	private ReportRepository report_repo;


	private MarkerBase getMarker(String type, Long id) {
		if(type == null || id == null || id < 0) return null;
		try {
			switch(MarkerType.valueOf(type.toUpperCase())) {
				case ALERT: return this.alert_repo.findById(id).get();
				case EVENT: return this.event_repo.findById(id).get();
				case OBSERVATION: return this.obs_repo.findById(id).get();
				case REPORT: return this.report_repo.findById(id).get();
				default:
			}
		} catch(Exception e) {

		}
		return null;
	}

	@Operation(summary = "Get the arc-distance between any two markers. Marker typenames are internally converted to uppercase, therefore casing does not matter (select from { \"alert\", \"event\", \"observation\", \"report\"}).")
	@GetMapping(path = "geomap/{markerT1}/{id1}/distanceto/{markerT2}/{id2}")
	public @ResponseBody Double getMarkerArcDistance(
		@PathVariable String markerT1,
		@PathVariable Long id1,
		@PathVariable String markerT2,
		@PathVariable Long id2
	) {
		try {
			final MarkerBase
				m1 = this.getMarker(markerT1, id1),
				m2 = this.getMarker(markerT2, id2);

			if(m1 != null && m2 != null) {
				m1.enforceLocationTable();
				m2.enforceLocationTable();
				return GeometryUtil.arcdistanceGlobal(
					m1.getIo_latitude(),
					m1.getIo_longitude(),
					m2.getIo_latitude(),
					m2.getIo_longitude()
				);
			}
		} catch(Exception e) {

		}
		return null;
	}

}
