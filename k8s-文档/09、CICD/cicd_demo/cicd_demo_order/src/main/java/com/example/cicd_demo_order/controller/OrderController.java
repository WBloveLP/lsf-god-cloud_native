package com.example.cicd_demo_order.controller;

import com.example.cicd_demo_order.feign.WareFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private WareFeign wareFeign;

    @GetMapping("/name")
    public String name(){
        return "I am Order";
    }

    @GetMapping("/ware")
    public String ware(){
        String name = wareFeign.name();
        return "I am OrderController; 我调用了库存：" + name;
    }

}
