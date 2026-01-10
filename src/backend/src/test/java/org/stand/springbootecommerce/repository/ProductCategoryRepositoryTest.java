package org.stand.springbootecommerce.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.stand.springbootecommerce.entity.user.ProductCategory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du repository ProductCategoryRepository")
class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProductCategory category;

    @BeforeEach
    void setUp() {
        // Créer une catégorie
        category = ProductCategory.builder()
                .name("Electronics")
                .description("Electronic devices and gadgets")
                .build();
        entityManager.persist(category);
        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver une catégorie par son nom")
    void findByName_Success() {
        Optional<ProductCategory> foundCategory = productCategoryRepository.findByName("Electronics");

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Electronics");
        assertThat(foundCategory.get().getDescription()).isEqualTo("Electronic devices and gadgets");
    }

    @Test
    @DisplayName("Doit retourner Optional.empty si la catégorie n'existe pas")
    void findByName_NotFound() {
        Optional<ProductCategory> foundCategory = productCategoryRepository.findByName("NonExistent");

        assertThat(foundCategory).isEmpty();
    }

    @Test
    @DisplayName("Doit sauvegarder une nouvelle catégorie")
    void save_Success() {
        ProductCategory newCategory = ProductCategory.builder()
                .name("Books")
                .description("Books and literature")
                .build();

        ProductCategory savedCategory = productCategoryRepository.save(newCategory);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Books");
    }

    @Test
    @DisplayName("Doit trouver toutes les catégories")
    void findAll_Success() {
        var categories = productCategoryRepository.findAll();

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("Electronics");
    }
}
