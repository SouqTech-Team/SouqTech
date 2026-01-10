package org.stand.springbootecommerce.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stand.springbootecommerce.entity.user.Order;
import org.stand.springbootecommerce.entity.user.OrderStatus;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.repository.OrderRepository;
import org.stand.springbootecommerce.repository.ProductRepository;
import org.stand.springbootecommerce.service.AuthenticationService;
import org.stand.springbootecommerce.service.OrderService;
import org.stand.springbootecommerce.error.BaseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuthenticationService authenticationService;

    @Override
    public Order createOrder(List<Long> productIds) {
        User user;
        try {
            user = authenticationService.me();
        } catch (Exception e) {
            throw new BaseException("User must be logged in to order");
        }

        List<Product> products = productRepository.findAllById(Objects.requireNonNull(productIds));
        if (products.isEmpty()) {
            throw new IllegalArgumentException("Cannot create order with no valid products");
        }

        BigDecimal total = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .products(products)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.save(Objects.requireNonNull(order));
    }

    @Override
    public List<Order> getMyOrders() {
        try {
            User user = authenticationService.me();
            return orderRepository.findByUserId(user.getId());
        } catch (Exception e) {
            throw new BaseException("Error fetching orders");
        }
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
    }
}
