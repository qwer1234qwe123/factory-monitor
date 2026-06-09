package com.factory.monitor.repository;

import com.factory.monitor.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    // 최근 50개 데이터
    List<SensorData> findTop50ByOrderByRecordedAtDesc();

    // 특정 디바이스 최근 데이터
    List<SensorData> findTop30ByDeviceIdOrderByRecordedAtDesc(String deviceId);

    // 최신 1개
    SensorData findTopByOrderByRecordedAtDesc();
}
