package org.stand.springbootecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "User information")
public class UserDTO {
    @Schema(description = "First name", example = "John")
    private String name;
    @Schema(description = "Last name", example = "Doe")
    private String surname;

    @Schema(description = "Email address", example = "john.doe@email.com")
    private String email;
}