package com.tenpo.service;

import com.tenpo.exception.NoValidCachedPercentageException;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class CalculatorService {

    private final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    private static final String PERCENTAGE_CACHE_KEY = "percentageCache:percentage";
    private final ExternalPercentageService externalPercentageService;
    private final RedisService redisService;
    private final RetryRegistry retryRegistry;


    public CalculatorService(ExternalPercentageService externalPercentageService, RedisService redisService, RetryRegistry retryRegistry) {
        this.externalPercentageService = externalPercentageService;
        this.redisService = redisService;
        this.retryRegistry = retryRegistry;
    }

    public Mono<Double> calculateWithDynamicPercentage(double num1, double num2) {
        return getPercentage()
                .map(percentage -> {
                    double sum = num1 + num2;
                    return sum + (sum * (percentage / 100));
                });
    }

    public Mono<Double> getPercentage() {
        Retry retry = retryRegistry.retry("externalPercentageServiceRetry");

        return redisService.getKey(PERCENTAGE_CACHE_KEY)
                .flatMap(
                        value -> {
                            if (value != null) {
                                logger.info("Cached Value: {}", value);
                                return Mono.just((Double) value);
                            }
                            return Mono.empty();
                        }
                )
                .switchIfEmpty(
                        Mono.defer(() ->
                                externalPercentageService.getPercentage()
                                        .transform(RetryOperator.of(retry))
                                        .flatMap(value ->
                                                redisService.setKey(PERCENTAGE_CACHE_KEY, value)
                                                        .thenReturn(value)
                                        ))
                )
                .onErrorResume(e ->
                        Mono.error(new NoValidCachedPercentageException("No valid cached percentage available"))
                );
    }
}
