package com.example.tengyunapiinterface.controller;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest; // 注意：如果是 SpringBoot 2.x 用 javax.servlet

@RestController
@RequestMapping("/api/name")
public class NameController {

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody String name, HttpServletRequest request) {
        // 从请求头中获取咱们 SDK 自动塞进去的加密参数
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");

        // 打印出来，让咱们看看 SDK 的威力！
        System.out.println("====== 收到来自 SDK 的加密请求 ======");
        System.out.println("accessKey = " + accessKey);
        System.out.println("nonce = " + nonce);
        System.out.println("timestamp = " + timestamp);
        System.out.println("sign = " + sign);
        System.out.println("body = " + body);
        System.out.println("=====================================");

        return "POST 接口调用成功！你的名字是：" + name;
    }
}