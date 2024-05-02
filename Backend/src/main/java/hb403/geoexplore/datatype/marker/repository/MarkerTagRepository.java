package hb403.geoexplore.datatype.marker.repository;

import hb403.geoexplore.datatype.MarkerTag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MarkerTagRepository extends JpaRepository<MarkerTag, Long> {

	Optional<MarkerTag> findByName(String name);

}
