package org.stand.springbootecommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.entity.user.Wishlist;
import org.stand.springbootecommerce.error.BaseException;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.repository.UserRepository;
import org.stand.springbootecommerce.repository.WishlistRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de WishlistService")
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private User user;
    private Product product;
    private Wishlist wishlist;

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

        wishlist = new Wishlist();
        wishlist.setId(1L);
        wishlist.setUser(user);
        wishlist.setShareToken("test-token-123");
        wishlist.setPublic(false);
        ReflectionTestUtils.setField(wishlistService, "self", wishlistService);
    }

    @Test
    @DisplayName("Doit récupérer la wishlist existante")
    void getOrCreateWishlist_ExistingWishlist() {
        // Arrange
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));

        // Act
        Wishlist result = wishlistService.getOrCreateWishlist(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(wishlistRepository, times(1)).findByUserId(1L);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit créer une nouvelle wishlist si elle n'existe pas")
    void getOrCreateWishlist_CreateNew() {
        // Arrange
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        Wishlist result = wishlistService.getOrCreateWishlist(1L);

        // Assert
        assertThat(result).isNotNull();
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    @DisplayName("Doit ajouter un produit à la wishlist")
    void addToWishlist_Success() {
        // Arrange
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        Wishlist result = wishlistService.addToWishlist(1L, 1L);

        // Assert
        assertThat(result).isNotNull();
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    @DisplayName("Doit gérer l'ajout d'un produit déjà présent dans la wishlist")
    void addToWishlist_ProductAlreadyInWishlist() {
        // Arrange
        wishlist.getProducts().add(product);
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        Wishlist result = wishlistService.addToWishlist(1L, 1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).contains(product);
    }

    @Test
    @DisplayName("Doit lancer une exception si le produit n'existe pas")
    void addToWishlist_ProductNotFound() {
        // Arrange
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> wishlistService.addToWishlist(1L, 999L))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Produit non trouvé");
    }

    @Test
    @DisplayName("Doit retirer un produit de la wishlist")
    void removeFromWishlist_Success() {
        // Arrange
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        Wishlist result = wishlistService.removeFromWishlist(1L, 1L);

        // Assert
        assertThat(result).isNotNull();
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    @DisplayName("Doit activer/désactiver le partage public")
    void togglePublicSharing_Success() {
        // Arrange
        when(wishlistRepository.findByUserId(1L)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        // Act
        Wishlist result = wishlistService.togglePublicSharing(1L);

        // Assert
        assertThat(result).isNotNull();
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    @DisplayName("Doit récupérer une wishlist partagée publique")
    void getSharedWishlist_Success() {
        // Arrange
        wishlist.setPublic(true);
        when(wishlistRepository.findByShareToken("test-token-123")).thenReturn(Optional.of(wishlist));

        // Act
        Wishlist result = wishlistService.getSharedWishlist("test-token-123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getShareToken()).isEqualTo("test-token-123");
    }

    @Test
    @DisplayName("Doit lancer une exception si la wishlist partagée est privée")
    void getSharedWishlist_Private() {
        // Arrange
        wishlist.setPublic(false);
        when(wishlistRepository.findByShareToken("test-token-123")).thenReturn(Optional.of(wishlist));

        // Act & Assert
        assertThatThrownBy(() -> wishlistService.getSharedWishlist("test-token-123"))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Cette wishlist est privée");
    }
}
