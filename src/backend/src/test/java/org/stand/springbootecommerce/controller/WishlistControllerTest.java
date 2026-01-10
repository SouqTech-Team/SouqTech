package org.stand.springbootecommerce.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.dto.response.WishlistResponse;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.entity.user.Wishlist;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.service.UserService;
import org.stand.springbootecommerce.service.WishlistService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur WishlistController")
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/wishlist - Doit retourner la wishlist")
    @WithMockUser(username = "test@example.com")
    void getWishlist_Success() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(wishlistService.getOrCreateWishlist(anyLong())).thenReturn(new Wishlist());
        when(modelMapper.map(any(), eq(WishlistResponse.class))).thenReturn(new WishlistResponse());

        // Act & Assert
        mockMvc.perform(get("/api/v1/wishlist"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/wishlist/add/{productId} - Doit ajouter un produit")
    @WithMockUser(username = "test@example.com")
    void addToWishlist_Success() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(wishlistService.addToWishlist(anyLong(), anyLong())).thenReturn(new Wishlist());
        when(modelMapper.map(any(), eq(WishlistResponse.class))).thenReturn(new WishlistResponse());

        // Act & Assert
        mockMvc.perform(post("/api/v1/wishlist/add/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/wishlist/remove/{productId} - Doit retirer un produit")
    @WithMockUser(username = "test@example.com")
    void removeFromWishlist_Success() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(wishlistService.removeFromWishlist(anyLong(), anyLong())).thenReturn(new Wishlist());
        when(modelMapper.map(any(), eq(WishlistResponse.class))).thenReturn(new WishlistResponse());

        // Act & Assert
        mockMvc.perform(delete("/api/v1/wishlist/remove/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/wishlist/share/toggle - Doit activer/désactiver le partage")
    @WithMockUser(username = "test@example.com")
    void toggleSharing_Success() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(wishlistService.togglePublicSharing(anyLong())).thenReturn(new Wishlist());
        when(modelMapper.map(any(), eq(WishlistResponse.class))).thenReturn(new WishlistResponse());

        // Act & Assert
        mockMvc.perform(post("/api/v1/wishlist/share/toggle"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/wishlist/shared/{token} - Doit retourner une wishlist partagée")
    void getSharedWishlist_Success() throws Exception {
        // Arrange
        when(wishlistService.getSharedWishlist(anyString())).thenReturn(new Wishlist());
        when(modelMapper.map(any(), eq(WishlistResponse.class))).thenReturn(new WishlistResponse());

        // Act & Assert
        mockMvc.perform(get("/api/v1/wishlist/shared/test-token-123"))
                .andExpect(status().isOk());
    }
}
