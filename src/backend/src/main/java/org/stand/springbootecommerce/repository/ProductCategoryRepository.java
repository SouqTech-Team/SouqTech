package org.stand.springbootecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.stand.springbootecommerce.entity.user.ProductCategory;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    Optional<ProductCategory> findByName(String name);
}