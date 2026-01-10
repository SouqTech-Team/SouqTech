package org.stand.springbootecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class BaseProductDTO {

    @Schema(description = "Product name", example = "Samsung Galaxy S21 Smartphone")
    @NotBlank
    @Size(max = 80)
    private String name;

    @Schema(description = "Detailed product description", example = "A high-end smartphone with a triple camera sensor.")
    @NotBlank
    @Size(max = 255)
    private String description;

    @Schema(description = "Short description for product lists", example = "The latest Samsung flagship")
    @NotBlank
    @Size(max = 80)
    private String shortDescription;

    @Schema(description = "Quantity in stock", example = "50")
    @NotNull
    @PositiveOrZero
    private int quantity = 0;

    @Schema(description = "Product unit price", example = "799.99")
    @NotNull
    @Positive
    private BigDecimal price;

    @Schema(description = "Product image URL", example = "https://example.com/images/s21.jpg")
    @NotBlank
    @Size(max = 255)
    // @Pattern(regexp = "^https?://.*\\.(png|jpg|jpeg)$")
    private String image;
}
