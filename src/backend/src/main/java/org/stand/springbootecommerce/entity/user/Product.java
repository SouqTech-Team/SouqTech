package org.stand.springbootecommerce.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotBlank
    @Size(max = 80)
    @Column(name = "short_description")
    private String shortDescription;

    @Builder.Default
    @NotNull
    @PositiveOrZero
    @Column(name = "quantity")
    private int quantity = 0;

    @NotNull
    @Positive
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank
    @Size(max = 255)
    // @Pattern(regexp = "^https?://.*\\.(png|jpg|jpeg)$")
    @Column(name = "image")
    private String image;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.EAGER
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Product product = (Product) o;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}