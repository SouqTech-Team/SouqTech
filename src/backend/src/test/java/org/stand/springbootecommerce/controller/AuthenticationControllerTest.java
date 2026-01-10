package org.stand.springbootecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.dto.UserDTO;
import org.stand.springbootecommerce.dto.request.RegisterRequest;
import org.stand.springbootecommerce.dto.response.BaseResponseBody;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.service.AuthenticationService;
import org.stand.springbootecommerce.service.JwtService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur AuthenticationController")
class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthenticationService authenticationService;

        @MockBean
        private ModelMapper modelMapper;

        @MockBean
        private JwtService jwtService;

        @Autowired
        private ObjectMapper objectMapper;

        private Map<String, Object> registerRequestMap;
        private Map<String, Object> authRequestMap;
        private User user;
        private UserDTO userDTO;

        @BeforeEach
        void setUp() {
                registerRequestMap = new HashMap<>();
                registerRequestMap.put("name", "John");
                registerRequestMap.put("surname", "Doe");
                registerRequestMap.put("email", "john@test.com");
                registerRequestMap.put("password", "password123");

                authRequestMap = new HashMap<>();
                authRequestMap.put("email", "john@test.com");
                authRequestMap.put("password", "password123");

                user = User.builder()
                                .id(1L)
                                .name("John")
                                .surname("Doe")
                                .email("john@test.com")
                                .build();

                userDTO = new UserDTO();
                userDTO.setEmail("john@test.com");
        }

        @Test
        @DisplayName("POST /api/v1/auth/register - Doit créer un utilisateur avec succès")
        void register_Success() throws Exception {
                // Arrange
                BaseResponseBody response = new BaseResponseBody("User registered successfully");
                when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequestMap)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value("User registered successfully"));
        }

        @Test
        @DisplayName("GET /api/v1/auth/me - Doit retourner les informations de l'utilisateur connecté")
        void me_Success() throws Exception {
                // Arrange
                when(authenticationService.me()).thenReturn(user);
                when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(userDTO);

                // Act & Assert
                mockMvc.perform(get("/api/v1/auth/me"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("john@test.com"));
        }
}
