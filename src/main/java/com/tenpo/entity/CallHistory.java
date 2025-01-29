package com.tenpo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("call_history")
@Builder
@Data
public class CallHistory {

    @Id
    private Long id;
    private String endpoint;
    private String parameters;
    private String response;
    private LocalDateTime createdAt;

}
