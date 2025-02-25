package jala.university.Qatu.repository;

import jala.university.Qatu.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Page<Product> findByUserId(UUID userId, Pageable pageable);
    boolean existsByNameAndUserId(String name, UUID userId);

    @Query("SELECT p FROM products p JOIN p.statistic s ORDER BY s.visits DESC")
    List<Product> findTopProductsByVisitCount(Pageable pageable);

    @Query("SELECT p FROM products p WHERE p.category.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);
}