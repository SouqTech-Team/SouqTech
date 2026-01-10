package org.stand.springbootecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.annotation.DirtiesContext;
import org.stand.springbootecommerce.dto.request.AuthenticationRequest;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.AuthenticationResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests d'Intégration - Authentification")
class AuthenticationIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Flux Complet : Inscription -> Login -> Accès Profil")
        void fullAuthFlow_Success() throws Exception {
                // 1. Inscription
                RegisterRequest registerRequest = RegisterRequest.builder()
                                .name("Integration")
                                .surname("Test")
                                .email("it@test.com")
                                .password("password123")
                                .build();

                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").exists());

                // 2. Connexion
                AuthenticationRequest loginRequest = AuthenticationRequest.builder()
                                .email("it@test.com")
                                .password("password123")
                                .build();

                MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String responseBody = loginResult.getResponse().getContentAsString();
                AuthenticationResponse authResponse = objectMapper.readValue(responseBody,
                                AuthenticationResponse.class);
                String token = authResponse.getToken();

                // 3. Accès au profil (avec le token JWT)
                mockMvc.perform(get("/api/v1/auth/me")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("it@test.com"))
                                .andExpect(jsonPath("$.name").value("integration"));
        }

        @Test
        @DisplayName("Connexion échouée avec mauvais mot de passe")
        void login_BadCredentials_Failure() throws Exception {
                AuthenticationRequest badRequest = AuthenticationRequest.builder()
                                .email("nonexistent@test.com")
                                .password("wrong")
                                .build();

                mockMvc.perform(post("/api/v1/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(badRequest)))
                                .andExpect(status().isBadRequest());
        }
}
