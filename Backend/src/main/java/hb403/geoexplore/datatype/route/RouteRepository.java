package hb403.geoexplore.datatype.route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
	
	// TODO: query for routes contained within BB, and/or intersecting with BB

}
