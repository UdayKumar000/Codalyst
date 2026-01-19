package com.company.demo.webclient;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
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

}
