package org.stand.springbootecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.stand.springbootecommerce.entity.user.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Override
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByNameContainingIgnoreCase(String query, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String query);

    Page<Product> findByCategoryNameContainingIgnoreCase(String query, Pageable pageable);

    List<Product> findByCategoryNameContainingIgnoreCase(String query);

}