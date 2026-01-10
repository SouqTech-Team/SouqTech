package org.stand.springbootecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.dto.response.ReviewResponse;
import org.stand.springbootecommerce.entity.user.Review;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.service.ReviewService;
import org.stand.springbootecommerce.service.UserService;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product review management")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/product/{productId}")
    @Operation(summary = "Add a review", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review added"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Long productId,
            @RequestBody org.stand.springbootecommerce.dto.request.ReviewRequest request) { // Use fully qualified if
                                                                                            // imports clash, or add
                                                                                            // import
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(auth.getName());
        Review review = reviewService.addReview(user.getId(), productId, request.getRating(), request.getComment());
        return ResponseEntity.ok(convertToDto(review));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "List reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            Pageable pageable) {
        Page<Review> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews.map(this::convertToDto));
    }

    @GetMapping("/product/{productId}/rating")
    @Operation(summary = "Average rating of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAverageRating(productId));
    }

    private ReviewResponse convertToDto(Review review) {
        ReviewResponse dto = modelMapper.map(review, ReviewResponse.class);
        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getName());
        }
        return dto;
    }

    @PostMapping("/{reviewId}/helpful")
    @Operation(summary = "Vote 'Helpful' for a review", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vote recorded")
    })
    public ResponseEntity<Void> markAsHelpful(@PathVariable Long reviewId) {
        reviewService.markReviewAsHelpful(reviewId);
        return ResponseEntity.ok().build();
    }
}
