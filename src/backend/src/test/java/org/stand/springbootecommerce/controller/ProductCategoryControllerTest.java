package org.stand.springbootecommerce.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.service.ProductCategoryService;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur ProductCategoryController")
class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCategoryService categoryService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtService jwtService;

    private ProductCategory category;

    @BeforeEach
    void setUp() {
        category = ProductCategory.builder()
                .id(1L)
                .name("Electronics")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/product-category - Doit retourner toutes les catégories")
    void getProductCategories_Success() throws Exception {
        // Arrange
        when(categoryService.getProductCategories()).thenReturn(Arrays.asList(category));

        // Act & Assert
        mockMvc.perform(get("/api/v1/category"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/category/{id} - Doit retourner une catégorie par ID")
    void getProductCategoryById_Success() throws Exception {
        // Arrange
        when(categoryService.getProductCategoryById(1L)).thenReturn(category);

        // Act & Assert
        mockMvc.perform(get("/api/v1/category/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/category - Doit créer une nouvelle catégorie")
    void addProductCategory_Success() throws Exception {
        // Arrange
        when(categoryService.addProductCategory(org.mockito.ArgumentMatchers.any(ProductCategory.class)))
                .thenReturn(category);

        String categoryJson = "{\"name\":\"Electronics\",\"description\":\"Electronic devices\"}";

        // Act & Assert
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/category")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated());
    }
}
