package com.factory.monitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private Double vibrationX;
    private Double vibrationY;
    private Double vibrationZ;
    private Double temperature;
    private Double current;
    private Double rul;
    private String status;
    private String message;

    @Column(updatable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        this.recordedAt = LocalDateTime.now();
    }
}