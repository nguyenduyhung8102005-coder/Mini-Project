package vn.hungjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hungjava.model.CartEntity;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserId(Long userId);
}
