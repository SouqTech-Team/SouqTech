package org.stand.springbootecommerce.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.service.ProductCategoryService;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.filter.JwtAuthenticationFilter;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductCategoryController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@DisplayName("Slice Test - Controller (WebMvcTest)")
class ProductCategoryControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCategoryService productCategoryService;

    // Ces Mocks sont nécessaires car SecurityConfig est chargé par WebMvcTest
    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Devrait retourner la liste des catégories sans charger tout le contexte")
    void getProductCategories_ShouldReturnList() throws Exception {
        ProductCategory cat = ProductCategory.builder().id(1L).name("Books").build();
        when(productCategoryService.getProductCategories()).thenReturn(Arrays.asList(cat));

        mockMvc.perform(get("/api/v1/category")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Books"));
    }
}
