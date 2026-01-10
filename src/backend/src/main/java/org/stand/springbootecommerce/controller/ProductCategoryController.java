package org.stand.springbootecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.service.ProductCategoryService;

import java.util.List;

@Tag(name = "Categories", description = "Product category management")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/category")
public class ProductCategoryController {

        private final ProductCategoryService productCategoryService;

        @Operation(summary = "List all categories")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success")
        })
        @GetMapping
        public ResponseEntity<List<ProductCategory>> getProductCategories() {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(productCategoryService.getProductCategories());
        }

        @Operation(summary = "Get category by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category found"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductCategory> getProductCategoryById(@PathVariable(name = "id") Long id) {
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body((productCategoryService.getProductCategoryById(id)));
        }

        @Operation(summary = "Add a new category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Category created"),
                        @ApiResponse(responseCode = "400", description = "Invalid data")
        })
        @PostMapping
        public ResponseEntity<ProductCategory> saveProductCategory(
                        @Valid @RequestBody ProductCategory productCategory) {
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(productCategoryService.addProductCategory(productCategory));
        }

}