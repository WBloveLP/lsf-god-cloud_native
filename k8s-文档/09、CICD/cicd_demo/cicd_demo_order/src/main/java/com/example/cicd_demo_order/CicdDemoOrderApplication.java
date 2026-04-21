package com.example.cicd_demo_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CicdDemoOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CicdDemoOrderApplication.class, args);
    }

}
