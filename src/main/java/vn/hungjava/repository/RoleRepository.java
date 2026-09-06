package vn.hungjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hungjava.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
