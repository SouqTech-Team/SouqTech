package org.stand.springbootecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.repository.ProductCategoryRepository;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.service.ProductService;

import java.util.*;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public Page<Product> getProducts(String query, Integer pageNumber, Integer pageSize) {
        return query == null ? productRepository.findAll(PageRequest.of(pageNumber, pageSize))
                : searchProducts(query, pageNumber, pageSize);
    }

    @Override
    public List<Product> getProducts(String query) {
        return query == null ? productRepository.findAll() : searchProducts(query);
    }

    @Override
    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategoryId(
                productCategoryRepository
                        .findByName(categoryName)
                        .orElseThrow(() -> new NoSuchElementException(
                                "ProductCategory with name='%s' not found".formatted(categoryName)))
                        .getId());
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository
                .findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new NoSuchElementException("Product with id='%d' not found".formatted(id)));
    }

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(Objects.requireNonNull(product));
    }

    @Override
    public Page<Product> searchProducts(String query, Integer pageNumber, Integer pageSize) {
        return productRepository.findByNameContainingIgnoreCase(query, PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
}