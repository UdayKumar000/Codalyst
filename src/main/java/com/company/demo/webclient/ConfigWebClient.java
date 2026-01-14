package com.company.demo.webclient;

import com.google.api.client.util.Value;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ConfigWebClient {

    @Value("${heygen.api.base-url}")
    private String heygenApiBaseUrl;

    @Value("${heygen.api.key}")
    private String heygenApiKey;

//    @Value("${gemini.api.base-url}")
//    private String geminiBaseUrl;

//    @Value("${gemini.api.key}")
//    private String geminiApiKey;

    @Bean("heyGenWebClient")
    public WebClient heyGenWebClient(){
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                                configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)
                        // OR: -1 for unlimited (not recommended)
                        // configurer.defaultCodecs().maxInMemorySize(-1)
                )
                .build();
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(heygenApiBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+heygenApiKey)
                .build();
    }

//    @Bean("geminiWebClient")
//    public WebClient geminiWebClient(){
//        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
//                .codecs(configurer ->
//                                configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)
//                        // OR: -1 for unlimited (not recommended)
//                        // configurer.defaultCodecs().maxInMemorySize(-1)
//                )
//                .build();
//        return WebClient.builder()
//                .exchangeStrategies(exchangeStrategies)
//                .baseUrl(geminiBaseUrl)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+geminiApiKey)
//                .build();
//
//    }
}
