package com.example.tengyunapibackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.tengyunapibackend.mapper") // 🌟 加上这行扫描 Mapper
@EnableDubbo
public class TengyunApiBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(TengyunApiBackendApplication.class, args);
	}
}
