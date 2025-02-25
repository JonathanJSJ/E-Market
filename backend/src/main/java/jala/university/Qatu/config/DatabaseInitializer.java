package jala.university.Qatu.config;

import jala.university.Qatu.domain.product.Category;
import jala.university.Qatu.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
        return args -> {
            List<String> categoryNames = Arrays.asList("Cellphones", "Games", "School", "Fashion", "Accessories", "Sports", "Pet", "Furniture", "Household", "Vehicles");
            for (String name : categoryNames) {
                if (!categoryRepository.existsByName(name)) {
                    Category category = new Category(UUID.randomUUID(), name, null);
                    categoryRepository.save(category);
                }
            }
        };
    }
}