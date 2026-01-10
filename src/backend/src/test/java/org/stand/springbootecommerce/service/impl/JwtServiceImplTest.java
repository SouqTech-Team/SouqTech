package org.stand.springbootecommerce.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.stand.springbootecommerce.entity.user.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de JwtServiceImpl")
class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        // Injecter une clé secrète de test
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        // Injecter une durée d'expiration de 1 heure (3600000 ms)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test")
                .surname("User")
                .build();
    }

    @Test
    @DisplayName("Doit générer un token JWT valide")
    void generateToken_Success() {
        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertAll("Token validation",
                () -> assertThat(token).isNotNull(),
                () -> assertThat(token).isNotEmpty(),
                () -> assertThat(token.split("\\.")).hasSize(3) // Format JWT: header.payload.signature
        );
    }

    @Test
    @DisplayName("Doit extraire l'email du token")
    void extractUsername_Success() {
        // Arrange
        String token = jwtService.generateToken(user);

        // Act
        String email = jwtService.extractUsername(token);

        // Assert
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Doit valider un token correct")
    void isTokenValid_ValidToken() {
        // Arrange
        String token = jwtService.generateToken(user);

        // Act
        boolean isValid = jwtService.isTokenValid(token, user);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Doit invalider un token avec un mauvais utilisateur")
    void isTokenValid_WrongUser() {
        // Arrange
        String token = jwtService.generateToken(user);
        User differentUser = User.builder()
                .email("different@example.com")
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertThat(isValid).isFalse();
    }
}
