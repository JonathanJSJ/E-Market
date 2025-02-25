package jala.university.Qatu.repository;

import jala.university.Qatu.domain.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Category findByName(String name);
    boolean existsByName(String name);
}
