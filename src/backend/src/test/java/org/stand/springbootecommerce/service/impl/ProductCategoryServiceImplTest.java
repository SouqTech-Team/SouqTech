package org.stand.springbootecommerce.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.repository.ProductCategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de ProductCategoryServiceImpl")
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository categoryRepository;

    @InjectMocks
    private ProductCategoryServiceImpl categoryService;

    private ProductCategory category;

    @BeforeEach
    void setUp() {
        category = ProductCategory.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic gadgets")
                .build();
    }

    @Test
    @DisplayName("Doit retourner la liste de toutes les catégories")
    void getProductCategories_Success() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

        // Act
        List<ProductCategory> result = categoryService.getProductCategories();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Doit trouver une catégorie par son ID")
    void getProductCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        ProductCategory result = categoryService.getProductCategoryById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Doit lancer une exception si la catégorie n'existe pas")
    void getProductCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> categoryService.getProductCategoryById(99L))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessageContaining("ProductCategory with id='99' not found");
    }

    @Test
    @DisplayName("Doit ajouter une catégorie")
    void addProductCategory_Success() {
        // Arrange
        when(categoryRepository.save(any(ProductCategory.class))).thenReturn(category);

        // Act
        ProductCategory result = categoryService.addProductCategory(category);

        // Assert
        assertThat(result).isNotNull();
        verify(categoryRepository).save(category);
    }
}
