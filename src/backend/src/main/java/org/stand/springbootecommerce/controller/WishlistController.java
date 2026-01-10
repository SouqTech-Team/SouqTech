package org.stand.springbootecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.dto.response.WishlistResponse;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.entity.user.Wishlist;
import org.stand.springbootecommerce.service.UserService;
import org.stand.springbootecommerce.service.WishlistService;

@Tag(name = "Wishlist", description = "User wishlist management")
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByEmail(auth.getName());
    }

    @GetMapping
    @Operation(summary = "Get my wishlist", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wishlist retrieved")
    })
    public ResponseEntity<WishlistResponse> getMyWishlist() {
        Wishlist wishlist = wishlistService.getOrCreateWishlist(getCurrentUser().getId());
        return ResponseEntity.ok(convertToDto(wishlist));
    }

    @PostMapping("/add/{productId}")
    @Operation(summary = "Add a product to wishlist", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added")
    })
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long productId) {
        Wishlist wishlist = wishlistService.addToWishlist(getCurrentUser().getId(), productId);
        return ResponseEntity.ok(convertToDto(wishlist));
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Remove a product from wishlist", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed")
    })
    public ResponseEntity<WishlistResponse> removeFromWishlist(@PathVariable Long productId) {
        Wishlist wishlist = wishlistService.removeFromWishlist(getCurrentUser().getId(), productId);
        return ResponseEntity.ok(convertToDto(wishlist));
    }

    @PostMapping("/share/toggle")
    @Operation(summary = "Toggle public sharing of wishlist", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sharing status updated")
    })
    public ResponseEntity<WishlistResponse> toggleSharing() {
        Wishlist wishlist = wishlistService.togglePublicSharing(getCurrentUser().getId());
        return ResponseEntity.ok(convertToDto(wishlist));
    }

    @GetMapping("/shared/{token}")
    @Operation(summary = "Access a wishlist via sharing link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared wishlist found"),
            @ApiResponse(responseCode = "404", description = "Link invalid or expired")
    })
    public ResponseEntity<WishlistResponse> getSharedWishlist(@PathVariable String token) {
        Wishlist wishlist = wishlistService.getSharedWishlist(token);
        return ResponseEntity.ok(convertToDto(wishlist));
    }

    private WishlistResponse convertToDto(Wishlist wishlist) {
        return modelMapper.map(wishlist, WishlistResponse.class);
    }
}
