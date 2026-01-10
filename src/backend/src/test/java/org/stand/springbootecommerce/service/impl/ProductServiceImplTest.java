package org.stand.springbootecommerce.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.repository.ProductCategoryRepository;
import org.stand.springbootecommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de ProductServiceImpl")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductCategory category;

    @BeforeEach
    void setUp() {
        category = ProductCategory.builder()
                .id(1L)
                .name("Electronics")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .category(category)
                .quantity(10)
                .build();
    }

    @Test
    @DisplayName("Doit trouver un produit par son ID")
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getProductById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Doit lancer une exception si le produit n'existe pas")
    void getProductById_NotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Product with id='99' not found");
    }

    @Test
    @DisplayName("Doit ajouter un produit avec succès")
    void addProduct_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.addProduct(product);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Doit lister les produits par nom de catégorie")
    void getProductsByCategoryName_Success() {
        when(productCategoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(1L)).thenReturn(Collections.singletonList(product));

        var results = productService.getProductsByCategoryName("Electronics");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Doit rechercher des produits par nom")
    void searchProducts_Success() {
        when(productRepository.findByNameContainingIgnoreCase("Lap")).thenReturn(Collections.singletonList(product));

        var results = productService.searchProducts("Lap");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Doit retourner tous les produits paginés (query null)")
    void getProducts_All_Paged() {
        // Arrange
        org.springframework.data.domain.Page<Product> productPage = new org.springframework.data.domain.PageImpl<>(
                java.util.Collections.singletonList(product));
        when(productRepository.findAll(any(org.springframework.data.domain.PageRequest.class))).thenReturn(productPage);

        // Act
        org.springframework.data.domain.Page<Product> result = productService.getProducts(null, 0, 10);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(productRepository).findAll(any(org.springframework.data.domain.PageRequest.class));
    }

    @Test
    @DisplayName("Doit retourner les produits par ID de catégorie")
    void getProductsByCategoryId_Success() {
        // Arrange
        when(productRepository.findByCategoryId(1L)).thenReturn(Collections.singletonList(product));

        // Act
        java.util.List<Product> result = productService.getProductsByCategoryId(1L);

        // Assert
        assertThat(result).hasSize(1);
        verify(productRepository).findByCategoryId(1L);
    }

    @Test
    @DisplayName("Doit lancer une exception si la catégorie n'existe pas lors de la recherche par nom")
    void getProductsByCategoryName_CategoryNotFound() {
        // Arrange
        when(productCategoryRepository.findByName("NonExistent")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> productService.getProductsByCategoryName("NonExistent"))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessageContaining("ProductCategory with name='NonExistent' not found");
    }
}
