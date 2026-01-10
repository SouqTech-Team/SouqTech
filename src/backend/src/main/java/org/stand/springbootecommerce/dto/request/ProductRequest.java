package org.stand.springbootecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.stand.springbootecommerce.dto.BaseProductDTO;

@Getter
@Setter
@Schema(description = "Product creation request")
public class ProductRequest extends BaseProductDTO {

    @Schema(description = "Identifier of the parent category", example = "10")
    @NotNull
    private Long category;
}