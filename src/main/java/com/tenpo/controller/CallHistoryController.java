package com.tenpo.controller;

import com.tenpo.controller.dto.CallHistoryDTO;
import com.tenpo.exception.ErrorResponse;
import com.tenpo.exception.TooManyRequestsException;
import com.tenpo.service.CallHistoryService;
import com.tenpo.service.RateLimitingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
public class CallHistoryController {

    private final CallHistoryService callHistoryService;
    private final RateLimitingService rateLimitingService;

    public CallHistoryController(CallHistoryService callHistoryService, RateLimitingService rateLimitingService) {
        this.callHistoryService = callHistoryService;
        this.rateLimitingService = rateLimitingService;
    }

    @Operation(summary = "Get call history", description = "Retrieve the call history with pagination")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved call history",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CallHistoryDTO.class)))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/history")
    public Mono<List<CallHistoryDTO>> getHistory(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return rateLimitingService.tryConsume().flatMap(consumed -> {
            if (consumed) {
                Pageable pageable = PageRequest.of(page, size);
               return callHistoryService.getHistory(pageable).collectList();
            } else {
                return Mono.error(new TooManyRequestsException("Too many requests"));
            }
        });

    }


}