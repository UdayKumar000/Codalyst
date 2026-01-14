package com.company.demo.webclient;

import com.google.common.net.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ConfigWebClient {

    @Bean
    public static WebClient webClient(){
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                                configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)
                        // OR: -1 for unlimited (not recommended)
                        // configurer.defaultCodecs().maxInMemorySize(-1)
                )
                .build();
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl("https://api.heygen.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer sk_V2_hgu_kBU3lBwZ0hD_zNLRGu5nbRzWp2FYaymOhmNAT3PTpHOy")
                .build();
    }
}
