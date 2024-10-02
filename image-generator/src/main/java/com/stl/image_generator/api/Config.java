package com.stl.image_generator.api;

import org.springframework.ai.image.ImageClient;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

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
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(20 * 1024 * 1024)) // Increase to 20MB (or more as needed)
                .build();
        return WebClient.builder()
                .baseUrl(DEFAULT_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + OPENAI_API_KEY)
                .exchangeStrategies(strategies)
                .build();
    }
}
