package com.javatechie.config;

import com.javatechie.CourseManagementApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

@Configuration
@Import(CourseManagementApplication.class)
public class AzureFunctionConfig {

    @Bean
    public GenericApplicationContext genericApplicationContext() {
        return new GenericApplicationContext();
    }
}
