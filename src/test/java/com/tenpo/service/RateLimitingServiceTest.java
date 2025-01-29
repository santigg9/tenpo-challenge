package com.tenpo.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitingServiceTest {

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private RateLimitingService rateLimitingService;

    @Test
    public void testTryConsume_Success() {
        when(rateLimiter.acquirePermission()).thenReturn(true);

        Mono<Boolean> result = rateLimitingService.tryConsume();

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(rateLimiter, times(1)).acquirePermission();
    }

    @Test
    public void testTryConsume_Failure() {
        when(rateLimiter.acquirePermission()).thenReturn(false);

        Mono<Boolean> result = rateLimitingService.tryConsume();

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(rateLimiter, times(1)).acquirePermission();
    }
}