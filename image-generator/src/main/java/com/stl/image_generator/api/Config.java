package com.stl.image_generator.api;

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;


@Configuration
public class Config {

    @Value("${spring.ai.openai.api-key:-forTesting}")
    private String OPENAI_API_KEY;

//    // You can inject these values from application.properties or application.yml
//    @Value("${openai.api.url}")
    private static final String DEFAULT_BASE_URL = "https://api.openai.com";


    @Bean
    public ImageClient imageClient (){
        return new OpenAiImageClient(new OpenAiImageApi(OPENAI_API_KEY));
    }

    @Bean
    public WebClient webClient(){

        // Connection pooling configuration
        PoolingAsyncClientConnectionManager connectionManager = new PoolingAsyncClientConnectionManager();
        connectionManager.setMaxTotal(50); // Max total connections
        connectionManager.setDefaultMaxPerRoute(20); // Max connections per route
        connectionManager.closeIdle(TimeValue.ofSeconds(30)); // Close idle connections after 30 seconds

        // Asynchronous HttpClient with connection pooling and idle connection eviction
        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(TimeValue.ofSeconds(30)) // Automatically evict idle connections
                .evictExpiredConnections() // Automatically evict expired connections
                .build();

        // Use HttpComponentsClientHttpConnector to configure WebClient with Apache HttpClient 5
        HttpComponentsClientHttpConnector httpConnector = new HttpComponentsClientHttpConnector(httpAsyncClient);

        // Increase max in-memory buffer size for large payloads
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)) // 20MB
                .build();

        // Build and return WebClient with the custom HttpClient and configurations
        return WebClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + OPENAI_API_KEY)
                .exchangeStrategies(strategies)
                .clientConnector(httpConnector)
                .build();

//        ExchangeStrategies strategies = ExchangeStrategies.builder()
//                .codecs(configurer -> configurer.defaultCodecs()
//                        .maxInMemorySize(20 * 1024 * 1024)) // Increase to 20MB (or more as needed)
//                .build();
//        return WebClient.builder()
//                .baseUrl(DEFAULT_BASE_URL)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + OPENAI_API_KEY)
//                .exchangeStrategies(strategies)
//                .build();
    }
}
