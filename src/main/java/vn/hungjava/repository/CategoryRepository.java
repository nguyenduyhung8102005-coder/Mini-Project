package vn.hungjava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hungjava.model.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query(value = "select u from CategoryEntity u Where u.status='ACTIVE' " +
            "and (lower(u.name) like :keyword " +
            "or lower(u.description) like :keyword )")
    Page<CategoryEntity> findByKeyWord(String keyword, Pageable pageable);

    CategoryEntity findByName(String name);
}
