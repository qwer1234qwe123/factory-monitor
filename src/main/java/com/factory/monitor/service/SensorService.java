package com.factory.monitor.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.factory.monitor.dto.PredictResponse;
import com.factory.monitor.entity.SensorData;
import com.factory.monitor.repository.SensorDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorDataRepository sensorDataRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // FastAPI /status 호출
    public PredictResponse checkStatus(SensorData sensorData) {
        try {
            String url = aiServerUrl + "/status";
            Map<String, Object> request = Map.of(
                    "vibration_x", sensorData.getVibrationX(),
                    "vibration_y", sensorData.getVibrationY(),
                    "vibration_z", sensorData.getVibrationZ(),
                    "temperature", sensorData.getTemperature(),
                    "current", sensorData.getCurrent());
            return restTemplate.postForObject(url, request, PredictResponse.class);
        } catch (Exception e) {
            // FastAPI 장애 시 기본값 반환
            PredictResponse fallback = new PredictResponse();
            fallback.setStatus("알수없음");
            fallback.setMessage("AI 서버 연결 실패: " + e.getMessage());
            return fallback;
        }
    }

    // 센서 데이터 저장 (FastAPI 결과 포함)
    public SensorData save(SensorData sensorData) {
        PredictResponse result = checkStatus(sensorData);
        sensorData.setStatus(result.getStatus());
        sensorData.setMessage(result.getMessage());
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