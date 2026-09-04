package vn.hungjava.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hungjava.model.CartEntity;
import vn.hungjava.model.CartItemEntity;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, Long productId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select item
        from CartItemEntity item
        join fetch item.product
        where item.id = :cartItemId
          and item.cart.user.id = :userId
        """)
    Optional<CartItemEntity> findOwnedItemForUpdate(
            @Param("cartItemId")
            Long cartItemId,

            @Param("userId")
            Long userId
    );
}
