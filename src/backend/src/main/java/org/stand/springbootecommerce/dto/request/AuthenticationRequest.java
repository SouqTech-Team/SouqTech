package org.stand.springbootecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication request")
public class AuthenticationRequest {
    @Schema(description = "User's email address", example = "john.doe@email.com")
    @NotBlank
    private String email;
    @Schema(description = "User password", example = "password123")
    @NotBlank
    private String password;
}
