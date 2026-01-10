package org.stand.springbootecommerce.utiil;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Tests unitaires de JwtUtils")
class JwtUtilsTest {

    @Test
    @DisplayName("Devrait extraire le token JWT de l'en-tête Authorization")
    void getJwtFromRequest_ShouldReturnToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer my-secret-token");

        String token = JwtUtils.getJwtFromRequest(request);

        assertThat(token).isEqualTo("my-secret-token");
    }

    @Test
    @DisplayName("Devrait retourner null si l'en-tête ne commence pas par Bearer")
    void getJwtFromRequest_ShouldReturnNull_WhenNoBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Basic user:pass");

        String token = JwtUtils.getJwtFromRequest(request);

        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Devrait retourner null si l'en-tête est absent")
    void getJwtFromRequest_ShouldReturnNull_WhenHeaderMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        String token = JwtUtils.getJwtFromRequest(request);

        assertThat(token).isNull();
    }
}
