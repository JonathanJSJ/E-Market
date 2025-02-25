package jala.university.Qatu.specification;

import jakarta.persistence.criteria.*;
import jala.university.Qatu.domain.product.Product;
import jala.university.Qatu.domain.rating.Rating;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<Product> hasName(String name) {
        return ((root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    public static Specification<Product> hasPriceBetween(Double min, Double max) {
        return ((root, query, criteriaBuilder) ->
                min == null  || max == null ? null : criteriaBuilder.between(root.get("price"), min, max));
    }

    public static Specification<Product> hasMinPrice(Double price) {
        return ((root, query, criteriaBuilder) ->
                price == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
    }

    public static Specification<Product> hasMaxPrice(Double price) {
        return ((root, query, criteriaBuilder) ->
                price == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
    }

    public static Specification<Product> hasCategory(String category) {
        return ((root, query, criteriaBuilder) ->
                category == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.join("category", JoinType.LEFT).get("name")), "%" + category.toLowerCase() + "%"));
    }

    public static Specification<Product> hasMinRate(Double minRate) {
        return (root, query, builder) -> {
            if (minRate == null || minRate < 0) { // Ajuste para permitir minRate igual a 0
                return null;
            }
            assert query != null;
            Subquery<Double> subquery = query.subquery(Double.class);
            Root<Rating> ratingRoot = subquery.from(Rating.class);
            subquery.select(builder.avg(ratingRoot.get("rate")))
                    .where(builder.equal(ratingRoot.get("product"), root));

            // Use COALESCE para tratar produtos sem avaliações
            return builder.greaterThanOrEqualTo(builder.coalesce(subquery, 0.0), minRate);
        };
    }

    public static Specification<Product> hasMaxRate(Double maxRate) {
        return (root, query, builder) -> {
            if (maxRate == null) {
                return null;
            }
            Subquery<Double> subquery = query.subquery(Double.class);
            Root<Rating> ratingRoot = subquery.from(Rating.class);
            subquery.select(builder.avg(ratingRoot.get("rate")))
                    .where(builder.equal(ratingRoot.get("product"), root));

            return builder.lessThanOrEqualTo(subquery, maxRate);
        };
    }
}
