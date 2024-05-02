package hb403.geoexplore.UserStorage.repository;

import hb403.geoexplore.UserStorage.entity.ReportedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportedUserRepository extends JpaRepository<ReportedUser, Long> {
}
