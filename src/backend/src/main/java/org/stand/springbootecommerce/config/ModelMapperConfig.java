package org.stand.springbootecommerce.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stand.springbootecommerce.dto.request.ProductRequest;
import org.stand.springbootecommerce.dto.response.ProductResponse;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.service.ProductCategoryService;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {
        private final ProductCategoryService productCategoryService;

        @Bean
        public ModelMapper modelMapper() {
                ModelMapper modelMapper = new ModelMapper();

                Converter<Long, ProductCategory> categoryIdToCategory = context -> productCategoryService
                                .getProductCategoryById(context.getSource());

                Converter<ProductCategory, Long> categoryToCategoryId = context -> context.getSource().getId();

                /*
                 * TypeMap
                 */
                TypeMap<ProductRequest, Product> productPostRequestToProduct = modelMapper.createTypeMap(
                                ProductRequest.class,
                                Product.class);

                productPostRequestToProduct.addMappings(
                                mapper -> mapper.using(categoryIdToCategory).map(ProductRequest::getCategory,
                                                Product::setCategory));

                TypeMap<Product, ProductResponse> productToProductResponse = modelMapper.createTypeMap(Product.class,
                                ProductResponse.class);

                productToProductResponse.addMappings(
                                mapper -> mapper.using(categoryToCategoryId).map(Product::getCategory,
                                                ProductResponse::setCategoryId));

                productToProductResponse.addMappings(
                                mapper -> mapper.using(categoryToCategoryId).map(Product::getCategory,
                                                ProductResponse::setCategoryId));

                return modelMapper;
        }
}