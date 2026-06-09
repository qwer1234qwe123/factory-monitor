package com.factory.monitor.controller;

import com.factory.monitor.dto.PredictRequest;
import com.factory.monitor.dto.PredictResponse;
import com.factory.monitor.entity.SensorData;
import com.factory.monitor.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}