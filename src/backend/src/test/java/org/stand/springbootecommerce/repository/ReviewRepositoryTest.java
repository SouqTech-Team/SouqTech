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
import org.stand.springbootecommerce.entity.user.Review;
import org.stand.springbootecommerce.entity.user.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du repository ReviewRepository")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Product product;
    private Review review;

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

        // Créer un avis
        review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(5);
        review.setComment("Excellent!");
        review.setHelpfulCount(10);
        review.setIsVerifiedPurchase(true);
        entityManager.persist(review);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver les avis par ID de produit")
    void findByProductId_Success() {
        Page<Review> reviews = reviewRepository.findByProductId(product.getId(), PageRequest.of(0, 10));

        assertThat(reviews).isNotEmpty();
        assertThat(reviews.getContent().get(0).getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("Doit trouver les avis par ID d'utilisateur")
    void findByUserId_Success() {
        Page<Review> reviews = reviewRepository.findByUserId(user.getId(), PageRequest.of(0, 10));

        assertThat(reviews).isNotEmpty();
        assertThat(reviews.getContent().get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Doit trouver les avis par produit et note")
    void findByProductIdAndRating_Success() {
        List<Review> reviews = reviewRepository.findByProductIdAndRating(product.getId(), 5);

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("Doit calculer la note moyenne d'un produit")
    void findAverageRatingByProductId_Success() {
        Double avgRating = reviewRepository.findAverageRatingByProductId(product.getId());

        assertThat(avgRating).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Doit compter le nombre d'avis d'un produit")
    void countByProductId_Success() {
        Long count = reviewRepository.countByProductId(product.getId());

        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("Doit vérifier si un utilisateur a déjà noté un produit")
    void existsByUserIdAndProductId_Success() {
        boolean exists = reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Doit retourner false si l'utilisateur n'a pas noté le produit")
    void existsByUserIdAndProductId_NotFound() {
        boolean exists = reviewRepository.existsByUserIdAndProductId(999L, product.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Doit trouver l'avis d'un utilisateur pour un produit")
    void findByUserIdAndProductId_Success() {
        Optional<Review> foundReview = reviewRepository.findByUserIdAndProductId(user.getId(), product.getId());

        assertThat(foundReview).isPresent();
        assertThat(foundReview.get().getComment()).isEqualTo("Excellent!");
    }

    @Test
    @DisplayName("Doit trier les avis par nombre de votes utiles")
    void findByProductIdOrderByHelpfulCountDesc_Success() {
        Page<Review> reviews = reviewRepository.findByProductIdOrderByHelpfulCountDesc(product.getId(),
                PageRequest.of(0, 10));

        assertThat(reviews).isNotEmpty();
        assertThat(reviews.getContent().get(0).getHelpfulCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("Doit trouver les avis vérifiés")
    void findByProductIdAndIsVerifiedPurchaseTrue_Success() {
        Page<Review> reviews = reviewRepository.findByProductIdAndIsVerifiedPurchaseTrue(product.getId(),
                PageRequest.of(0, 10));

        assertThat(reviews).isNotEmpty();
        assertThat(reviews.getContent().get(0).getIsVerifiedPurchase()).isTrue();
    }

    @Test
    @DisplayName("Doit trouver les meilleurs avis (4-5 étoiles)")
    void findTopRatedReviewsByProductId_Success() {
        List<Review> topReviews = reviewRepository.findTopRatedReviewsByProductId(product.getId());

        assertThat(topReviews).isNotEmpty();
        assertThat(topReviews.get(0).getRating()).isGreaterThanOrEqualTo(4);
    }
}
