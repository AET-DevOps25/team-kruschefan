package com.devops.kruschefan.user.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class LoginMetrics {
    private final Counter loginCounter;

    public LoginMetrics(MeterRegistry registry) {
        this.loginCounter = registry.counter("user_logins_total");
    }

    public void recordLogin() {
        loginCounter.increment();
    }
}
