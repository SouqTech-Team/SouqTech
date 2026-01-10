package org.stand.springbootecommerce.service;

import org.springframework.data.domain.Page;
import org.stand.springbootecommerce.entity.user.Product;

import java.util.List;

public interface ProductService {
    Page<Product> getProducts(String query, Integer page, Integer size);
    List<Product> getProducts(String query);
    List<Product> getProductsByCategoryName(String categoryName);
    List<Product> getProductsByCategoryId(Long categoryId);
    Product getProductById(Long id);
    Product addProduct(Product product);
    Page<Product> searchProducts(String query, Integer page, Integer size);
    List<Product> searchProducts(String query);

}