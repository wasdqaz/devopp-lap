package org.springframework.samples.petclinic.customers.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import static org.junit.jupiter.api.Assertions.*;

class MetricConfigTest {

    @Test
    void shouldCreateMetricsCommonTagsCustomizer() {
        MetricConfig config = new MetricConfig();
        MeterRegistry registry = new SimpleMeterRegistry();

        MeterRegistryCustomizer<MeterRegistry> customizer = config.metricsCommonTags();
        assertNotNull(customizer);

        // Gọi để đảm bảo lambda được thực thi
        customizer.customize(registry);
        assertTrue(registry.get("application").tag("application", "petclinic") != null);
    }

    @Test
    void shouldCreateTimedAspect() {
        MetricConfig config = new MetricConfig();
        MeterRegistry registry = new SimpleMeterRegistry();

        TimedAspect aspect = config.timedAspect(registry);
        assertNotNull(aspect);
    }
}
