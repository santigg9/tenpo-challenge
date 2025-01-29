package com.tenpo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ExternalPercentageService {

    private final Logger logger = LoggerFactory.getLogger(ExternalPercentageService.class);

    public Mono<Double> getPercentage() {
        logger.info("Fetching percentage from external service");
        //return Mono.error(new RuntimeException("Simulated error for retry testing"));
        return Mono.just(10.0);
    }
}
