package com.example.cicd_demo_order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("ware-service")
public interface WareFeign {
    @GetMapping("/ware/name")
    String name();
}
