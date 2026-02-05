package com.oort.weichat.ui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 完整的MD5工具类，用于生成字符串的MD5哈希值（32位）
 */
public class MD5Util {

    /**
     * 生成字符串的MD5哈希值（32位小写）
     * @param input 原始字符串（可为null或空）
     * @return MD5哈希值（若输入为空则返回空字符串）
     */
    public static String md5(String input) {
        // 处理空输入
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        try {
            // 获取MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算哈希值（字节数组）
            byte[] messageDigest = md.digest(input.getBytes());
            // 转换为16进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                // 将字节转为16进制（确保两位，不足补0）
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 理论上不会走到这里（MD5是标准算法）
            e.printStackTrace();
            return "";
        }
    }
}