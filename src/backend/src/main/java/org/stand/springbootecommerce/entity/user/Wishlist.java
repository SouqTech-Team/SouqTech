package org.stand.springbootecommerce.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Wishlist - Liste de souhaits personnalisée
 * Fonctionnalité unique ajoutée pour TechShop Pro
 */
@Entity
@Table(name = "wishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wishlist_products", joinColumns = @JoinColumn(name = "wishlist_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new HashSet<>();

    @Column(name = "is_public")
    private boolean isPublic = false;

    @Column(name = "share_token", unique = true)
    private String shareToken; // Pour partager la wishlist

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public void addProduct(Product product) {
        this.products.add(product);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean containsProduct(Product product) {
        return this.products.contains(product);
    }

    public int getProductCount() {
        return this.products.size();
    }
}
