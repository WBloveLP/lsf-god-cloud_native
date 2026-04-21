package com.example.cicd_demo_ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class CicdDemoWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(CicdDemoWareApplication.class, args);
    }

}
