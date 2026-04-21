package com.example.cicd_demo_ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("order-service")
public interface OrderFeign {
    @GetMapping("/order/name")
    String name();
}
