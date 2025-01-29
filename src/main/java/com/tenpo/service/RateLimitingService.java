package com.tenpo.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RateLimitingService {

    private final RateLimiter rateLimiter;

    public RateLimitingService(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public RateLimitingService() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(3)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);
        this.rateLimiter = rateLimiterRegistry.rateLimiter("apiRateLimiter");
    }

    public Mono<Boolean> tryConsume() {
        return Mono.fromCallable(rateLimiter::acquirePermission);
    }
}
