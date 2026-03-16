package com.company.codelyst.config;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class CorsFilter {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter doFilter() {
        return (request, response, chain) -> {

            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            res.setHeader(
                    "Access-Control-Allow-Origin",
                    "https://codelyst.onrender.com"
            );
            res.setHeader(
                    "Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS"
            );
            res.setHeader(
                    "Access-Control-Allow-Headers",
                    "Content-Type, Authorization"
            );
            res.setHeader(
                    "Access-Control-Allow-Credentials",
                    "false"
            );

            // VERY IMPORTANT
            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                res.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            chain.doFilter(request, response);
        };
    }
}
