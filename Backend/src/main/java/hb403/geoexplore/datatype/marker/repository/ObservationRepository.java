package hb403.geoexplore.datatype.marker.repository;

import hb403.geoexplore.datatype.marker.ObservationMarker;

import java.util.*;

import org.locationtech.jts.geom.Geometry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ObservationRepository extends JpaRepository<ObservationMarker, Long> {
	
	ObservationMarker findById(int id);
	void deleteById(int id);

	@Query(value = "SELECT m from ObservationMarker m WHERE within(m.location, :bounds) = true")
	public Set<ObservationMarker> findSetWithin(@Param("bounds") Geometry bounds);

	@Query(value = "SELECT m from ObservationMarker m WHERE within(m.location, :bounds) = true")
	public List<ObservationMarker> findListWithin(@Param("bounds") Geometry bounds);


}
