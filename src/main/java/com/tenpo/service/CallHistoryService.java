package com.tenpo.service;

import com.tenpo.controller.dto.CallHistoryDTO;
import com.tenpo.entity.CallHistory;
import com.tenpo.repository.CallHistoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CallHistoryService {

    private final CallHistoryRepository callHistoryRepository;

    public CallHistoryService(CallHistoryRepository callHistoryRepository) {
        this.callHistoryRepository = callHistoryRepository;
    }

    public Mono<Void> logCall(String endpoint, String parameters, String response) {
        CallHistory history = CallHistory.builder()
                .endpoint(endpoint)
                .parameters(parameters)
                .response(response)
                .build();
        return callHistoryRepository.save(history).then();
    }

    public Flux<CallHistoryDTO> getHistory(Pageable pageable) {
        return callHistoryRepository.findAllBy(pageable)
                .map(history -> CallHistoryDTO.builder()
                       .endpoint(history.getEndpoint())
                       .parameters(history.getParameters())
                       .response(history.getResponse())
                       .createdAt(history.getCreatedAt())
                       .build());
    }
}
