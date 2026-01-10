package org.stand.springbootecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.entity.user.Order;
import org.stand.springbootecommerce.entity.user.OrderStatus;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.service.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur OrderController")
class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @MockBean
        private JwtService jwtService;

        @Autowired
        private ObjectMapper objectMapper;

        private Order order;
        private User user;
        private Product product;

        @BeforeEach
        void setUp() {
                user = User.builder()
                                .id(1L)
                                .email("user@test.com")
                                .build();

                product = Product.builder()
                                .id(1L)
                                .name("Product 1")
                                .price(BigDecimal.valueOf(100.00))
                                .build();

                order = Order.builder()
                                .id(1L)
                                .user(user)
                                .products(Arrays.asList(product))
                                .totalAmount(BigDecimal.valueOf(100.00))
                                .status(OrderStatus.PENDING)
                                .build();
        }

        @Test
        @DisplayName("POST /api/v1/order - Doit créer une commande")
        void createOrder_Success() throws Exception {
                // Arrange
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("productIds", Arrays.asList(1L, 2L));

                when(orderService.createOrder(anyList())).thenReturn(order);

                // Act & Assert
                mockMvc.perform(post("/api/v1/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Arrays.asList(1L, 2L))))
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("GET /api/v1/order - Doit retourner les commandes de l'utilisateur")
        void getMyOrders_Success() throws Exception {
                // Arrange
                when(orderService.getMyOrders()).thenReturn(Arrays.asList(order));

                // Act & Assert
                mockMvc.perform(get("/api/v1/order"))
                                .andExpect(status().isOk());
        }
}
