package com.tenpo.controller;

import com.tenpo.exception.ErrorResponse;
import com.tenpo.exception.TooManyRequestsException;
import com.tenpo.service.CalculatorService;
import com.tenpo.service.RateLimitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final RateLimitingService rateLimitingService;

    public CalculatorController(CalculatorService calculatorService, RateLimitingService rateLimitingService) {
        this.calculatorService = calculatorService;
        this.rateLimitingService = rateLimitingService;
    }

    @GetMapping("/calculate")
    @Operation(summary = "Calculates the sum of two numbers and applies a dynamic percentage",
            description = "Receives two numbers (num1 and num2) and calculates the sum with a dynamic percentage applied.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculation successful",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "4XX", description = "4XX errors",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Mono<ResponseEntity<Double>> calculate(@RequestParam double num1,@RequestParam double num2) {
        return rateLimitingService.tryConsume().flatMap(consumed -> {
            if (consumed) {
                return calculatorService.calculateWithDynamicPercentage(num1, num2)
                        .map(ResponseEntity::ok);
            } else {
                return Mono.error(new TooManyRequestsException("Too many requests"));
            }
        });
    }

}