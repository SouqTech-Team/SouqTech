package org.stand.springbootecommerce.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du repository ProductRepository")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ProductCategory category;
    private Product product;

    @BeforeEach
    void setUp() {
        // Créer une catégorie
        category = ProductCategory.builder()
                .name("Electronics")
                .description("Electronic devices")
                .build();
        entityManager.persist(category);

        // Créer un produit
        product = Product.builder()
                .name("Laptop Dell XPS")
                .description("High-performance laptop")
                .shortDescription("Dell XPS")
                .image("laptop.jpg")
                .price(new java.math.BigDecimal("1200.0"))
                .quantity(5)
                .category(category)
                .build();
        entityManager.persist(product);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver tous les produits paginés")
    void findAll_Pageable_Success() {
        Page<Product> products = productRepository.findAll(PageRequest.of(0, 10));

        assertThat(products).isNotEmpty();
        assertThat(products.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Doit trouver les produits par ID de catégorie")
    void findByCategoryId_Success() {
        List<Product> products = productRepository.findByCategoryId(category.getId());

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("Doit rechercher les produits par nom (ignorant la casse)")
    void findByNameContainingIgnoreCase_Success() {
        List<Product> products = productRepository.findByNameContainingIgnoreCase("laptop");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).containsIgnoringCase("laptop");
    }

    @Test
    @DisplayName("Doit rechercher les produits par nom avec pagination")
    void findByNameContainingIgnoreCase_Pageable_Success() {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase("dell", PageRequest.of(0, 10));

        assertThat(products).isNotEmpty();
        assertThat(products.getContent().get(0).getName()).containsIgnoringCase("dell");
    }

    @Test
    @DisplayName("Doit rechercher les produits par nom de catégorie")
    void findByCategoryNameContainingIgnoreCase_Success() {
        List<Product> products = productRepository.findByCategoryNameContainingIgnoreCase("electronics");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getCategory().getName()).containsIgnoringCase("electronics");
    }

    @Test
    @DisplayName("Doit rechercher les produits par nom de catégorie avec pagination")
    void findByCategoryNameContainingIgnoreCase_Pageable_Success() {
        Page<Product> products = productRepository.findByCategoryNameContainingIgnoreCase("elec",
                PageRequest.of(0, 10));

        assertThat(products).isNotEmpty();
        assertThat(products.getContent().get(0).getCategory().getName()).containsIgnoringCase("elec");
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucun produit ne correspond")
    void findByNameContainingIgnoreCase_NotFound() {
        List<Product> products = productRepository.findByNameContainingIgnoreCase("NonExistent");

        assertThat(products).isEmpty();
    }
}
