package com.tenpo.aspect;

import com.tenpo.service.CallHistoryService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Aspect
@Order(-1)
public class CallHistoryAspect {

    private final CallHistoryService callHistoryService;
    private final Logger logger = LoggerFactory.getLogger(CallHistoryAspect.class);

    public CallHistoryAspect(CallHistoryService callHistoryService) {
        this.callHistoryService = callHistoryService;
    }

    @Pointcut("execution(public * com.tenpo.controller..*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logCall(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String endpoint = proceedingJoinPoint.getSignature().toShortString();
        String parameters = extractParameters(proceedingJoinPoint);

        logger.info("Intercepted call to endpoint: {}, with parameters: {}", endpoint, parameters);

        try {
            Object result = proceedingJoinPoint.proceed();

            if (result instanceof Mono) {
                return ((Mono<?>) result)
                        .doOnSuccess(response -> {
                            logger.info("Logging successful response for endpoint: {}", endpoint);
                            logResponse(endpoint, parameters, response.toString());
                        })
                        .doOnError(error -> {
                            logger.error("Logging error for endpoint: {}", endpoint, error);
                            logError(endpoint, parameters, error);
                        });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result)
                        .doOnNext(response -> {
                            logger.info("Logging response for endpoint: {}", endpoint);
                            logResponse(endpoint, parameters, response.toString());
                        })
                        .doOnError(error -> {
                            logger.error("Logging error for endpoint: {}", endpoint, error);
                            logError(endpoint, parameters, error);
                        });
            } else {
                logger.info("Non-reactive result for endpoint: {}", endpoint);
                logResponse(endpoint, parameters, result != null ? result.toString() : "null");
                return result;
            }
        } catch (Throwable throwable) {
            logger.error("Error during aspect processing for endpoint: {}", endpoint, throwable);
            logError(endpoint, parameters, throwable);
            throw throwable;
        }
    }

    private String extractParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder params = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                params.append(arg.toString()).append(" ");
            } else {
                params.append("null ");
            }
        }
        return params.toString().trim();
    }

    private void logResponse(String endpoint, String parameters, String responseStr) {
        callHistoryService.logCall(endpoint, parameters, "Response: " + responseStr)
                .subscribe(
                        success -> logger.info("Call history logged successfully for endpoint: {}", endpoint),
                        error -> logger.error("Failed to log call history for endpoint: {}", endpoint, error)
                );
    }

    private void logError(String endpoint, String parameters, Throwable error) {
        callHistoryService.logCall(endpoint, parameters, "Error: " + error.getMessage())
                .subscribe(
                        success -> logger.info("Error logged successfully for endpoint: {}", endpoint),
                        logError -> logger.error("Failed to log error for endpoint: {}", endpoint, logError)
                );
    }
}