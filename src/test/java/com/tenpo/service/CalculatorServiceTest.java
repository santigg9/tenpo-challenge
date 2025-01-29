package com.tenpo.service;

import com.tenpo.exception.NoValidCachedPercentageException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculatorServiceTest {

    @Mock
    private ExternalPercentageService externalPercentageService;

    @Mock
    private RedisService redisService;

    @Mock
    private RetryRegistry retryRegistry;

    @Mock
    private Retry retry;

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCalculateWithDynamicPercentage() {
        double num1 = 10.0;
        double num2 = 20.0;
        double percentage = 10.0;
        double expectedResult = (num1 + num2) * (1 + percentage / 100);
        when(retryRegistry.retry(anyString())).thenReturn(Retry.of("externalPercentageServiceRetry", RetryConfig.custom().maxAttempts(1).build()));
        when(redisService.getKey(anyString())).thenReturn(Mono.empty());
        when(externalPercentageService.getPercentage()).thenReturn(Mono.just(percentage));
        when(redisService.setKey(anyString(), anyDouble())).thenReturn(Mono.just(true));

        Mono<Double> result = calculatorService.calculateWithDynamicPercentage(num1, num2);

        StepVerifier.create(result)
                .expectNext(expectedResult)
                .verifyComplete();

        verify(redisService, times(1)).getKey(anyString());
        verify(externalPercentageService, times(1)).getPercentage();
        verify(redisService, times(1)).setKey(anyString(), anyDouble());
    }

    @Test
    void testGetPercentageWithCachedValue() {
        when(retryRegistry.retry(anyString())).thenReturn(retry);

        double cachedPercentage = 15.0;

        when(redisService.getKey(anyString())).thenReturn(Mono.just(cachedPercentage));

        Mono<Double> result = calculatorService.getPercentage();

        StepVerifier.create(result)
                .expectNext(cachedPercentage)
                .verifyComplete();

        verify(redisService, times(1)).getKey(anyString());
        verify(externalPercentageService, never()).getPercentage();
        verify(redisService, never()).setKey(anyString(), anyDouble());
    }

    @Test
    void testGetPercentageWithNoCachedValue() {
        double percentage = 20.0;

        when(redisService.getKey(anyString())).thenReturn(Mono.empty());
        when(retryRegistry.retry(anyString())).thenReturn(Retry.of("externalPercentageServiceRetry", RetryConfig.custom().maxAttempts(1).build()));
        when(externalPercentageService.getPercentage()).thenReturn(Mono.just(percentage));
        when(redisService.setKey(anyString(), eq(percentage))).thenReturn(Mono.just(true));


        Mono<Double> result = calculatorService.getPercentage();


        StepVerifier.create(result)
                .expectNext(percentage)
                .verifyComplete();


        verify(redisService, times(1)).getKey(anyString());
        verify(externalPercentageService, times(1)).getPercentage();
        verify(redisService, times(1)).setKey(anyString(), eq(percentage));
    }

    @Test
    void testGetPercentageWithError() {
        when(redisService.getKey(anyString())).thenReturn(Mono.empty());
        when(externalPercentageService.getPercentage()).thenReturn(Mono.error(new RuntimeException("Service down")));

        Mono<Double> result = calculatorService.getPercentage();

        StepVerifier.create(result)
                .expectError(NoValidCachedPercentageException.class)
                .verify();

        verify(redisService, times(1)).getKey(anyString());
        verify(externalPercentageService, times(1)).getPercentage();
        verify(redisService, never()).setKey(anyString(), anyDouble());
    }
}