package com.example.demodocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoDockerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoDockerApplication.class, args);
    }

}
