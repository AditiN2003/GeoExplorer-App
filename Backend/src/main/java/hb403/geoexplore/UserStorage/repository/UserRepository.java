package hb403.geoexplore.UserStorage.repository;

import hb403.geoexplore.UserStorage.entity.User;
import jakarta.persistence.NamedQuery;

import java.util.*;

import org.locationtech.jts.geom.Geometry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // public List<User> findByIdInAndIsAdmin(Collection<Long> subset_ids, Boolean is_admin);
    @Query(value = "SELECT u from User u WHERE u.role = 'ADMIN' AND u.id IN :ids")
    public List<User> findSubsetAdminUsers(@Param("ids") Collection<Long> user_ids_subset);

    @Query(value = "SELECT u from User u WHERE within(u.location, :bounds) = true")
    public List<User> findUsersWithin(@Param("bounds") Geometry bounds);

    @Query(value = "SELECT u from User u WHERE u.id IN :ids AND within(u.location, :bounds) = true")
    public List<User> findSubsetUsersWithin(@Param("ids") Collection<Long> user_ids_subset, @Param("bounds") Geometry bounds);

    @Query(value = "SELECT u from User u WHERE u.role = 'ADMIN' AND u.id IN :ids AND within(u.location, :bounds) = true")
    public List<User> findSubsetAdminUsersWithin(@Param("ids") Collection<Long> user_ids_subset, @Param("bounds") Geometry bounds);


}
