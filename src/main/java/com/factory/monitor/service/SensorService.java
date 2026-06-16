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
        // 1. 임계값 기반 상태 판정
        PredictResponse statusResult = checkStatus(sensorData);

        // 2. ONNX 이상탐지 점수
        PredictResponse predictResult = callPredict(sensorData);

        // 3. 둘 중 더 심각한 상태 채택
        String finalStatus = mergeStatus(statusResult.getStatus(), predictResult.getStatus());
        String finalMessage = buildMessage(statusResult, predictResult);

        sensorData.setStatus(finalStatus);
        sensorData.setMessage(finalMessage);
        return sensorDataRepository.save(sensorData);
    }

    private PredictResponse callPredict(SensorData sensorData) {
        try {
            String url = aiServerUrl + "/predict";
            Map<String, Object> request = Map.of(
                    "vibration_x", sensorData.getVibrationX(),
                    "vibration_y", sensorData.getVibrationY(),
                    "vibration_z", sensorData.getVibrationZ(),
                    "temperature", sensorData.getTemperature(),
                    "current", sensorData.getCurrent());
            return restTemplate.postForObject(url, request, PredictResponse.class);
        } catch (Exception e) {
            PredictResponse fallback = new PredictResponse();
            fallback.setStatus("정상");
            fallback.setMessage("");
            return fallback;
        }
    }

    private String mergeStatus(String s1, String s2) {
        if ("위험".equals(s1) || "위험".equals(s2))
            return "위험";
        if ("경고".equals(s1) || "경고".equals(s2))
            return "경고";
        return "정상";
    }

    private String buildMessage(PredictResponse status, PredictResponse predict) {
        StringBuilder sb = new StringBuilder();
        if (status.getMessage() != null && !status.getMessage().isBlank()
                && !"정상 가동 중".equals(status.getMessage())) {
            sb.append(status.getMessage());
        }
        if (predict.getMessage() != null && !predict.getMessage().isBlank()) {
            if (sb.length() > 0)
                sb.append(" / ");
            sb.append(predict.getMessage());
        }
        return sb.length() > 0 ? sb.toString() : "정상 가동 중";
    }

    // 최근 50개 조회
    public List<SensorData> getRecent() {
        return sensorDataRepository.findTop50ByOrderByRecordedAtDesc();
    }

    // 최신 1개 조회
    public SensorData getLatest() {
        return sensorDataRepository.findTopByOrderByRecordedAtDesc();
    }

    // Gemini 리포트 생성 (FastAPI /report 프록시)
    public Map<String, Object> generateReport(Map<String, Object> payload) {
        try {
            String url = aiServerUrl + "/report";
            return restTemplate.postForObject(url, payload, Map.class);
        } catch (Exception e) {
            return Map.of("error", "리포트 생성 실패: " + e.getMessage());
        }
    }

    public Map<String, Object> chat(Map<String, Object> payload) {
        try {
            String url = aiServerUrl + "/chat";
            return restTemplate.postForObject(url, payload, Map.class);
        } catch (Exception e) {
            return Map.of("error", "채팅 실패: " + e.getMessage());
        }
    }
}