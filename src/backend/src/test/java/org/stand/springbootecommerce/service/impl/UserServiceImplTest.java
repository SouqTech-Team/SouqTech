package org.stand.springbootecommerce.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.error.UserNotFoundException;
import org.stand.springbootecommerce.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires de UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@test.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("Doit récupérer un utilisateur par email")
    void getUserByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByEmail("john@test.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@test.com");
        verify(userRepository, times(1)).findByEmail("john@test.com");
    }

    @Test
    @DisplayName("Doit lancer une exception si l'email n'existe pas")
    void getUserByEmail_NotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserByEmail("unknown@test.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Doit mettre à jour un utilisateur connecté")
    void updateUser_Success() {
        // Arrange
        org.stand.springbootecommerce.dto.request.UserUpdateRequest updateRequest = new org.stand.springbootecommerce.dto.request.UserUpdateRequest();
        updateRequest.setName("JohnUpdated");
        updateRequest.setSurname("DoeUpdated");

        org.springframework.security.core.Authentication authentication = mock(
                org.springframework.security.core.Authentication.class);
        org.springframework.security.core.context.SecurityContext securityContext = mock(
                org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john@test.com");

        try (org.mockito.MockedStatic<org.springframework.security.core.context.SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
                org.springframework.security.core.context.SecurityContextHolder.class)) {
            mockedSecurityContextHolder
                    .when(org.springframework.security.core.context.SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

            // Act
            User result = userService.updateUser(updateRequest);

            // Assert
            assertThat(result.getName()).isEqualTo("JohnUpdated");
            verify(userRepository).save(user);
        }
    }

    @Test
    @DisplayName("Doit lancer une exception si l'utilisateur n'est pas authentifié lors de la mise à jour")
    void updateUser_NotAuthenticated() {
         org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
         when(securityContext.getAuthentication()).thenReturn(null);

         try (org.mockito.MockedStatic<org.springframework.security.core.context.SecurityContextHolder> mockedSecurityContextHolder = mockStatic(org.springframework.security.core.context.SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(org.springframework.security.core.context.SecurityContextHolder::getContext).thenReturn(securityContext);
            
            org.stand.springbootecommerce.dto.request.UserUpdateRequest updateRequest = new org.stand.springbootecommerce.dto.request.UserUpdateRequest();

            assertThatThrownBy(() -> userService.updateUser(updateRequest))
                    .isInstanceOf(org.stand.springbootecommerce.error.UserNotAuthenticatedException.class);
        }
    }
}
