package org.stand.springbootecommerce.service;

import org.stand.springbootecommerce.entity.user.ProductCategory;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategory> getProductCategories();
    ProductCategory getProductCategoryById(Long id);
    ProductCategory addProductCategory(ProductCategory productCategory);
}