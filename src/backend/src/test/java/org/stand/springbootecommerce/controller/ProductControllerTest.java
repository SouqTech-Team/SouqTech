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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.stand.springbootecommerce.dto.request.ProductRequest;
import org.stand.springbootecommerce.dto.response.ProductResponse;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.service.JwtService;
import org.stand.springbootecommerce.service.ProductService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests du contrôleur ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductResponse productResponse;
    private Map<String, Object> productRequestMap;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Smartphone")
                .description("Detailed description")
                .shortDescription("Short desc")
                .price(BigDecimal.valueOf(699.00))
                .quantity(5)
                .image("http://image.url")
                .build();

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Smartphone");

        // Utilisation d'une Map pour simuler ProductRequest afin de contrôler
        // facilement les champs
        productRequestMap = new HashMap<>();
        productRequestMap.put("name", "Smartphone");
        productRequestMap.put("description", "Detailed description");
        productRequestMap.put("shortDescription", "Short desc");
        productRequestMap.put("price", 699.00);
        productRequestMap.put("quantity", 5);
        productRequestMap.put("image", "http://image.url/test.jpg");
        productRequestMap.put("category", 1L);
    }

    @Test
    @DisplayName("GET /api/v1/product - Doit retourner la liste des produits paginée")
    void getProducts_Success() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 10), 1);

        when(productService.getProducts(nullable(String.class), anyInt(), anyInt())).thenReturn(productPage);
        when(modelMapper.map(any(), eq(ProductResponse.class))).thenReturn(productResponse);

        mockMvc.perform(get("/api/v1/product")
                .param("pageNumber", "0")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.list[0].name").value("Smartphone"));
    }

    @Test
    @DisplayName("GET /api/v1/product/{id} - Doit retourner un produit")
    void getProductById_Success() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);
        when(modelMapper.map(any(), eq(ProductResponse.class))).thenReturn(productResponse);

        mockMvc.perform(get("/api/v1/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smartphone"));
    }

    @Test
    @DisplayName("POST /api/v1/product - Doit créer un produit")
    void addProduct_Success() throws Exception {
        when(modelMapper.map(any(ProductRequest.class), eq(Product.class))).thenReturn(product);
        when(productService.addProduct(any(Product.class))).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductResponse.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequestMap)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Smartphone"));
    }
}
