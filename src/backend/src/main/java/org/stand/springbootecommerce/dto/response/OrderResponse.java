package org.stand.springbootecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.stand.springbootecommerce.entity.user.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Order details")
public class OrderResponse {
    @Schema(description = "Order identifier", example = "101")
    private Long id;
    @Schema(description = "Total order amount", example = "159.98")
    private BigDecimal totalAmount;
    @Schema(description = "Current order status", example = "PENDING")
    private OrderStatus status;
    @Schema(description = "Creation date", example = "2023-10-27T10:15:30")
    private LocalDateTime createdAt;
    @Schema(description = "Number of products in the order", example = "2")
    private int numberOfProducts;
}
