package com.javatechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.javatechie")
public class AzureFunctionsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AzureFunctionsApplication.class, args);
    }
}
