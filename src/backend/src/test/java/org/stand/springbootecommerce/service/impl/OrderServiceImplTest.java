package org.stand.springbootecommerce.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stand.springbootecommerce.entity.user.Order;
import org.stand.springbootecommerce.entity.user.OrderStatus;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.repository.OrderRepository;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.service.AuthenticationService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de OrderServiceImpl")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product1;
    private Product product2;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .name("Test")
                .surname("User")
                .build();

        product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .price(BigDecimal.valueOf(200.00))
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .products(Arrays.asList(product1, product2))
                .totalAmount(BigDecimal.valueOf(300.00))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Doit créer une commande avec succès")
    void createOrder_Success() throws Exception {
        // Arrange
        List<Long> productIds = Arrays.asList(1L, 2L);
        when(authenticationService.me()).thenReturn(user);
        when(productRepository.findAllById(anyList())).thenReturn(Arrays.asList(product1, product2));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.createOrder(productIds);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(300.00));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Doit lancer une exception si l'utilisateur n'est pas authentifié")
    void createOrder_UserNotAuthenticated() throws Exception {
        // Arrange
        List<Long> productIds = Arrays.asList(1L, 2L);
        when(authenticationService.me()).thenThrow(new RuntimeException("Not authenticated"));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(productIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User must be logged in to order");
    }

    @Test
    @DisplayName("Doit lancer une exception si aucun produit valide")
    void createOrder_NoValidProducts() throws Exception {
        // Arrange
        List<Long> productIds = Arrays.asList(999L);
        when(authenticationService.me()).thenReturn(user);
        when(productRepository.findAllById(anyList())).thenReturn(List.of());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(productIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot create order with no valid products");
    }

    @Test
    @DisplayName("Doit récupérer les commandes de l'utilisateur connecté")
    void getMyOrders_Success() throws Exception {
        // Arrange
        when(authenticationService.me()).thenReturn(user);
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));

        // Act
        List<Order> result = orderService.getMyOrders();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Doit récupérer une commande par son ID")
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Doit lancer une exception si la commande n'existe pas")
    void getOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Order not found");
    }
}
