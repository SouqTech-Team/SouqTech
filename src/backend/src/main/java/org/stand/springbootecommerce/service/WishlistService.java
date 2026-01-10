package org.stand.springbootecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.entity.user.Wishlist;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.repository.UserRepository;
import org.stand.springbootecommerce.repository.WishlistRepository;

import java.util.UUID;
import java.util.Objects;

/**
 * Service pour la gestion des listes de souhaits
 * Fonctionnalité TechShop Pro
 */
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private WishlistService self;

    @org.springframework.beans.factory.annotation.Autowired
    public void setSelf(@org.springframework.context.annotation.Lazy WishlistService self) {
        this.self = self;
    }

    /**
     * Récupérer ou créer la wishlist d'un utilisateur
     */
    @Transactional
    public Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(Objects.requireNonNull(userId))
                .orElseGet(() -> createWishlist(userId));
    }

    private Wishlist createWishlist(Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new BaseException("Utilisateur non trouvé"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setShareToken(UUID.randomUUID().toString());
        return wishlistRepository.save(Objects.requireNonNull(wishlist));
    }

    /**
     * Ajouter un produit à la wishlist
     */
    @Transactional
    public Wishlist addToWishlist(Long userId, Long productId) {
        Wishlist wishlist = self.getOrCreateWishlist(userId);
        Product product = productRepository.findById(Objects.requireNonNull(productId))
                .orElseThrow(() -> new BaseException("Produit non trouvé"));

        wishlist.addProduct(product);
        return wishlistRepository.save(Objects.requireNonNull(wishlist));
    }

    /**
     * Retirer un produit de la wishlist
     */
    @Transactional
    public Wishlist removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = self.getOrCreateWishlist(userId);
        Product product = productRepository.findById(Objects.requireNonNull(productId))
                .orElseThrow(() -> new BaseException("Produit non trouvé"));

        wishlist.removeProduct(product);
        return wishlistRepository.save(Objects.requireNonNull(wishlist));
    }

    /**
     * Activer/Désactiver le partage public
     */
    @Transactional
    public Wishlist togglePublicSharing(Long userId) {
        Wishlist wishlist = self.getOrCreateWishlist(userId);
        wishlist.setIsPublic(!wishlist.getIsPublic());
        if (wishlist.getShareToken() == null) {
            wishlist.setShareToken(UUID.randomUUID().toString());
        }
        return wishlistRepository.save(Objects.requireNonNull(wishlist));
    }

    /**
     * Récupérer une wishlist partagée
     */
    public Wishlist getSharedWishlist(String shareToken) {
        Wishlist wishlist = wishlistRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new BaseException("Wishlist introuvable"));

        if (!wishlist.getIsPublic()) {
            throw new BaseException("Cette wishlist est privée");
        }
        return wishlist;
    }
}
