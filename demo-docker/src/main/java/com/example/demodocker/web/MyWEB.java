package com.example.demodocker.web;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RequestMapping("/testDocker")
@RestController
public class MyWEB {
    @GetMapping("/666")
    public Data test() {
        return new Data("xxx水电费撒发生", "msg", 200);
    }


    static class Data {
        private String data;
        private String msg;
        private Integer code;

        public Data(String data, String msg, Integer code) {
            this.data = data;
            this.msg = msg;
            this.code = code;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }
}
