package com.factory.monitor.dto;

import java.util.List;

import lombok.Data;

@Data
public class PredictResponse {
    private String status;
    private String message;
    private List<String> warnings;
}