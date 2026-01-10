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
import org.stand.springbootecommerce.dto.request.UserUpdateRequest;
import org.stand.springbootecommerce.entity.user.User;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@test.com")
                .build();
    }

    @Test
    @DisplayName("PATCH /api/v1/user - Doit mettre à jour l'utilisateur")
    void updateUser_Success() throws Exception {
        // Arrange
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("name", "Jane");
        updateMap.put("surname", "Smith");

        when(userService.updateUser(any(UserUpdateRequest.class))).thenReturn(user);
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(new UserDTO());

        // Act & Assert
        mockMvc.perform(patch("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isCreated());
    }
}
