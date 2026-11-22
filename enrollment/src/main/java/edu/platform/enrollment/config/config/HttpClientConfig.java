package edu.platform.enrollment.config;

import edu.platform.enrollment.client.CatalogClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public CatalogClient catalogClient(WebClient.Builder webClientBuilder,
                                       @Value("${catalog.base-url}") String baseUrl) {
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
        return factory.createClient(CatalogClient.class);
    }
}