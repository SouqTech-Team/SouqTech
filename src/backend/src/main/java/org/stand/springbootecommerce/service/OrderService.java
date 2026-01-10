package org.stand.springbootecommerce.service;

import org.stand.springbootecommerce.entity.user.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(List<Long> productIds);

    List<Order> getMyOrders();

    Order getOrderById(Long id);
}
