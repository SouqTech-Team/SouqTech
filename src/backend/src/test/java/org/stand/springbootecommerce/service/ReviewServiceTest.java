package org.stand.springbootecommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.Review;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.repository.ReviewRepository;
import org.stand.springbootecommerce.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de ReviewService")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Product product;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .name("Test")
                .surname("User")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Product Test")
                .build();

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setProduct(product);
        review.setRating(5);
        review.setComment("Excellent produit!");
        review.setHelpfulCount(0);
    }

    @Test
    @DisplayName("Doit ajouter un avis avec succès")
    void addReview_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        Review result = reviewService.addReview(1L, 1L, 5, "Excellent produit!");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Doit lancer une exception si l'utilisateur a déjà noté le produit")
    void addReview_AlreadyReviewed() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> reviewService.addReview(1L, 1L, 5, "Test"))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Vous avez déjà noté ce produit");
    }

    @Test
    @DisplayName("Doit lancer une exception si l'utilisateur n'existe pas lors de l'ajout d'avis")
    void addReview_UserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.addReview(99L, 1L, 5, "Test"))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Utilisateur non trouvé");
    }

    @Test
    @DisplayName("Doit lancer une exception si le produit n'existe pas lors de l'ajout d'avis")
    void addReview_ProductNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.addReview(1L, 99L, 5, "Test"))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    @DisplayName("Doit récupérer les avis d'un produit")
    void getProductReviews_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(review));
        when(reviewRepository.findByProductId(1L, pageable)).thenReturn(reviewPage);

        // Act
        Page<Review> result = reviewService.getProductReviews(1L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(reviewRepository, times(1)).findByProductId(1L, pageable);
    }

    @Test
    @DisplayName("Doit calculer la note moyenne d'un produit")
    void getAverageRating_Success() {
        // Arrange
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(4.56);

        // Act
        Double result = reviewService.getAverageRating(1L);

        // Assert
        assertThat(result).isEqualTo(4.6); // Arrondi à 1 décimale
    }

    @Test
    @DisplayName("Doit retourner 0.0 si aucun avis")
    void getAverageRating_NoReviews() {
        // Arrange
        when(reviewRepository.findAverageRatingByProductId(1L)).thenReturn(null);

        // Act
        Double result = reviewService.getAverageRating(1L);

        // Assert
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Doit marquer un avis comme utile")
    void markReviewAsHelpful_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        reviewService.markReviewAsHelpful(1L);

        // Assert
        assertThat(review.getHelpfulCount()).isEqualTo(1);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    @DisplayName("Doit lancer une exception si l'avis n'existe pas")
    void markReviewAsHelpful_NotFound() {
        // Arrange
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reviewService.markReviewAsHelpful(999L))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Avis non trouvé");
    }
}
