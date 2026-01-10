package org.stand.springbootecommerce.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.stand.springbootecommerce.entity.user.Order;
import org.stand.springbootecommerce.entity.user.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du repository OrderRepository")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        // Créer un utilisateur
        user = User.builder()
                .email("test@example.com")
                .name("Test")
                .surname("User")
                .password("password")
                .build();
        entityManager.persist(user);

        // Créer une commande
        order = Order.builder()
                .user(user)
                .totalAmount(new java.math.BigDecimal("250.0"))
                .build();
        entityManager.persist(order);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver les commandes par ID d'utilisateur")
    void findByUserId_Success() {
        List<Order> orders = orderRepository.findByUserId(user.getId());

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(orders.get(0).getTotalAmount()).isEqualByComparingTo(new java.math.BigDecimal("250.0"));
    }

    @Test
    @DisplayName("Doit retourner une liste vide si l'utilisateur n'a pas de commandes")
    void findByUserId_NotFound() {
        List<Order> orders = orderRepository.findByUserId(999L);

        assertThat(orders).isEmpty();
    }

    @Test
    @DisplayName("Doit sauvegarder une nouvelle commande")
    void save_Success() {
        Order newOrder = Order.builder()
                .user(user)
                .totalAmount(new java.math.BigDecimal("500.0"))
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new java.math.BigDecimal("500.0"));
    }
}
