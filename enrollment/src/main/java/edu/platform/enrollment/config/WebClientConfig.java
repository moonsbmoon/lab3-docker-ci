package edu.platform.enrollment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient catalogWebClient(WebClient.Builder builder, CatalogProperties props) {
        return builder.baseUrl(props.baseUrl()).build();
    }
}