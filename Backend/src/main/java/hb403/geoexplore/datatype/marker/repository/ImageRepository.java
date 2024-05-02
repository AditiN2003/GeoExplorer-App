package hb403.geoexplore.datatype.marker.repository;

import hb403.geoexplore.datatype.Image;
import hb403.geoexplore.datatype.marker.EventMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
