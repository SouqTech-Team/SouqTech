package org.stand.springbootecommerce.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.stand.springbootecommerce.entity.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Slice Test - Persistence (DataJpaTest)")
class UserRepositorySliceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Devrait sauvegarder et retrouver un utilisateur par son email")
    void findByEmail_ShouldReturnUser() {
        // Arrange
        User user = User.builder()
                .name("Alice")
                .surname("Smith")
                .email("alice@test.com")
                .password("encoded_pass")
                .build();
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("alice@test.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }
}
