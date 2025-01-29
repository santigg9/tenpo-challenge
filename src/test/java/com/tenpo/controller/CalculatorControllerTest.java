package com.tenpo.controller;

import com.tenpo.exception.TooManyRequestsException;
import com.tenpo.service.CalculatorService;
import com.tenpo.service.RateLimitingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculatorControllerTest {

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private RateLimitingService rateLimitingService;

    @InjectMocks
    private CalculatorController calculatorController;

    @BeforeEach
    void setUp() {}

    @Test
    void testCalculate_Success() {
        double num1 = 10.0;
        double num2 = 20.0;
        double expectedResult = 33.0;

        when(rateLimitingService.tryConsume()).thenReturn(Mono.just(true));
        when(calculatorService.calculateWithDynamicPercentage(num1, num2)).thenReturn(Mono.just(expectedResult));

        Mono<ResponseEntity<Double>> result = calculatorController.calculate(num1, num2);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    return response.getStatusCode() == HttpStatus.OK &&
                            response.getBody() != null &&
                            response.getBody().equals(expectedResult);
                })
                .verifyComplete();
    }

    @Test
    void testCalculate_TooManyRequests() {
        double num1 = 10.0;
        double num2 = 20.0;

        when(rateLimitingService.tryConsume()).thenReturn(Mono.just(false));

        Mono<ResponseEntity<Double>> result = calculatorController.calculate(num1, num2);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof TooManyRequestsException &&
                        throwable.getMessage().equals("Too many requests"))
                .verify();
    }
}