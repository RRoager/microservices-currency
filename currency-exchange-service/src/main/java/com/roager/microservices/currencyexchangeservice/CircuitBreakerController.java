package com.roager.microservices.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @GetMapping("/retry-sample-api")
    // Using Resilience4j @Retry to retry the call (3 times if name = default) if failure occurs
    // fallbackMethod makes it possible to create a tailor made fallback response
    @Retry(name = "retry-sample-api", fallbackMethod = "hardcodedResponse")
    public String retrySampleApi() {
        logger.info("Retry Sample API call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
                String.class);

        return forEntity.getBody();
    }

    @GetMapping("/circuit-breaker-sample-api")
    // If down @CircuitBreaker breaks the circuit and directly returns a response back
    @CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
    // Sets the limit for how many calls are allowed to the API in a specified time
    @RateLimiter(name = "default")
    // Sets how many concurrent calls are allowed
    @Bulkhead(name = "default")
    public String CircuitBreakerSampleApi() {
        logger.info("Circuit Breaker Sample API call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
                String.class);

        return forEntity.getBody();
    }

    // Fallback method
    public String hardcodedResponse(Exception ex) {
        return "fallback-response";
    }
}
