package org.stand.springbootecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.annotation.DirtiesContext;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.AuthenticationResponse;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.repository.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests d'Intégration - Activités Utilisateur (Wishlist & Reviews)")
class UserActivityIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProductCategoryRepository categoryRepository;

        @Autowired
        private WishlistRepository wishlistRepository;

        @Autowired
        private ReviewRepository reviewRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private OrderRepository orderRepository;

        private String jwtToken;
        private Long testProductId;

        @BeforeEach
        void setUp() throws Exception {
                // Nettoyage dans l'ordre pour les contraintes
                orderRepository.deleteAll();
                reviewRepository.deleteAll();
                wishlistRepository.deleteAll();
                productRepository.deleteAll();
                userRepository.deleteAll();
                categoryRepository.deleteAll();

                // 1. Créer une catégorie et un produit de test
                ProductCategory category = ProductCategory.builder()
                                .name("Test Category")
                                .description("Test Description")
                                .build();
                category = categoryRepository.save(category);

                Product product = Product.builder()
                                .name("Test Product")
                                .description("Long Description")
                                .shortDescription("Short Description")
                                .price(new BigDecimal("99.99"))
                                .quantity(10)
                                .image("test.jpg")
                                .category(category)
                                .build();
                product = productRepository.save(product);
                testProductId = product.getId();

                // 2. Enregistrer et connecter un utilisateur
                RegisterRequest registerRequest = RegisterRequest.builder()
                                .name("Activity")
                                .surname("User")
                                .email("activity@test.com")
                                .password("password123")
                                .build();

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated());

                AuthenticationRequest loginRequest = AuthenticationRequest.builder()
                                .email("activity@test.com")
                                .password("password123")
                                .build();

                MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String responseBody = loginResult.getResponse().getContentAsString();
                AuthenticationResponse authResponse = objectMapper.readValue(responseBody,
                                AuthenticationResponse.class);
                jwtToken = authResponse.getToken();
        }

        @Test
        @DisplayName("Gestion de la Wishlist : Ajouter et Vérifier")
        void wishlistFlow_Success() throws Exception {
                // Ajouter à la wishlist
                mockMvc.perform(post("/api/v1/wishlist/add/" + testProductId)
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk());

                // Vérifier la wishlist (WishlistResponse a un champ 'products')
                mockMvc.perform(get("/api/v1/wishlist")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.products").isArray())
                                .andExpect(jsonPath("$.products[0].name").value("Test Product"));
        }

        @Test
        @DisplayName("Laisser un Avis sur un produit")
        void reviewFlow_Success() throws Exception {
                Map<String, Object> reviewRequest = new HashMap<>();
                reviewRequest.put("rating", 5);
                reviewRequest.put("comment", "Excellent produit !");

                mockMvc.perform(post("/api/v1/reviews/product/" + testProductId)
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.rating").value(5))
                                .andExpect(jsonPath("$.comment").value("Excellent produit !"));

                // Vérifier la liste des avis (Pageable response => field 'content' par défaut
                // dans Page)
                mockMvc.perform(get("/api/v1/reviews/product/" + testProductId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].comment").value("Excellent produit !"));
        }
}
