package org.stand.springbootecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Object representing a customer review")
public class ReviewResponse {
    @Schema(description = "Review identifier", example = "50")
    private Long id;
    @Schema(description = "Rating from 1 to 5", example = "5")
    private Integer rating;
    @Schema(description = "User comment", example = "Great product!")
    private String comment;
    @Schema(description = "Author name", example = "John Doe")
    private String userName;
    @Schema(description = "Publication date", example = "2023-11-01T14:20:00")
    private LocalDateTime createdAt;
    @Schema(description = "Number of 'Helpful' votes", example = "12")
    private Integer helpfulCount;
}
