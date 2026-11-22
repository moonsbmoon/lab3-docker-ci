package edu.platform.enrollment.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "catalog")
public record CatalogProperties(
        @NotBlank(message = "Catalog URL is required")
        @Pattern(regexp = "^https?://.*", message = "Catalog URL must start with http:// or https://")
        String baseUrl
) {}