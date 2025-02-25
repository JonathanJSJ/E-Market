package jala.university.Qatu.repository;

import jala.university.Qatu.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findAllByUserEmail(String email, Pageable pageable);

    @Query("""
       SELECT COUNT(o) > 0
       FROM order o
       JOIN o.items i
       WHERE o.user.id = :userId
       AND i.product.id = :productId
       """)
    boolean existsByUserEmailAndProductId(UUID userId, UUID productId);
}
