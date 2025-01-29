package com.tenpo.service;

import com.tenpo.controller.dto.CallHistoryDTO;
import com.tenpo.entity.CallHistory;
import com.tenpo.repository.CallHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CallHistoryServiceTest {

    @Mock
    private CallHistoryRepository callHistoryRepository;

    @InjectMocks
    private CallHistoryService callHistoryService;

    @Test
    public void testLogCall() {
        CallHistory history = CallHistory.builder()
                .endpoint("/test")
                .parameters("param1=value1")
                .response("response")
                .build();

        when(callHistoryRepository.save(any(CallHistory.class))).thenReturn(Mono.just(history));

        Mono<Void> result = callHistoryService.logCall("/test", "param1=value1", "response");

        StepVerifier.create(result)
                .verifyComplete();

        verify(callHistoryRepository, times(1)).save(any(CallHistory.class));
    }

    @Test
    public void testGetHistory() {
        CallHistory history = CallHistory.builder()
                .endpoint("/test")
                .parameters("param1=value1")
                .response("response")
                .createdAt(LocalDateTime.now())
                .build();

        when(callHistoryRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(history));

        Flux<CallHistoryDTO> result = callHistoryService.getHistory(Pageable.unpaged());

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getEndpoint().equals("/test") &&
                        dto.getParameters().equals("param1=value1") &&
                        dto.getResponse().equals("response")
                )
                .verifyComplete();

        verify(callHistoryRepository, times(1)).findAllBy(any(Pageable.class));
    }
}