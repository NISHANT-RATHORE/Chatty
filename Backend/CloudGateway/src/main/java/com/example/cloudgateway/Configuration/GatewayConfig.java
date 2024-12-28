package com.example.cloudgateway.Configuration;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory) {
        return builder.routes()
                .route("user-service", r -> r.path("/user/**")
                        .uri("lb://user-service"))
                .route("chat-service", r -> r.path("/messages/**")
                        .filters(f -> f.filter(handleInfoEndpoint()))
                        .uri("lb://chat-service"))
                .build();
    }

    private GatewayFilter handleInfoEndpoint() {
        return (exchange, chain) -> {
            if (exchange.getRequest().getURI().getPath().contains("/info")) {
                return handleInfoRequest(exchange);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleInfoRequest(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().setComplete();
    }
}