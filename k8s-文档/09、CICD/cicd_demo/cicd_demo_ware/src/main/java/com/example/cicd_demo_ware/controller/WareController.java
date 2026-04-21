package com.example.cicd_demo_ware.controller;

import com.example.cicd_demo_ware.feign.OrderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ware")
public class WareController {

    @Autowired
    private OrderFeign orderFeign;

    @GetMapping("/name")
    public String name(){
        return "I am Ware";
    }

    @GetMapping("/order")
    public String order(){
        String name = orderFeign.name();
        return "I am WareController; 我调用了订单：" + name;
    }

}
