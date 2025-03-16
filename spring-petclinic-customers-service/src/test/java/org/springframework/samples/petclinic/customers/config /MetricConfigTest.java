package org.springframework.samples.petclinic.customers.config;

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
    private MeterRegistry meterRegistry;

    @Autowired
    private TimedAspect timedAspect;

    @Test
    void testMetricsCommonTagsBeanExists() {
        assertThat(meterRegistry).isNotNull();
        assertThat(meterRegistry).isInstanceOf(SimpleMeterRegistry.class);
        assertThat(meterRegistry.config().commonTags()).isNotEmpty(); // Kiểm tra commonTags có được set không
    }

    @Test
    void testTimedAspectBeanExists() {
        assertThat(timedAspect).isNotNull();
    }

    @Test
    void testMetricConfig_BeanCreation() {
        MetricConfig metricConfig = new MetricConfig();
        MeterRegistry registry = new SimpleMeterRegistry();
        
        // Kiểm tra bean metricsCommonTags()
        MeterRegistryCustomizer<MeterRegistry> customizer = metricConfig.metricsCommonTags();
        customizer.customize(registry);
        assertThat(registry.config().commonTags()).contains("application", "petclinic");

        // Kiểm tra bean timedAspect()
        TimedAspect aspect = metricConfig.timedAspect(registry);
        assertThat(aspect).isNotNull();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
}
