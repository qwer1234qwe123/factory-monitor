package com.factory.monitor.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.factory.monitor.entity.SensorData;
import com.factory.monitor.service.SensorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    // 대시보드 메인 페이지
    @GetMapping("/")
    public String dashboard(Model model) {
        List<SensorData> recentData = sensorService.getRecent();
        SensorData latest = sensorService.getLatest();
        model.addAttribute("recentData", recentData);
        model.addAttribute("latest", latest);
        return "dashboard";
    }

    // 센서 데이터 수신 + AI 예측 + DB 저장 (라즈베리파이에서 호출)
    @PostMapping("/api/sensor")
    @ResponseBody
    public ResponseEntity<SensorData> receiveSensorData(@RequestBody SensorData sensorData) {
        return ResponseEntity.ok(sensorService.save(sensorData));
    }

    // 최근 데이터 조회 API (대시보드 실시간 업데이트용)
    @GetMapping("/api/sensor/recent")
    @ResponseBody
    public ResponseEntity<List<SensorData>> getRecent() {
        return ResponseEntity.ok(sensorService.getRecent());
    }

    // 최신 상태 조회
    @GetMapping("/api/sensor/latest")
    @ResponseBody
    public ResponseEntity<SensorData> getLatest() {
        return ResponseEntity.ok(sensorService.getLatest());
    }

    // Gemini 리포트 프록시
    @PostMapping("/api/report")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(sensorService.generateReport(payload));
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(sensorService.chat(payload));
    }
}