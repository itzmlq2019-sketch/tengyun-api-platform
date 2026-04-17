package com.example.tengyunapiinterface;

import com.example.tengyunapiclientsdk.client.TengyunApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.annotation.Resource;

@SpringBootTest
class TengyunApiInterfaceApplicationTests {

	// 🌟 见证奇迹的时刻：直接注入咱们自己写的 SDK 客户端！
	@Resource
	private TengyunApiClient tengyunApiClient;

	@Test
	void contextLoads() {
		// 使用 SDK 发起一次极速调用
		String result = tengyunApiClient.invokeTestApi("Bro");
		System.out.println("SDK 调用结果：" + result);
	}
}