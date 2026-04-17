package com.example.tengyunapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * API 签名工具类
 * 核心逻辑：将请求体和秘钥混合后，进行 SHA256 不可逆加密
 */
public class SignUtils {

    /**
     * 生成签名
     * @param body 请求体内容 (比如 JSON 字符串)
     * @param secretKey 用户的私钥
     * @return 生成的签名字符串
     */
    public static String getSign(String body, String secretKey) {
        // 使用 SHA256 算法，比 MD5 更安全，是目前大厂的主流选择
        Digester digester = new Digester(DigestAlgorithm.SHA256);

        // 将请求体和私钥拼接在一起（这里加个小数点作为分隔符，增加破解难度）
        String content = body + "." + secretKey;

        // 计算并返回十六进制的加密字符串
        return digester.digestHex(content);
    }
}