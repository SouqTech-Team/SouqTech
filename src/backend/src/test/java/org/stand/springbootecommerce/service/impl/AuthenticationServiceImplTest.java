package org.stand.springbootecommerce.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de AuthenticationServiceImpl")
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Ahmed")
                .surname("Ben")
                .email("ahmed@email.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .email("ahmed@email.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("Doit enregistrer un nouvel utilisateur et retourner un token")
    void register_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Success");

        // Act
        var response = authenticationService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Doit authentifier un utilisateur et retourner un JWT")
    void authenticate_Success() {
        // Arrange
        org.stand.springbootecommerce.dto.request.AuthenticationRequest authRequest = new org.stand.springbootecommerce.dto.request.AuthenticationRequest(
                "ahmed@email.com", "password123");

        when(userRepository.findByEmail("ahmed@email.com")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Act
        var response = authenticationService.authenticate(authRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(
                any(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Doit lancer une exception si les credentials sont incorrects")
    void authenticate_BadCredentials() {
        // Arrange
        org.stand.springbootecommerce.dto.request.AuthenticationRequest authRequest = new org.stand.springbootecommerce.dto.request.AuthenticationRequest(
                "ahmed@email.com", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // Act & Assert
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> authenticationService.authenticate(authRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);
    }

    @Test
    @DisplayName("Doit retourner l'utilisateur connecté via me()")
    void me_Success() {
        // Arrange
        org.springframework.security.core.Authentication authentication = mock(
                org.springframework.security.core.Authentication.class);
        org.springframework.security.core.context.SecurityContext securityContext = mock(
                org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("ahmed@email.com");

        try (org.mockito.MockedStatic<org.springframework.security.core.context.SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                org.springframework.security.core.context.SecurityContextHolder.class)) {
            mockedSecurityContextHolder
                    .when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findByEmail("ahmed@email.com")).thenReturn(java.util.Optional.of(user));

            // Act
            User result = authenticationService.me();

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("ahmed@email.com");
        }
    }

    @Test
    @DisplayName("Doit lancer une exception si l'utilisateur n'est pas authentifié")
    void me_NotAuthenticated() {
        // Arrange
        org.springframework.security.core.context.SecurityContext securityContext = mock(
                org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        try (org.mockito.MockedStatic<org.springframework.security.core.context.SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                org.springframework.security.core.context.SecurityContextHolder.class)) {
            mockedSecurityContextHolder
                    .when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            // Act & Assert
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> authenticationService.me())
                    .isInstanceOf(org.stand.springbootecommerce.error.UserNotAuthenticatedException.class);
        }
    }
}
