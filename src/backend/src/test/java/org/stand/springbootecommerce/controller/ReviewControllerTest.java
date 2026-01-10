package org.stand.springbootecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.dto.response.ReviewResponse;
import org.stand.springbootecommerce.entity.user.Review;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.service.ReviewService;
import org.stand.springbootecommerce.service.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur ReviewController")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test")
                .surname("User")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/reviews/product/{productId} - Doit ajouter un avis")
    @WithMockUser(username = "test@example.com")
    void addReview_Success() throws Exception {
        // Arrange
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("rating", 5);
        reviewMap.put("comment", "Excellent!");

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(reviewService.addReview(anyLong(), anyLong(), anyInt(), any())).thenReturn(new Review());
        when(modelMapper.map(any(), eq(ReviewResponse.class))).thenReturn(new ReviewResponse());

        // Act & Assert
        mockMvc.perform(post("/api/v1/reviews/product/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewMap)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/reviews/product/{productId} - Doit retourner les avis d'un produit")
    void getProductReviews_Success() throws Exception {
        // Arrange
        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(new Review()), PageRequest.of(0, 10), 1);
        when(reviewService.getProductReviews(anyLong(), any())).thenReturn(reviewPage);
        when(modelMapper.map(any(), eq(ReviewResponse.class))).thenReturn(new ReviewResponse());

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/product/1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/reviews/product/{productId}/rating - Doit retourner la note moyenne")
    void getAverageRating_Success() throws Exception {
        // Arrange
        when(reviewService.getAverageRating(1L)).thenReturn(4.5);

        // Act & Assert
        mockMvc.perform(get("/api/v1/reviews/product/1/rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(4.5));
    }

    @Test
    @DisplayName("POST /api/v1/reviews/{reviewId}/helpful - Doit marquer un avis comme utile")
    @WithMockUser
    void markAsHelpful_Success() throws Exception {
        // Arrange
        doNothing().when(reviewService).markReviewAsHelpful(anyLong());

        // Act & Assert
        mockMvc.perform(post("/api/v1/reviews/1/helpful"))
                .andExpect(status().isOk());
    }
}
