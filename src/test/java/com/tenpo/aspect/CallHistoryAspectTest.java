package com.tenpo.aspect;

import com.tenpo.service.CallHistoryService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CallHistoryAspectTest {

    @Mock
    private CallHistoryService callHistoryService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private CallHistoryAspect callHistoryAspect;

    private final Logger logger = LoggerFactory.getLogger(CallHistoryAspectTest.class);

    @BeforeEach
    public void setUp() {
        reset(callHistoryService, proceedingJoinPoint, signature);
    }

    @Test
    public void testLogCall_MonoSuccess() throws Throwable {
        String endpoint = "TestController.testMethod()";
        String parameters = "param1 param2";
        String response = "SuccessResponse";

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(endpoint);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"param1", "param2"});
        when(proceedingJoinPoint.proceed()).thenReturn(Mono.just(response));
        when(callHistoryService.logCall(endpoint, parameters, "Response: " + response))
                .thenReturn(Mono.empty());

        Object result = callHistoryAspect.logCall(proceedingJoinPoint);

        assertNotNull(result);
        assertInstanceOf(Mono.class, result);

        ((Mono<?>) result).block();

        verify(proceedingJoinPoint, times(1)).proceed();
        verify(callHistoryService, times(1)).logCall(endpoint, parameters, "Response: " + response);
    }

    @Test
    public void testLogCall_MonoError() throws Throwable {
        String endpoint = "TestController.testMethod()";
        String parameters = "param1 param2";
        Throwable error = new RuntimeException("Test error");

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(endpoint);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"param1", "param2"});
        when(proceedingJoinPoint.proceed()).thenReturn(Mono.error(error));
        when(callHistoryService.logCall(endpoint, parameters, "Error: " + error.getMessage()))
                .thenReturn(Mono.empty());

        Object result = callHistoryAspect.logCall(proceedingJoinPoint);

        assertNotNull(result);
        assertInstanceOf(Mono.class, result);

        ((Mono<?>) result).onErrorResume(e -> Mono.empty()).block();

        verify(proceedingJoinPoint, times(1)).proceed();
        verify(callHistoryService, times(1)).logCall(endpoint, parameters, "Error: " + error.getMessage());
    }

    @Test
    public void testLogCall_NonReactiveResult() throws Throwable {
        String endpoint = "TestController.testMethod()";
        String parameters = "param1 param2";
        String response = "NonReactiveResponse";

        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(endpoint);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"param1", "param2"});
        when(proceedingJoinPoint.proceed()).thenReturn(response);
        when(callHistoryService.logCall(endpoint, parameters, "Response: " + response))
                .thenReturn(Mono.empty());

        Object result = callHistoryAspect.logCall(proceedingJoinPoint);

        assertNotNull(result);
        assertEquals(response, result);

        verify(proceedingJoinPoint, times(1)).proceed();
        verify(callHistoryService, times(1)).logCall(endpoint, parameters, "Response: " + response);
    }
}