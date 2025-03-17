package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MetricConfig.class, MetricConfigTest.TestConfig.class})
class MetricConfigTest {

    @Autowired
    private MeterRegistry testMeterRegistry;

    @Autowired
    private TimedAspect testTimedAspect;

    @Test
    void testMetricsCommonTagsBeanExists() {
        assertThat(testMeterRegistry).isNotNull();
        assertThat(testMeterRegistry).isInstanceOf(SimpleMeterRegistry.class);
    }

    @Test
    void testTimedAspectBeanExists() {
        assertThat(testTimedAspect).isNotNull();
    }

    @Test
    void testMetricRegistryHasApplicationTag() {
        testMeterRegistry.counter("test_metric", "application", "petclinic").increment();
        assertThat(testMeterRegistry.get("test_metric").tag("application", "petclinic").counter()).isNotNull();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "testMeterRegistry")
        public MeterRegistry testMeterRegistry() {
            return new SimpleMeterRegistry();
        }

        @Bean(name = "testTimedAspect")
        public TimedAspect testTimedAspect(MeterRegistry testMeterRegistry) {
            return new TimedAspect(testMeterRegistry);
        }
    }
}
