package com.devops.kruschefan.metrics;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class PayloadMetrics {
    private final DistributionSummary payloadSizeSummary;

    public PayloadMetrics(MeterRegistry registry) {
        this.payloadSizeSummary = DistributionSummary.builder("payload_size_bytes")
            .description("Payload sizes of user service requests")
            .register(registry);
    }

    public void recordPayloadSize(int size) {
        payloadSizeSummary.record(size);
    }
}
