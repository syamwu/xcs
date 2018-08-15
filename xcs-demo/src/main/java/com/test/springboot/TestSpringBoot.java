package com.test.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.test.springboot"})
public class TestSpringBoot {

    public static void main(String[] args) {
        SpringApplication bootstrap = new SpringApplication(TestSpringBoot.class);
        bootstrap.run(args);
    }
 
}
