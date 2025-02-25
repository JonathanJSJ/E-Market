package jala.university.Qatu.repository;

import jala.university.Qatu.domain.rating.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Page<Rating> findByProductId(UUID productId, Pageable pageable);

    List<Rating> findAllByProductId(UUID id);

    @Query("SELECT r FROM rating r WHERE r.product.user.id = :userId")
    List<Rating> findAllByProductCreatorUserId(UUID userId);

    @Query("""
       SELECT COUNT(r) > 0
       FROM rating r
       WHERE r.user.id = :userId
       AND r.product.id = :productId
    """)
    boolean alreadyCommentedByUser(UUID userId, UUID productId);

}
