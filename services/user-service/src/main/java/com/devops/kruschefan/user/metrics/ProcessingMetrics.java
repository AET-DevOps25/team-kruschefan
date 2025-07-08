package com.devops.kruschefan.user.metrics;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ProcessingMetrics {
    private final Timer processingTimer;

    public ProcessingMetrics(MeterRegistry registry) {
        this.processingTimer = registry.timer("user_request_processing_duration");
    }

    public void processUserRequest() {
        processingTimer.record(() -> {
            try {
                Thread.sleep(500); // Simulate processing
            } catch (InterruptedException ignored) {}
        });
    }
}
