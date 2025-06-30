package com.javatechie.config;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationInsightsConfig {

    @Bean
    @ConditionalOnProperty(name = "azure.application-insights.enabled", havingValue = "true")
    public TelemetryClient telemetryClient() {
        return new TelemetryClient();
    }
}
