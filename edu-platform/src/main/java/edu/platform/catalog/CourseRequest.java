package edu.platform.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CourseRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 2000) String summary,
        @PositiveOrZero(message = "Price cannot be negative") BigDecimal price
) {}