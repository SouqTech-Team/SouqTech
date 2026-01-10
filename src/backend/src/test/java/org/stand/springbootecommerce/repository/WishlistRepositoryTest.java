package org.stand.springbootecommerce.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.entity.user.Wishlist;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du repository WishlistRepository")
class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Product product;
    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        // Créer une catégorie
        ProductCategory category = ProductCategory.builder()
                .name("Electronics")
                .description("Electronic devices")
                .build();
        entityManager.persist(category);

        // Créer un utilisateur
        user = User.builder()
                .email("test@example.com")
                .name("Test")
                .surname("User")
                .password("password")
                .build();
        entityManager.persist(user);

        // Créer un produit
        product = Product.builder()
                .name("Test Product")
                .description("Description")
                .shortDescription("Test")
                .image("test.jpg")
                .price(new java.math.BigDecimal("100.0"))
                .quantity(10)
                .category(category)
                .build();
        entityManager.persist(product);

        // Créer une wishlist
        wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setPublic(true);
        wishlist.setShareToken("abc123");
        wishlist.getProducts().add(product);
        entityManager.persist(wishlist);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver la wishlist par ID d'utilisateur")
    void findByUserId_Success() {
        Optional<Wishlist> foundWishlist = wishlistRepository.findByUserId(user.getId());

        assertThat(foundWishlist).isPresent();
        assertThat(foundWishlist.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Doit retourner Optional.empty si l'utilisateur n'a pas de wishlist")
    void findByUserId_NotFound() {
        Optional<Wishlist> foundWishlist = wishlistRepository.findByUserId(999L);

        assertThat(foundWishlist).isEmpty();
    }

    @Test
    @DisplayName("Doit trouver une wishlist par son token de partage")
    void findByShareToken_Success() {
        Optional<Wishlist> foundWishlist = wishlistRepository.findByShareToken("abc123");

        assertThat(foundWishlist).isPresent();
        assertThat(foundWishlist.get().getShareToken()).isEqualTo("abc123");
        assertThat(foundWishlist.get().isPublic()).isTrue();
    }

    @Test
    @DisplayName("Doit retourner Optional.empty si le token n'existe pas")
    void findByShareToken_NotFound() {
        Optional<Wishlist> foundWishlist = wishlistRepository.findByShareToken("invalid");

        assertThat(foundWishlist).isEmpty();
    }

    @Test
    @DisplayName("Doit vérifier si un produit est dans la wishlist d'un utilisateur")
    void existsByUserIdAndProductId_Success() {
        boolean exists = wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Doit retourner false si le produit n'est pas dans la wishlist")
    void existsByUserIdAndProductId_NotFound() {
        boolean exists = wishlistRepository.existsByUserIdAndProductId(user.getId(), 999L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Doit compter le nombre de produits dans la wishlist")
    void countProductsByUserId_Success() {
        Integer count = wishlistRepository.countProductsByUserId(user.getId());

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit retourner 0 si la wishlist est vide")
    void countProductsByUserId_Empty() {
        // Créer un nouvel utilisateur sans wishlist
        User newUser = User.builder()
                .email("new@example.com")
                .name("New")
                .surname("User")
                .password("password")
                .build();
        entityManager.persist(newUser);

        Wishlist emptyWishlist = new Wishlist();
        emptyWishlist.setUser(newUser);
        entityManager.persist(emptyWishlist);
        entityManager.flush();

        Integer count = wishlistRepository.countProductsByUserId(newUser.getId());

        assertThat(count).isZero();
    }
}
