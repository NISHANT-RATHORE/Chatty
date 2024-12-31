package com.example.cloudgateway.Configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/user/**")
                        .uri("lb://USER-SERVICE/"))

                .route(r -> r.path("/websocket/**")
                        .uri("lb://NOTIFICATION-SERVICE/"))

                .route(r -> r.path("/messages/**")
                        .uri("lb://CHAT-SERVICE/"))
                .build();
    }


}