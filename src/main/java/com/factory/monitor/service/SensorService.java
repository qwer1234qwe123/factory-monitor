package com.factory.monitor.service;

import com.factory.monitor.dto.PredictRequest;
import com.factory.monitor.dto.PredictResponse;
import com.factory.monitor.entity.SensorData;
import com.factory.monitor.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorDataRepository sensorDataRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // FastAPI 호출해서 RUL 예측
    public PredictResponse predict(PredictRequest request) {
        String url = aiServerUrl + "/predict";
        return restTemplate.postForObject(url, request, PredictResponse.class);
    }

    // 센서 데이터 저장
    public SensorData save(SensorData sensorData) {
        return sensorDataRepository.save(sensorData);
    }

    // 최근 50개 조회
    public List<SensorData> getRecent() {
        return sensorDataRepository.findTop50ByOrderByRecordedAtDesc();
    }

    // 최신 1개 조회
    public SensorData getLatest() {
        return sensorDataRepository.findTopByOrderByRecordedAtDesc();
    }
}