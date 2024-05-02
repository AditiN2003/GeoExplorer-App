package hb403.geoexplore.datatype.marker.repository;

import hb403.geoexplore.datatype.marker.ReportMarker;

import java.util.*;

import org.locationtech.jts.geom.Geometry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportRepository extends JpaRepository<ReportMarker, Long> {
	
	ReportMarker findById(int id);
	void deleteById(int id);

	@Query(value = "SELECT m from ReportMarker m WHERE within(m.location, :bounds) = true")
	public Set<ReportMarker> findSetWithin(@Param("bounds") Geometry bounds);

	@Query(value = "SELECT m from ReportMarker m WHERE within(m.location, :bounds) = true")
	public List<ReportMarker> findListWithin(@Param("bounds") Geometry bounds);


}
