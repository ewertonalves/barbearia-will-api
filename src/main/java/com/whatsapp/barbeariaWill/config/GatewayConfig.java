package com.whatsapp.barbeariaWill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.*;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator rotas(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("rota_webhook", r -> r.path("/webhook/**").uri("http://localhost:8080"))
                .build();
    }
}
