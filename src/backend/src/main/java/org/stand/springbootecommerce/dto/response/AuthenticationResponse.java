package org.stand.springbootecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response containing the JWT token")
public class AuthenticationResponse {
    @Schema(description = "JWT token to be used for authenticated requests", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
