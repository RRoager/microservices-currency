package com.roager.microservices.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/get")
                        .filters(f -> f
                                .addRequestHeader("MyHeader", "MyURI")
                                .addRequestParameter("Param", "MyValue"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/currency-exchange/**") // If a request starts with this path
                        .uri("lb://currency-exchange")) // It will be redirected to this specific URL (based on the name registered in Eureka) with load balancing (lb)
                .route(p -> p.path("/currency-conversion/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-feign/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-new/**")
                        .filters(f -> f.rewritePath(
                                "/currency-conversion-new/(?<segment>.*)", // Take everything that follows /currency-conversion-new
                                "/currency-conversion-feign/${segment}")) // and append to /currency-conversion-feign
                        .uri("lb://currency-conversion"))
                .build();
    }

    //http://localhost:8765/currency-exchange/from/USD/to/INR
    //http://localhost:8765/currency-conversion/currency-conversion/from/USD/to/INR/quantity/10
    //http://localhost:8765/currency-conversion/currency-conversion-feign/from/USD/to/INR/quantity/10

}
