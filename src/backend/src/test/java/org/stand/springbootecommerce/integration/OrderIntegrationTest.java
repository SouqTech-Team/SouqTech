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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.AuthenticationResponse;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.repository.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests d'Intégration - Commandes (Orders)")
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;
    private Long testProductId;

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyage dans l'ordre pour les contraintes
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        // 1. Créer produit de test
        ProductCategory category = categoryRepository.save(ProductCategory.builder()
                .name("Order Category")
                .description("Desc")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Order Product")
                .description("Long Desc")
                .shortDescription("Short Desc")
                .price(new BigDecimal("50.00"))
                .quantity(100)
                .image("order.jpg")
                .category(category)
                .build());
        testProductId = product.getId();

        // 2. Créer utilisateur et recup token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Order")
                .surname("User")
                .email("order@test.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        AuthenticationRequest loginRequest = AuthenticationRequest.builder()
                .email("order@test.com")
                .password("password123")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthenticationResponse.class)
                .getToken();
    }

    @Test
    @DisplayName("Flux de commande : Créer -> Lister")
    void orderFlow_Success() throws Exception {
        List<Long> productIds = Arrays.asList(testProductId);

        // Passer la commande
        mockMvc.perform(post("/api/v1/order")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productIds)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalAmount").value(50.0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Vérifier l'historique
        mockMvc.perform(get("/api/v1/order")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalAmount").value(50.0));
    }
}
