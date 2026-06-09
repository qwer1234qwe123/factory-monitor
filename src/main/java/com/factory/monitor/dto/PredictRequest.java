package com.factory.monitor.dto;

import lombok.Data;
import java.util.List;

@Data
public class PredictRequest {
    private List<List<Double>> sensorValues;
}