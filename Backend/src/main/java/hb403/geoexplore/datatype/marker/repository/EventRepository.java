package hb403.geoexplore.datatype.marker.repository;

import hb403.geoexplore.datatype.marker.EventMarker;

import java.util.*;

import org.locationtech.jts.geom.Geometry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface EventRepository extends JpaRepository<EventMarker, Long> {
	
	EventMarker findById(int id);
	void deleteById(int id);

	@Query(value = "SELECT m from EventMarker m WHERE within(m.location, :bounds) = true")
	public Set<EventMarker> findSetWithin(@Param("bounds") Geometry bounds);

	@Query(value = "SELECT m from EventMarker m WHERE within(m.location, :bounds) = true")
	public List<EventMarker> findListWithin(@Param("bounds") Geometry bounds);


}
