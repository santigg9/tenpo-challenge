package com.tenpo.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CallHistoryDTO {
    private String endpoint;
    private String parameters;
    private String response;
    private LocalDateTime createdAt;

}