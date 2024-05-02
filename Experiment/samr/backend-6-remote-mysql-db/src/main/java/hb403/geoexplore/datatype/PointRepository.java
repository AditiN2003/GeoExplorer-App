package hb403.geoexplore.datatype;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vividsolutions.jts.geom.Geometry;


@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> {

	PointEntity findById(int id);
	void deleteById(int id);

	@Query(value = "SELECT p from PointEntity p WHERE within(p.point, :bounds) = true")
	public List<PointEntity> findWithin(@Param("bounds") Geometry bounds);

}
