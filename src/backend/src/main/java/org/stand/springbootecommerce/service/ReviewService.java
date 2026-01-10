package org.stand.springbootecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.Review;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.repository.ReviewRepository;
import org.stand.springbootecommerce.repository.UserRepository;
import java.util.Objects;

/**
 * Service pour la gestion des avis produits
 * Logique métier TechShop Pro
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Ajouter un avis pour un produit
     */
    @Transactional
    public Review addReview(Long userId, Long productId, Integer rating, String comment) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new BaseException("Utilisateur non trouvé"));

        Product product = productRepository.findById(Objects.requireNonNull(productId))
                .orElseThrow(() -> new BaseException("Produit non trouvé"));

        // Vérifier si l'utilisateur a déjà noté ce produit
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BaseException("Vous avez déjà noté ce produit");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        review.setIsVerifiedPurchase(false);

        return reviewRepository.save(Objects.requireNonNull(review));
    }

    /**
     * Récupérer les avis d'un produit
     */
    public Page<Review> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    /**
     * Calculer la note moyenne d'un produit
     */
    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    /**
     * Marquer un avis comme utile
     */
    @Transactional
    public void markReviewAsHelpful(Long reviewId) {
        Review review = reviewRepository.findById(Objects.requireNonNull(reviewId))
                .orElseThrow(() -> new BaseException("Avis non trouvé"));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(Objects.requireNonNull(review));
    }
}
