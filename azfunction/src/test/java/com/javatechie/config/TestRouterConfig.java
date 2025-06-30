package com.javatechie.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRouterConfig {

    @Bean
    public RouterConfig routerConfig() {
        return new RouterConfig();
    }
}
