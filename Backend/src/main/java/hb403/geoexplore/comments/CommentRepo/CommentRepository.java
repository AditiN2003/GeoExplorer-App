package hb403.geoexplore.comments.CommentRepo;

import hb403.geoexplore.comments.Entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {

}
