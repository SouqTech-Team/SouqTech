package org.stand.springbootecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.entity.user.Order;
import org.stand.springbootecommerce.service.OrderService;
import org.stand.springbootecommerce.dto.response.OrderResponse;

import java.util.List;

@Tag(name = "Orders", description = "Customer order management")
@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place a new order", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody List<Long> productIds) {
        Order order = orderService.createOrder(productIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(order));
    }

    @Operation(summary = "Get my order history", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders().stream()
                .map(this::mapToResponse)
                .toList());
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .numberOfProducts(order.getProducts().size())
                .build();
    }
}
