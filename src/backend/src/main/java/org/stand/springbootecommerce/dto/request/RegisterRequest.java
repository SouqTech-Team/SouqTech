package org.stand.springbootecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "New user registration request")
public class RegisterRequest {
    @Schema(description = "User's first name", example = "John")
    @NotBlank
    @Size(max = 16)
    private String name;
    @Schema(description = "User's last name", example = "Doe")
    @NotBlank
    @Size(max = 16)
    private String surname;
    @Schema(description = "Email address used as identifier", example = "john.doe@email.com")
    @NotBlank
    @Size(max = 48)
    @Email
    private String email;
    @Schema(description = "User password", example = "password123")
    @NotBlank
    @Size(max = 48)
    private String password;
}
