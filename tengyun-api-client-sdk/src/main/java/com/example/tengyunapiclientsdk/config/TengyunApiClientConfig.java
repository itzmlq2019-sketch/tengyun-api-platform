package com.example.tengyunapiclientsdk.config;

import com.example.tengyunapiclientsdk.client.TengyunApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("tengyun.client") // 自动读取 yml 里 tengyun.client 开头的配置
@Data
@ComponentScan
public class TengyunApiClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public TengyunApiClient tengyunApiClient() {
        // 读取配置后，自动帮你 new 一个客户端放进 Spring 容器
        return new TengyunApiClient(accessKey, secretKey);
    }
}