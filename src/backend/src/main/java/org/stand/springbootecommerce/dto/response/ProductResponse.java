package org.stand.springbootecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.stand.springbootecommerce.dto.BaseProductDTO;

@Getter
@Setter
@Schema(description = "Object representing a catalog product")
public class ProductResponse extends BaseProductDTO {

    @Schema(description = "Unique identifier of the product", example = "1")
    @NotNull
    private Long id;

    @Schema(description = "Identifier of the parent category", example = "10")
    @NotNull
    private Long categoryId;
}