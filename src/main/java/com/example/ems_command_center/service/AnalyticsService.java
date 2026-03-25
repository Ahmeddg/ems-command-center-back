package com.example.ems_command_center.service;

import com.example.ems_command_center.model.Analytics;
import com.example.ems_command_center.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public Optional<Analytics> getByType(String type) {
        return analyticsRepository.findByType(type);
    }

    public List<Map<String, Object>> getDispatchVolume() {
        return analyticsRepository.findByType("dispatch_volume")
                .map(Analytics::data)
                .orElse(List.of());
    }

    public List<Map<String, Object>> getResponseTimeData() {
        return analyticsRepository.findByType("response_time")
                .map(Analytics::data)
                .orElse(List.of());
    }

    public Analytics save(Analytics analytics) {
        return analyticsRepository.save(analytics);
    }
}
