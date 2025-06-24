package com.devops.kruschefan.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActiveUserGauge {
    private final AtomicInteger activeUsers = new AtomicInteger(0);

    public ActiveUserGauge(MeterRegistry registry) {
        Gauge.builder("active_users", activeUsers, AtomicInteger::get)
             .description("Currently active users")
             .register(registry);
    }

    public void increment() {
        activeUsers.incrementAndGet();
    }

    public void decrement() {
        activeUsers.decrementAndGet();
    }
}
