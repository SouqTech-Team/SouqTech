package org.stand.springbootecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stand.springbootecommerce.entity.user.Review;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les avis produits
 * Personnalisation TechShop Pro
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Trouver tous les avis d'un produit
    Page<Review> findByProductId(Long productId, Pageable pageable);

    // Trouver tous les avis d'un utilisateur
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Trouver les avis par note
    List<Review> findByProductIdAndRating(Long productId, Integer rating);

    // Calculer la note moyenne d'un produit
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    // Compter le nombre d'avis d'un produit
    Long countByProductId(Long productId);

    // Vérifier si un utilisateur a déjà noté un produit
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Trouver l'avis d'un utilisateur pour un produit
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    // Trouver les avis les plus utiles
    Page<Review> findByProductIdOrderByHelpfulCountDesc(Long productId, Pageable pageable);

    // Trouver les avis vérifiés (achats confirmés)
    Page<Review> findByProductIdAndIsVerifiedPurchaseTrue(Long productId, Pageable pageable);

    // Trouver les meilleurs avis (4-5 étoiles)
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating >= 4 ORDER BY r.createdAt DESC")
    List<Review> findTopRatedReviewsByProductId(@Param("productId") Long productId);
}
