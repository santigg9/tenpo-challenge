package com.tenpo.repository;

import com.tenpo.entity.CallHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface CallHistoryRepository extends ReactiveCrudRepository<CallHistory, Long> {
    Flux<CallHistory> findAllBy(Pageable pageable);
}