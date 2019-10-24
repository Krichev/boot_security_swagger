//package murraco.repository;
//
//import murraco.boundary.Connection;
//import murraco.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import javax.transaction.Transactional;
//
//public interface ConnectionRepository extends JpaRepository<Connection, Integer> {
//
//    boolean existsByUsername(String username);
//
//    User findByUsername(String username);
//
//    User findByRolesAndUsername(String role, String Username);
//
//    @Transactional
//    void deleteByUsername(String username);
//
//}
