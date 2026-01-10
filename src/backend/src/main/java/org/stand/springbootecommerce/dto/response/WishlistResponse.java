package org.stand.springbootecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Set;

@Data
@Schema(description = "Object representing a user's wishlist")
public class WishlistResponse {
    @Schema(description = "Wishlist identifier", example = "10")
    private Long id;
    @Schema(description = "List of products in the wishlist")
    private Set<ProductResponse> products;
    @Schema(description = "Indicates if the wishlist is public/shared", example = "true")
    private Boolean isPublic;
    @Schema(description = "Unique sharing token", example = "abc-123-token")
    private String shareToken;
}
