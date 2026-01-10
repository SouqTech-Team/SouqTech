package org.stand.springbootecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.stand.springbootecommerce.entity.user.Wishlist;

import java.util.Optional;

/**
 * Repository pour les listes de souhaits
 * Fonctionnalité TechShop Pro
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Trouver la wishlist d'un utilisateur
    Optional<Wishlist> findByUserId(Long userId);

    // Trouver une wishlist publique par son token de partage
    Optional<Wishlist> findByShareToken(String shareToken);

    // Vérifier si un produit est dans la wishlist d'un utilisateur
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w JOIN w.products p WHERE w.user.id = :userId AND p.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    // Compter le nombre de produits dans la wishlist
    @Query("SELECT SIZE(w.products) FROM Wishlist w WHERE w.user.id = :userId")
    Integer countProductsByUserId(@Param("userId") Long userId);
}
