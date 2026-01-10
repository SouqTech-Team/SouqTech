package org.stand.springbootecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User profile update request")
public class UserUpdateRequest {
    @Schema(description = "New first name", example = "John")
    private String name;
    @Schema(description = "New last name", example = "Doe")
    private String surname;
}