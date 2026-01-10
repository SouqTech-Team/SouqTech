package org.stand.springbootecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.dto.request.ProductRequest;
import org.stand.springbootecommerce.dto.response.PageableResponse;
import org.stand.springbootecommerce.dto.response.ProductResponse;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.service.ProductService;

@Tag(name = "Products", description = "Product catalog management")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/product")
public class ProductController {
        private final ProductService productService;
        private final ModelMapper modelMapper;

        @Operation(summary = "Get list of products", description = "Returns a paginated list of products with optional search query.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List retrieved successfully")
        })
        @GetMapping
        public ResponseEntity<PageableResponse<ProductResponse>> getProducts(
                        @RequestParam(name = "q", required = false) String query,
                        @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
                        @RequestParam(name = "pageSize", required = true) Integer pageSize)
                        throws InterruptedException {
                Page<Product> productPage = productService.getProducts(query, pageNumber, pageSize);
                PageableResponse<ProductResponse> pageableResponse = new PageableResponse<>(
                                productPage.getTotalElements(),
                                productPage.getContent().stream()
                                                .map(product -> modelMapper.map(product, ProductResponse.class))
                                                .toList());
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(pageableResponse);
        }

        @Operation(summary = "Add a new product", description = "Creates a new product in the catalog.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Product created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data")
        })
        @PostMapping
        public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest productRequest) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(
                                                modelMapper.map(
                                                                productService.addProduct(
                                                                                modelMapper.map(productRequest,
                                                                                                Product.class)),
                                                                ProductResponse.class));
        }

        @Operation(summary = "Get product by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found"),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponse> getProductById(@PathVariable(name = "id") Long id) {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(
                                                modelMapper.map(productService.getProductById(id),
                                                                ProductResponse.class));
        }

}