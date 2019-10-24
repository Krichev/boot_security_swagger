package touchsoft.repository;

import javax.transaction.Transactional;

import touchsoft.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import touchsoft.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    User findByUsername(String username);
    ArrayList<User> findAllByRoles(List<Role> roles);

    User findByRolesAndUsername(List<Role> role, String Username);

    @Transactional
    void deleteByUsername(String username);

}
