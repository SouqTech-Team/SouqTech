package org.stand.springbootecommerce.error.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.stand.springbootecommerce.service.ProductCategoryService;
import org.stand.springbootecommerce.filter.JwtAuthenticationFilter;
import org.stand.springbootecommerce.service.JwtService;

import java.util.Locale;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests d'intégration du Handler d'exceptions")
class RestResponseEntityExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private ProductCategoryService productCategoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Devrait gérer BadCredentialsException avec un message localisé")
    void handleBadCredentialsException() throws Exception {
        when(productCategoryService.getProductCategories()).thenThrow(new BadCredentialsException("Bad credentials"));

        String expectedMessage = messageSource.getMessage("user.authentication.error", null, Locale.getDefault());

        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("Devrait gérer les exceptions génériques")
    void handleGenericException() throws Exception {
        when(productCategoryService.getProductCategories()).thenThrow(new RuntimeException("Generic error"));

        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Generic error"));
    }
}
