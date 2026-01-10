package org.stand.springbootecommerce.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "category")
@Schema(description = "Product category")
public class ProductCategory {
    @Schema(description = "Category identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Schema(description = "Category name", example = "Electronics")
    @NotBlank
    @Size(max = 80)
    @Column(name = "name", unique = true)
    private String name;

    @Schema(description = "Category description", example = "High-tech products and gadgets")
    @NotBlank
    @Size(max = 255)
    @Column(name = "description")
    private String description;
}