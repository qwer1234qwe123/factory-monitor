package com.factory.monitor.dto;

import lombok.Data;

@Data
public class PredictResponse {
    private Double rul;
    private String status;
    private String message;
}