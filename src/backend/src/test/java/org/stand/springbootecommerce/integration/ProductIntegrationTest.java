package org.stand.springbootecommerce.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Tests d'Intégration - Catalogue Produits")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Lister toutes les catégories")
    void getAllCategories_Success() throws Exception {
        mockMvc.perform(get("/api/v1/category")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Lister les produits avec pagination")
    void getAllProducts_Paged_Success() throws Exception {
        mockMvc.perform(get("/api/v1/product")
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray());
    }

    @Test
    @DisplayName("Rechercher un produit inexistant")
    void searchProduct_NotFound_EmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/product")
                .param("q", "NonExistentProduct")
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isEmpty());
    }
}
