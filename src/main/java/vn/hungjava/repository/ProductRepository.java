package vn.hungjava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hungjava.model.ProductEntity;
import vn.hungjava.model.UserEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query(value = "select u from ProductEntity u Where u.status='ACTIVE' " +
            "and (lower(u.name) like :keyword " +
            "or lower(u.description) like :keyword )")
    Page<ProductEntity> searchByKeyword(String keyword, Pageable pageable);
    ProductEntity findByName(String name);
}
